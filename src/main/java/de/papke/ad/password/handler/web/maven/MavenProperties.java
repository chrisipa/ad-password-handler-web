package de.papke.ad.password.handler.web.maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ResourceBundle;

/**
 * Maven properties class for accessing project properties.
 * 
 * @author Christoph Papke (info@christoph-papke.de)
 */
@Component
public class MavenProperties {

	private static final Logger LOG = LoggerFactory.getLogger(MavenProperties.class);

	private ResourceBundle resourceBundle;

	@PostConstruct
	public void initialize() {
		try {
			this.resourceBundle = ResourceBundle.getBundle("maven");
		}
		catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public Object get(String key) {
		return resourceBundle.getObject(key);
	}
}
