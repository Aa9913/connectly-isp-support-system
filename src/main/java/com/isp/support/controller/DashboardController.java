package com.isp.support.controller;

import com.isp.support.model.*;
import com.isp.support.repository.*;
import com.isp.support.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {
    private final CurrentUserService current;
    private final TicketService tickets;
    private final TicketRepository ticketRepo;
    private final CustomerRepository customers;
    private final ServiceProviderRepository providers;

    public DashboardController(CurrentUserService current, TicketService tickets, TicketRepository ticketRepo, CustomerRepository customers, ServiceProviderRepository providers) {
        this.current = current;
        this.tickets = tickets;
        this.ticketRepo = ticketRepo;
        this.customers = customers;
        this.providers = providers;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var me = current.require();
        var items = tickets.list(me);
        model.addAttribute("tickets", items.stream().limit(6).toList());
        model.addAttribute("total", items.size());
        model.addAttribute("active", items.stream().filter(t -> t.getStatus() != TicketStatus.RESOLVED).count());
        model.addAttribute("resolved", items.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count());
        model.addAttribute("pending", items.stream().filter(t -> t.getStatus() == TicketStatus.PENDING).count());
        if (me.role() == Role.ADMIN) {
            model.addAttribute("customerCount", customers.count());
            model.addAttribute("staffCount", providers.count());
            return "dashboard-admin";
        }
        if (me.role() == Role.CUSTOMER) return "dashboard-customer";
        if (me.role() == Role.LEVEL1) return "dashboard-level1";
        return "dashboard-provider";
    }
}
