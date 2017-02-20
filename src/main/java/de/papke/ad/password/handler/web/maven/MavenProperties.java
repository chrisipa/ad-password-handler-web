package de.papke.ad.password.handler.web.maven;

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
	
	private ResourceBundle resourceBundle;

	@PostConstruct
	public void initialize() {
		try {
			this.resourceBundle = ResourceBundle.getBundle("maven");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object get(String key) {
		return resourceBundle.getObject(key);
	}
}
