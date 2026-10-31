package com.isp.support.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Public landing page shown before signing in. */
@Controller
public class WelcomeController {
    @GetMapping({
        "/", "/welcome"
    }) public String welcome() {
        return "welcome";
    }
}
