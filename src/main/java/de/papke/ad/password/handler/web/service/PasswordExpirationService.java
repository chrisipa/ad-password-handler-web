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

@Service
public class PasswordExpirationService {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordExpirationService.class);

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${password.expiration.user.filter}")
    private String userFilter;

    @Value("${password.expiration.days.till.expire}")
    private int daysTillExpire;

    @Autowired
    private MailService mailService;

    @Autowired
    private ActiveDirectoryService activeDirectoryService;

    @Scheduled(cron = "${password.expiration.cron.expression}")
    public void getDaysTillPasswordExpires() {

        try {

            // calculate days till password expires
            long now = System.currentTimeMillis();
            long maxPwdAge = activeDirectoryService.getMaxPasswordAge();

            for (ActiveDirectoryUser user : activeDirectoryService.getUserList(userFilter)) {

                String mail = user.getMail();

                if (!StringUtils.isEmpty(mail)) {

                    String sAMAccountName = user.getsAMAccountName();
                    if (!StringUtils.isEmpty(sAMAccountName)) {

                        long pwdLastSet = activeDirectoryService.getPasswordLastSet(sAMAccountName);

                        int daysTillPasswordExpires = -1;
                        if (maxPwdAge != -1 && pwdLastSet != -1) {

                            long expires = activeDirectoryService.getTimestamp(pwdLastSet + Math.abs(maxPwdAge));
                            daysTillPasswordExpires = (int) ((expires - now) / 1000 / 60 / 60 / 24);

                            if (daysTillPasswordExpires <= daysTillExpire) {

                                Map<String, Object> variableMap = new HashMap<>();
                                variableMap.put("name", user.getName());
                                variableMap.put("url", applicationUrl);
                                variableMap.put("days", daysTillPasswordExpires);

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
