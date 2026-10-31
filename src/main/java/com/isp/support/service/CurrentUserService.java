package com.isp.support.service;

import com.isp.support.model.*;
import com.isp.support.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.DisabledException;
import java.util.Locale;

@Service
public class CurrentUserService implements UserDetailsService {
    private final CustomerRepository customers;
    private final ServiceProviderRepository providers;
    private final AdminRepository admins;

    public CurrentUserService(CustomerRepository customers, ServiceProviderRepository providers, AdminRepository admins) {
        this.customers = customers;
        this.providers = providers;
        this.admins = admins;
    }

    public record Identity(String id, String name, String email, Role role) {
        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getEmail() {
            return email;
        }
        public Role getRole() {
            return role;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String e = email.trim().toLowerCase(Locale.ROOT);
        var customer = customers.findByEmailIgnoreCase(e);
        if (customer.isPresent()) {
            Customer c = customer.get();
            return User.withUsername(c.getEmail()).password(c.getPassword()).roles("CUSTOMER").disabled(!c.isEnabled()).build();
        }
        var provider = providers.findByEmailIgnoreCase(e);
        if (provider.isPresent()) {
            ServiceProvider s = provider.get();
            return User.withUsername(s.getEmail()).password(s.getPassword()).roles(s.getRole().name()).disabled(!s.isEnabled()).build();
        }
        var admin = admins.findByEmailIgnoreCase(e);
        if (admin.isPresent()) {
            Admin a = admin.get();
            return User.withUsername(a.getEmail()).password(a.getPassword()).roles("ADMIN").disabled(!a.isEnabled()).build();
        }
        throw new UsernameNotFoundException("Account not found");
    }

    public boolean exists(String email) {
        return customers.findByEmailIgnoreCase(email).isPresent() || providers.findByEmailIgnoreCase(email).isPresent() || admins.findByEmailIgnoreCase(email).isPresent();
    }

    public Identity current() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || "anonymousUser".equals(a.getPrincipal())) return null;
        String email = a.getName();
        var c = customers.findByEmailIgnoreCase(email);
        if (c.isPresent()) return new Identity(c.get().getId(), c.get().getName(), email, Role.CUSTOMER);
        var s = providers.findByEmailIgnoreCase(email);
        if (s.isPresent()) return new Identity(s.get().getId(), s.get().getName(), email, s.get().getRole());
        var ad = admins.findByEmailIgnoreCase(email);
        if (ad.isPresent()) return new Identity(ad.get().getId(), ad.get().getName(), email, Role.ADMIN);
        throw new DisabledException("Account unavailable");
    }

    public Identity require() {
        Identity me = current();
        if (me == null) throw new org.springframework.security.access.AccessDeniedException("Login required");
        return me;
    }
}
