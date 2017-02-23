package de.papke.ad.password.handler.web.service;

import com.unboundid.ldap.sdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ActiveDirectoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveDirectoryService.class);

    private static final String USER_PRINCIPAL_NAME = "userPrincipalName";
    private static final String SAM_ACCOUNT_NAME = "sAMAccountName";

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

    public String getSAMAccountName(String userPrincipalName) {

        String sAMAccountName = null;
        LDAPConnection connection = null;

        try {
            connection = getLdapConnection();
            String[] attributes = new String[] { SAM_ACCOUNT_NAME };
            Filter filter = Filter.createEqualityFilter(USER_PRINCIPAL_NAME, userPrincipalName);
            SearchResult searchResult = connection.search(baseDn, SearchScope.SUB, filter, attributes);

            for (SearchResultEntry searchResultEntry : searchResult.getSearchEntries()) {
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

    private LDAPConnection getLdapConnection() throws LDAPException {
        return new LDAPConnection(host, port, userDn, userSecret);
    }
}
