package de.papke.ad.password.handler.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PasswordChangeController {

    @Autowired
    private PasswordChangeService passwordChangeService;

    @GetMapping("/")
    public String passwordForm(Model model) {
        model.addAttribute("password", new Credentials());
        return "index";
    }

    @PostMapping("/password")
    public String passwordSubmit(@ModelAttribute Credentials credentials) {
        passwordChangeService.changePassword(credentials.getLoginName(), credentials.getCurrentPassword(), credentials.getNewPassword());
        return "result";
    }
}

