package de.papke.ad.password.handler.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * Service class for sending emails.
 *
 * @author Christoph Papke (info@papke.it)
 */
@Service
public class MailService {

    @Value("${mail.protocol}")
    private String protocol;

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.smtp.auth}")
    private boolean auth;

    @Value("${mail.smtp.starttls.enable}")
    private boolean starttls;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.template.path}")
    private String templatePath;

    @Value("${mail.subject}")
    private String subject;

    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityService velocityService;

    @PostConstruct
    public void init() {

        // create new java mail sender
        mailSender = new JavaMailSenderImpl();

        // set mail sender configuration
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol(protocol);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // create java mail properties
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", starttls);

        // set java mail properties
        mailSender.setJavaMailProperties(mailProperties);
    }

    /**
     * Method for sending an HTML mail.
     *
     * @param to - mail recipient
     * @param variableMap - variable map for substitution
     */
    public void send(String to, Map variableMap) {

        // create mime message preparator
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                // get mail text by substituting variables in mail template with velocity
                String text = velocityService.evaluate(templatePath, variableMap);

                // create message helper
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                messageHelper.setFrom(from);
                messageHelper.setTo(to);
                messageHelper.setSubject(subject);
                messageHelper.setText(text, true);
            }
        };

        // send HTML mail
        mailSender.send(preparator);
    }
}