package de.papke.ad.password.handler.web.controller;

import de.papke.ad.password.handler.web.maven.MavenProperties;
import de.papke.ad.password.handler.web.model.Credentials;
import de.papke.ad.password.handler.web.service.PasswordChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller class for changing the password of an active director user.
 *
 * @author Christoph Papke (info@papke.it)
 */
@Controller
public class PasswordChangeController {

    @Autowired
    private MavenProperties mavenProperties;
    
    @Autowired
    private PasswordChangeService passwordChangeService;
    
    @Value("${application.title}")
    private String applicationTitle;

    /**
     * Method for creating model and view for index page
     *
     * @param model - model for spring mvc
     * @return view name
     */
    @GetMapping("/")
    public String index(Model model) {

        // add maven properties to model object
        model.addAttribute("name", applicationTitle);
        model.addAttribute("version", mavenProperties.get("version"));

        // return view name
        return "index";
    }

    /**
     * Method for chaning the password of an active directory user.
     *
     * @param credentials - the user credentials
     * @return HTTP status code as response entity
     */
    @PostMapping("/pwchange")
    public ResponseEntity<String> changePassword(@ModelAttribute Credentials credentials) {

        // change password with the given credentials
        boolean success = passwordChangeService.changePassword(credentials.getUsername(), credentials.getPassword(), credentials.getNewPassword());

        // create response entity based on success variable
        ResponseEntity<String> responseEntity;
        if (success) {
            responseEntity = ResponseEntity.status(HttpStatus.OK).build();
        }
        else {
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // return response entity
        return  responseEntity;
    }
}

