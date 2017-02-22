package de.papke.ad.password.handler.web.controller;

import de.papke.ad.password.handler.web.maven.MavenProperties;
import de.papke.ad.password.handler.web.model.Credentials;
import de.papke.ad.password.handler.web.service.PasswordChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PasswordChangeController {

    @Autowired
    private MavenProperties mavenProperties;

    @Autowired
    private PasswordChangeService passwordChangeService;

    @GetMapping("/")
    public String passwordForm(Model model) {
        model.addAttribute("name", mavenProperties.get("name"));
        model.addAttribute("version", mavenProperties.get("version"));
        return "index";
    }

    @PostMapping("/pwchange")
    public ResponseEntity<String> passwordSubmit(@ModelAttribute Credentials credentials) {

        boolean success = passwordChangeService.changePassword(credentials.getUsername(), credentials.getPassword(), credentials.getNewPassword());

        ResponseEntity<String> responseEntity;
        if (success) {
            responseEntity = ResponseEntity.status(HttpStatus.OK).build();
        }
        else {
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return  responseEntity;
    }
}

