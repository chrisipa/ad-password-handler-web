package de.papke.ad.password.handler.web.service;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import de.papke.ad.password.handler.web.model.ActiveDirectoryUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActiveDirectoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveDirectoryService.class);

    private static final String USER_PRINCIPAL_NAME = "userPrincipalName";
    private static final String SAM_ACCOUNT_NAME = "sAMAccountName";
    private static final String MAIL = "mail";
    private static final String NAME = "name";
    private static final String PWD_LAST_SET = "pwdLastSet"; // NOSONAR
    private static final String MAX_PWD_AGE = "maxPwdAge"; // NOSONAR
    private static final String OBJECT_CLASS = "objectClass";
    private static final String DOMAIN = "domain";
    private static final long TIMESTAMP_DIFFERENCE = 11644473600000L;

    @Value("${ad.server.host}")
    private String host;

    @Value("${ad.server.port}")
    private int port;

    @Value("${ad.server.base.dn}")
    private String baseDn;

    @Value("${ad.server.user.dn}")
    private String userDn;

    @Value("${ad.server.user.secret}")
    private String userSecret;

    @Value("${ad.server.elements.per.page}")
    private int elementsPerPage;

    public String getSAMAccountName(String userPrincipalName) {

        String sAMAccountName = null;
        LDAPConnection connection = null;

        try {
            connection = getLdapConnection();
            String[] attributes = new String[] { SAM_ACCOUNT_NAME };
            Filter filter = Filter.createEqualityFilter(USER_PRINCIPAL_NAME, userPrincipalName);

            for (SearchResultEntry searchResultEntry : search(filter, attributes)) {
                sAMAccountName = searchResultEntry.getAttributeValue(SAM_ACCOUNT_NAME);
                break;
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }

        return sAMAccountName;
    }

    public long getPasswordLastSet(String sAMAccountName) {

        long pwdLastSet = -1;
        LDAPConnection connection = null;

        try {
            connection = getLdapConnection();
            String[] attributes = new String[] { PWD_LAST_SET };
            Filter filter = Filter.createEqualityFilter(SAM_ACCOUNT_NAME, sAMAccountName);

            for (SearchResultEntry searchResultEntry : search(filter, attributes)) {
                Attribute pwdLastSetAttribute = searchResultEntry.getAttribute(PWD_LAST_SET);
                pwdLastSet = pwdLastSetAttribute.getValueAsLong();
                break;
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }

        return pwdLastSet;
    }

    public long getMaxPasswordAge() {

        long maxPwdAge = -1;
        LDAPConnection connection = null;

        try {
            connection = getLdapConnection();
            String[] attributes = new String[] { MAX_PWD_AGE };
            Filter filter = Filter.createEqualityFilter(OBJECT_CLASS, DOMAIN);

            for (SearchResultEntry searchResultEntry : search(filter, attributes)) {
                Attribute maxPwdAgeAttribute = searchResultEntry.getAttribute(MAX_PWD_AGE);
                maxPwdAge = maxPwdAgeAttribute.getValueAsLong();
                break;
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }

        return maxPwdAge;
    }

    public List<ActiveDirectoryUser> getUserList(String userFilter) {

        List<ActiveDirectoryUser> userList = new ArrayList<>();

        try {
            for (SearchResultEntry entry : search(userFilter)) {

                ActiveDirectoryUser user = new ActiveDirectoryUser();
                user.setsAMAccountName(entry.getAttributeValue(SAM_ACCOUNT_NAME));
                user.setUserPrincipalName(entry.getAttributeValue(USER_PRINCIPAL_NAME));
                user.setMail(entry.getAttributeValue(MAIL));
                user.setName(entry.getAttributeValue(NAME));

                userList.add(user);
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return userList;
    }

    public List<SearchResultEntry> search(String filterString) throws Exception {
        return search(Filter.create(filterString), new String[]{});
    }

    public List<SearchResultEntry> search(Filter filter) {
        return search(filter, new String[]{});
    }

    public List<SearchResultEntry> search(String filterString, String[] attributes) throws Exception {
        return search(SearchScope.SUB, Filter.create(filterString), attributes);
    }

    public List<SearchResultEntry> search(Filter filter, String[] attributes) {
        return search(SearchScope.SUB, filter, attributes);
    }

    public List<SearchResultEntry> search(SearchScope searchScope, String filterString, String[] attributes) throws Exception {
        return search(baseDn, searchScope, Filter.create(filterString), attributes);
    }

    public List<SearchResultEntry> search(SearchScope searchScope, Filter filter, String[] attributes) {
        return search(baseDn, searchScope, filter, attributes);
    }

    public List<SearchResultEntry> search(String baseDn, SearchScope searchScope, String filterString, String[] attributes) throws Exception {
        return search(baseDn, searchScope, Filter.create(filterString), attributes, false);
    }

    public List<SearchResultEntry> search(String baseDn, SearchScope searchScope, Filter filter, String[] attributes) {
        return search(baseDn, searchScope, filter, attributes, false);
    }

    public List<SearchResultEntry> search(String baseDn, SearchScope searchScope, String filterString, String[] attributes, boolean paging) throws Exception {
        return search(baseDn, searchScope, Filter.create(filterString), attributes, paging);
    }

    public List<SearchResultEntry> search(String baseDn, SearchScope searchScope, Filter filter, String[] attributes, boolean paging) {

        List<SearchResultEntry> searchResultEntries = new ArrayList<>();
        LDAPConnection connection = null;

        try {

            connection = getLdapConnection();

            if (paging) {

                SearchRequest searchRequest = new SearchRequest(baseDn, searchScope, filter, attributes);
                ASN1OctetString cookie = null;

                do {

                    Control[] controls = new Control[1];
                    controls[0] = new SimplePagedResultsControl(elementsPerPage, cookie);
                    searchRequest.setControls(controls);
                    SearchResult searchResult = connection.search(searchRequest);

                    searchResultEntries.addAll(searchResult.getSearchEntries());

                    cookie = null;
                    for (Control control : searchResult.getResponseControls()) {
                        if (control instanceof SimplePagedResultsControl) {
                            SimplePagedResultsControl simplePagedResultsControl = (SimplePagedResultsControl) control;
                            cookie = simplePagedResultsControl.getCookie();
                        }
                    }

                } while ((cookie != null) && (cookie.getValueLength() > 0));
            }
            else {

                SearchResult searchResult = connection.search(baseDn, searchScope, filter, attributes);
                searchResultEntries = searchResult.getSearchEntries();
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }

        return searchResultEntries;
    }

    private LDAPConnection getLdapConnection() throws LDAPException {
        return new LDAPConnection(host, port, userDn, userSecret);
    }

    public long getTimestamp(long adTimestamp) {
        return (adTimestamp / 10000) - TIMESTAMP_DIFFERENCE;
    }
}
