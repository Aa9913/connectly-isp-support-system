package com.isp.support.config;

import com.isp.support.model.*;
import com.isp.support.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DemoDataSeeder {
    @Value("${app.demo-seed:false}")
    private boolean enabled;
    private final AdminRepository admins;
    private final CustomerRepository customers;
    private final ServiceProviderRepository providers;
    private final TicketRepository tickets;
    private final TicketUpdateRepository updates;
    private final PasswordEncoder encoder;

    public DemoDataSeeder(AdminRepository admins, CustomerRepository customers, ServiceProviderRepository providers, TicketRepository tickets, TicketUpdateRepository updates, PasswordEncoder encoder) {
        this.admins = admins;
        this.customers = customers;
        this.providers = providers;
        this.tickets = tickets;
        this.updates = updates;
        this.encoder = encoder;
    }

    private ServiceProvider provider(String name, String email, Role role) {
        return providers.findByEmailIgnoreCase(email).orElseGet(() -> {
            ServiceProvider p = new ServiceProvider();
            p.setName(name);
            p.setEmail(email);
            p.setPhone("0770000000");
            p.setPassword(encoder.encode("Demo@12345"));
            p.setEnabled(true);
            p.setRole(role);
            p.setCreatedAt(LocalDateTime.now());
            return providers.save(p);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        if (!enabled) return;
        System.out.println("\n*** DEMO MODE ENABLED: sample accounts use Demo@12345. Disable APP_DEMO_SEED before public deployment. ***\n");
        admins.findByEmailIgnoreCase("admin@isp.local").orElseGet(() -> {
            Admin a = new Admin();
            a.setName("System Administrator");
            a.setEmail("admin@isp.local");
            a.setPassword(encoder.encode("Demo@12345"));
            a.setEnabled(true);
            a.setCreatedAt(LocalDateTime.now());
            return admins.save(a);
        });
        provider("Amal Support", "support@isp.local", Role.LEVEL1);
        ServiceProvider technical = provider("Nimal Technical", "technical@isp.local", Role.TECHNICAL);
        provider("Kavindi Field", "field@isp.local", Role.FIELD);
        provider("Tharushi Billing", "billing@isp.local", Role.BILLING);
        Customer c = customers.findByEmailIgnoreCase("customer@isp.local").orElseGet(() -> {
            Customer x = new Customer();
            x.setName("Demo Customer");
            x.setEmail("customer@isp.local");
            x.setPhone("0771234567");
            x.setPassword(encoder.encode("Demo@12345"));
            x.setEnabled(true);
            x.setCreatedAt(LocalDateTime.now());
            return customers.save(x);
        });
        if (tickets.findAll().isEmpty()) {
            Ticket t = new Ticket();
            t.setTicketNumber("ISP-DEMO101");
            t.setCustomerId(c.getId());
            t.setCustomerName(c.getName());
            t.setCustomerPhone(c.getPhone());
            t.setCustomerArea("Negombo");
            t.setLocationDetails("Near the town centre (demo location)");
            t.setCategory(Category.INTERMITTENT);
            t.setSubject("Internet disconnects every evening");
            t.setDescription("The fibre connection disconnects several times each evening, and router restarts have not helped.");
            t.setStatus(TicketStatus.PENDING);
            t.setPriority(Priority.NORMAL);
            t.setAssignedProviderId(technical.getId());
            t.setAssignedProviderName(technical.getName());
            t.setAssignedRole(technical.getRole());
            t.setCreatedAt(LocalDateTime.now().minusHours(4));
            t.setUpdatedAt(LocalDateTime.now().minusHours(2));
            t.setEstimatedResolutionAt(LocalDateTime.now().plusDays(1));
            t = tickets.save(t);
            TicketUpdate u = new TicketUpdate();
            u.setTicketId(t.getId());
            u.setActorName(c.getName());
            u.setActorRole("Customer");
            u.setStatus(TicketStatus.OPEN);
            u.setMessage("Complaint submitted.");
            u.setCreatedAt(LocalDateTime.now().minusHours(4));
            updates.save(u);
            TicketUpdate a = new TicketUpdate();
            a.setTicketId(t.getId());
            a.setActorName("Amal Support");
            a.setActorRole("Level 1 Support");
            a.setStatus(TicketStatus.PENDING);
            a.setMessage("Reviewed and forwarded to technical support. Estimated resolution: tomorrow.");
            a.setCreatedAt(LocalDateTime.now().minusHours(2));
            updates.save(a);
        }
    }
}
