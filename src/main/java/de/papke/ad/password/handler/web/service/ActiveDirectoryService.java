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

/**
 * Service class for accessing the active directory server via LDAP.
 *
 * @author Christoph Papke (info@papke.it)
 */
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

    /**
     * Method for getting the the sAMAccountName by userPrincipalName.
     *
     * @param userPrincipalName - the user principal name
     * @return sAMAccountName
     */
    public String getSAMAccountName(String userPrincipalName) {

        String sAMAccountName = null;
        LDAPConnection connection = null;

        try {

            // get connection to LDAP server
            connection = getLdapConnection();

            // specify return attributes and filter query
            String[] attributes = new String[] { SAM_ACCOUNT_NAME };
            Filter filter = Filter.createEqualityFilter(USER_PRINCIPAL_NAME, userPrincipalName);

            // execute LDAP query to get the sAMAccountName
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

    /**
     * Method for getting the timestamp of the date the user password was last set.
     *
     * @param sAMAccountName - sAMAccountName of the user
     * @return timestamp of the date the user password was last set
     */
    public long getPasswordLastSet(String sAMAccountName) {

        long pwdLastSet = -1;
        LDAPConnection connection = null;

        try {

            // get connection to LDAP server
            connection = getLdapConnection();

            // specify return attributes and filter query
            String[] attributes = new String[] { PWD_LAST_SET };
            Filter filter = Filter.createEqualityFilter(SAM_ACCOUNT_NAME, sAMAccountName);

            // execute LDAP query to get the pwdLastSet
            for (SearchResultEntry searchResultEntry : search(filter, attributes)) {
                pwdLastSet = searchResultEntry.getAttributeValueAsLong(PWD_LAST_SET);
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

    /**
     * Method for getting the max age of a password.
     *
     * If this age is expired, the password has to be changed.
     *
     * @return timestamp of the max password age
     */
    public long getMaxPasswordAge() {

        long maxPwdAge = -1;
        LDAPConnection connection = null;

        try {

            // get connection to LDAP server
            connection = getLdapConnection();

            // specify return attributes and filter query
            String[] attributes = new String[] { MAX_PWD_AGE };
            Filter filter = Filter.createEqualityFilter(OBJECT_CLASS, DOMAIN);

            // execute LDAP query to get the maxPwdAge
            for (SearchResultEntry searchResultEntry : search(filter, attributes)) {
                maxPwdAge = searchResultEntry.getAttributeValueAsLong(MAX_PWD_AGE);
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

        return maxPwdAge;
    }

    /**
     * Method for getting a list of active directory user by a LDAP user filter.
     *
     * @param userFilter - LDAP filter for user
     * @return list of active directory users
     */
    public List<ActiveDirectoryUser> getUserList(String userFilter) {

        List<ActiveDirectoryUser> userList = new ArrayList<>();

        try {

            // execute LDAP query to get the list of active directory users
            for (SearchResultEntry entry : search(userFilter)) {

                // create new active directory user
                ActiveDirectoryUser user = new ActiveDirectoryUser();
                user.setsAMAccountName(entry.getAttributeValue(SAM_ACCOUNT_NAME));
                user.setUserPrincipalName(entry.getAttributeValue(USER_PRINCIPAL_NAME));
                user.setMail(entry.getAttributeValue(MAIL));
                user.setName(entry.getAttributeValue(NAME));

                // add user to list
                userList.add(user);
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return userList;
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param baseDn - base DN of the query
     * @param searchScope - search scope of the query
     * @param filter - LDAP filter
     * @param attributes - attributes which should be returned
     * @param paging - use paging for the query or not
     * @return list of search result entries
     */
    public List<SearchResultEntry> search(String baseDn, SearchScope searchScope, Filter filter, String[] attributes, boolean paging) {

        List<SearchResultEntry> searchResultEntries = new ArrayList<>();
        LDAPConnection connection = null;

        try {

            // logging
            LOG.info("exceuting LDAP query with baseDn '{}', search scope '{}', filter '{}', attributes '{}' and paging '{}'", baseDn, searchScope, filter, attributes, paging);

            // get connection to LDAP server
            connection = getLdapConnection();

            // whether to use paging or not
            if (paging) {

                ASN1OctetString cookie = null;

                // create search request for LDAP server
                SearchRequest searchRequest = new SearchRequest(baseDn, searchScope, filter, attributes);

                do {

                    // add cookie to search request
                    Control[] controls = new Control[1];
                    controls[0] = new SimplePagedResultsControl(elementsPerPage, cookie);
                    searchRequest.setControls(controls);

                    // execute LDAP query
                    SearchResult searchResult = connection.search(searchRequest);

                    // add search result entries to global list
                    searchResultEntries.addAll(searchResult.getSearchEntries());

                    // get cookie for next search page
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
                // execute LDAP query without paging
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

    /**
     * Method for executing an LDAP query.
     *
     * @param filterString - filter query as string
     * @return list of search result entries
     * @throws LDAPException
     */
    public List<SearchResultEntry> search(String filterString) throws LDAPException {
        return search(Filter.create(filterString), new String[]{});
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param filter - filter query
     * @return list of search result entries
     */
    public List<SearchResultEntry> search(Filter filter) {
        return search(filter, new String[]{});
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param filterString - filter query as string
     * @param attributes - attributes which should be returned
     * @return list of search result entries
     * @throws LDAPException
     */
    public List<SearchResultEntry> search(String filterString, String[] attributes) throws LDAPException {
        return search(SearchScope.SUB, Filter.create(filterString), attributes);
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param filter - filter query
     * @param attributes - attributes which should be returned
     * @return list of search result entries
     */
    public List<SearchResultEntry> search(Filter filter, String[] attributes) {
        return search(SearchScope.SUB, filter, attributes);
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param searchScope - search scope of the query
     * @param filterString - filter query as string
     * @param attributes - attributes which should be returned
     * @return list of search result entries
     * @throws LDAPException
     */
    public List<SearchResultEntry> search(SearchScope searchScope, String filterString, String[] attributes) throws LDAPException {
        return search(baseDn, searchScope, Filter.create(filterString), attributes);
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param searchScope - search scope of the query
     * @param filter - filter query
     * @param attributes - attributes which should be returned
     * @return list of search result entries
     */
    public List<SearchResultEntry> search(SearchScope searchScope, Filter filter, String[] attributes) {
        return search(baseDn, searchScope, filter, attributes);
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param baseDn - base DN of the query
     * @param searchScope - search scope of the query
     * @param filterString - filter query as string
     * @param attributes - attributes which should be returned
     * @return list of search result entries
     * @throws LDAPException
     */
    public List<SearchResultEntry> search(String baseDn, SearchScope searchScope, String filterString, String[] attributes) throws LDAPException {
        return search(baseDn, searchScope, Filter.create(filterString), attributes, false);
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param baseDn - base DN of the query
     * @param searchScope - search scope of the query
     * @param filter - filter query as string
     * @param attributes - attributes which should be returned
     * @return list of search result entries
     */
    public List<SearchResultEntry> search(String baseDn, SearchScope searchScope, Filter filter, String[] attributes) {
        return search(baseDn, searchScope, filter, attributes, false);
    }

    /**
     * Method for executing an LDAP query.
     *
     * @param baseDn - base DN of the query
     * @param searchScope - search scope of the query
     * @param filterString - filter query as string
     * @param attributes - attributes which should be returned
     * @param paging - use paging for the query or not
     * @return list of search result entries
     * @throws LDAPException
     */
    public List<SearchResultEntry> search(String baseDn, SearchScope searchScope, String filterString, String[] attributes, boolean paging) throws LDAPException {
        return search(baseDn, searchScope, Filter.create(filterString), attributes, paging);
    }

    /**
     * Method for getting a connection to an LDAP server.
     *
     * @return connection to LDAP server
     * @throws LDAPException
     */
    private LDAPConnection getLdapConnection() throws LDAPException {
        return new LDAPConnection(host, port, userDn, userSecret);
    }

    /**
     * Method for converting an active directory timestamp to a java timestamp.
     *
     * @param adTimestamp - timestamp in active directory format
     * @return java timestamp
     */
    public long getTimestamp(long adTimestamp) {
        return (adTimestamp / 10000) - TIMESTAMP_DIFFERENCE;
    }
}