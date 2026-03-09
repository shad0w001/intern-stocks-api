package com.internship.stocks_api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    @GetMapping("/google")
    public String googleLogin() {
        return "redirect:/oauth2/authorization/google";
    }
}
