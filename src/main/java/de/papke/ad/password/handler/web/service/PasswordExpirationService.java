package de.papke.ad.password.handler.web.service;

import de.papke.ad.password.handler.web.model.ActiveDirectoryUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class for handling the password expiration of an active directory user.
 *
 * @author Christoph Papke (info@papke.it)
 */
@Service
public class PasswordExpirationService {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordExpirationService.class);

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${password.expiration.user.filter}")
    private String userFilter;

    @Value("${password.expiration.days.till.expires}")
    private int daysTillExpires;

    @Autowired
    private MailService mailService;

    @Autowired
    private ActiveDirectoryService activeDirectoryService;

    @Scheduled(cron = "${password.expiration.cron.expression}")
    public void getDaysTillPasswordExpires() {

        try {

            // get current timestamp
            long now = System.currentTimeMillis();

            // get password max age from active directory
            long maxPwdAge = activeDirectoryService.getMaxPasswordAge();

            // get all active directory users which should be checked by filter string
            for (ActiveDirectoryUser user : activeDirectoryService.getUserList(userFilter)) {

                // get mail address of user
                String mail = user.getMail();

                // check if mail address is set
                if (!StringUtils.isEmpty(mail)) {

                    // get sAMAccountName of user
                    String sAMAccountName = user.getsAMAccountName();

                    // check if sAMAccountName is set
                    if (!StringUtils.isEmpty(sAMAccountName)) {

                        // get timestamp of last password change
                        long pwdLastSet = activeDirectoryService.getPasswordLastSet(sAMAccountName);

                        // if password max age and timestamp of last password change are set
                        if (maxPwdAge != -1 && pwdLastSet != -1) {

                            // get timestamp for password expiration
                            long expiresTimestamp = activeDirectoryService.getTimestamp(pwdLastSet + Math.abs(maxPwdAge));

                            // calculate days till password expires
                            int passwordDaysTillExpires = (int) ((expiresTimestamp - now) / 1000 / 60 / 60 / 24);

                            // send mail if password expires soon
                            if (passwordDaysTillExpires <= daysTillExpires) {

                                // create variable map for substitution
                                Map<String, Object> variableMap = new HashMap<>();
                                variableMap.put("name", user.getName());
                                variableMap.put("url", applicationUrl);
                                variableMap.put("days", passwordDaysTillExpires);

                                // send mail
                                mailService.send(mail, variableMap);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
