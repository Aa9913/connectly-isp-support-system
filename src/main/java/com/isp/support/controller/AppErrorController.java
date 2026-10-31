package com.isp.support.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class AppErrorController {
    @ExceptionHandler(IllegalArgumentException.class)
    public String invalid(IllegalArgumentException ex, RedirectAttributes redirect) {
        redirect.addFlashAttribute("error", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.FORBIDDEN)
    public String denied(AccessDeniedException ex, Model model) {
        model.addAttribute("message", "You do not have permission to access this page or perform this action.");
        return "error-page";
    }
}
