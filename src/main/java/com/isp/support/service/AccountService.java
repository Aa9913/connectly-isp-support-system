package com.isp.support.service;

import com.isp.support.model.*;
import com.isp.support.repository.*;
import com.isp.support.form.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AccountService {
    private final CustomerRepository customers;
    private final ServiceProviderRepository providers;
    private final CurrentUserService users;
    private final PasswordEncoder encoder;

    public AccountService(CustomerRepository customers, ServiceProviderRepository providers, CurrentUserService users, PasswordEncoder encoder) {
        this.customers = customers;
        this.providers = providers;
        this.users = users;
        this.encoder = encoder;
    }

    public void register(RegistrationForm f) {
        String email = f.getEmail().trim().toLowerCase(Locale.ROOT);
        if (users.exists(email)) throw new IllegalArgumentException("Email is already registered");
        Customer c = new Customer();
        c.setName(f.getName().trim());
        c.setEmail(email);
        c.setPhone(f.getPhone().trim());
        c.setPassword(encoder.encode(f.getPassword()));
        c.setEnabled(true);
        c.setCreatedAt(LocalDateTime.now());
        customers.save(c);
    }

    public void createStaff(StaffForm f) {
        if (f.getRole() == null || !f.getRole().isProvider()) throw new IllegalArgumentException("Choose a support role");
        String email = f.getEmail().trim().toLowerCase(Locale.ROOT);
        if (users.exists(email)) throw new IllegalArgumentException("Email is already registered");
        ServiceProvider s = new ServiceProvider();
        s.setName(f.getName().trim());
        s.setEmail(email);
        s.setPhone(f.getPhone().trim());
        s.setPassword(encoder.encode(f.getPassword()));
        s.setEnabled(true);
        s.setRole(f.getRole());
        s.setCreatedAt(LocalDateTime.now());
        providers.save(s);
    }

    public void toggleStaff(String id) {
        ServiceProvider p = providers.findById(id).orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        p.setEnabled(!p.isEnabled());
        providers.save(p);
    }
}
