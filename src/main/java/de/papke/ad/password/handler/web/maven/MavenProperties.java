package de.papke.ad.password.handler.web.maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ResourceBundle;

/**
 * Maven properties class for accessing project properties.
 *
 * @author Christoph Papke (info@papke.it)
 */
@Component
public class MavenProperties {

    private static final Logger LOG = LoggerFactory.getLogger(MavenProperties.class);

    private ResourceBundle resourceBundle;

    /**
     * Method for initializing the resource bundle by maven property file.
     */
    @PostConstruct
    public void init() {
        try {
            this.resourceBundle = ResourceBundle.getBundle("maven");
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Method for getting a maven property by key.
     *
     * @param key - maven property key
     * @return maven property value
     */
    public Object get(String key) {
        return resourceBundle.getObject(key);
    }
}
