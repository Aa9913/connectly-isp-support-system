package com.isp.support.controller;

import com.isp.support.form.RegistrationForm;
import com.isp.support.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final AccountService accounts;

    public AuthController(AccountService accounts) {
        this.accounts = accounts;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registration", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("registration") RegistrationForm form, BindingResult result, Model model) {
        if (result.hasErrors()) return "register";
        try {
            accounts.register(form);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (org.springframework.dao.DuplicateKeyException e) {
            model.addAttribute("error", "Email already registered");
            return "register";
        }
    }
}
