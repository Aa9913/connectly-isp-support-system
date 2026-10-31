package com.isp.support.controller;

import com.isp.support.form.StaffForm;
import com.isp.support.model.Role;
import com.isp.support.repository.*;
import com.isp.support.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ServiceProviderRepository providers;
    private final CustomerRepository customers;
    private final AccountService accounts;

    public AdminController(ServiceProviderRepository providers, CustomerRepository customers, AccountService accounts) {
        this.providers = providers;
        this.customers = customers;
        this.accounts = accounts;
    }

    @GetMapping("/admin/staff")
    public String staff(Model model) {
        model.addAttribute("staff", providers.findAll());
        model.addAttribute("staffForm", new StaffForm());
        model.addAttribute("staffRoles", java.util.List.of(Role.LEVEL1, Role.TECHNICAL, Role.FIELD, Role.BILLING));
        return "admin-staff";
    }

    @PostMapping("/admin/staff")
    public String createStaff(@Valid @ModelAttribute("staffForm") StaffForm f, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                accounts.createStaff(f);
                return "redirect:/admin/staff?added";
            } catch (IllegalArgumentException | org.springframework.dao.DuplicateKeyException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        model.addAttribute("staff", providers.findAll());
        model.addAttribute("staffRoles", java.util.List.of(Role.LEVEL1, Role.TECHNICAL, Role.FIELD, Role.BILLING));
        return "admin-staff";
    }

    @PostMapping("/admin/staff/{id}/toggle")
    public String toggle(@PathVariable String id) {
        accounts.toggleStaff(id);
        return "redirect:/admin/staff?updated";
    }

    @GetMapping("/admin/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customers.findAll());
        return "admin-customers";
    }
}
