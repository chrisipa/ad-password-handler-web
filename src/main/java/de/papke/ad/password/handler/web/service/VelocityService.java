package de.papke.ad.password.handler.web.service;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class VelocityService {

    private VelocityEngine velocityEngine;

    @PostConstruct
    private void init() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    public String evaluate(String templatePath, Map<String, Object> variableMap) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, variableMap);
    }
}
