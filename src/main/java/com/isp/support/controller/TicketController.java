package com.isp.support.controller;

import com.isp.support.form.TicketForm;
import com.isp.support.model.*;
import com.isp.support.repository.*;
import com.isp.support.service.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class TicketController {
    private final TicketService tickets;
    private final CurrentUserService current;
    private final ServiceProviderRepository providers;
    private final CustomerRepository customers;

    public TicketController(TicketService tickets, CurrentUserService current, ServiceProviderRepository providers, CustomerRepository customers) {
        this.tickets = tickets;
        this.current = current;
        this.providers = providers;
        this.customers = customers;
    }

    @GetMapping("/tickets")
    public String list(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "") String status, Model model) {
        var me = current.require();
        if (me.role().isProvider() && me.role() != Role.LEVEL1) return "redirect:/tasks";
        return listView(q, status, model, "My tickets");
    }

    @PreAuthorize("hasAnyRole('TECHNICAL','FIELD','BILLING')")
    @GetMapping("/tasks")
    public String tasks(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "") String status, Model model) {
        return listView(q, status, model, "My assigned tasks");
    }

    private String listView(String q, String status, Model model, String pageTitle) {
        var me = current.require();
        var items = tickets.list(me);
        if (!q.isBlank()) {
            String query = q.toLowerCase(Locale.ROOT);
            items = items.stream().filter(t -> t.getSubject().toLowerCase(Locale.ROOT).contains(query) || t.getTicketNumber().toLowerCase(Locale.ROOT).contains(query) || t.getCustomerName().toLowerCase(Locale.ROOT).contains(query) ||(t.getCustomerArea() != null && t.getCustomerArea().toLowerCase(Locale.ROOT).contains(query))).toList();
        }
        if (!status.isBlank()) {
            try {
                TicketStatus filter = TicketStatus.valueOf(status);
                items = items.stream().filter(t -> t.getStatus() == filter).toList();
            } catch (IllegalArgumentException ignored) {
            }
        }
        model.addAttribute("tickets", items);
        model.addAttribute("q", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("pageTitle", pageTitle);
        return "tickets";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/tickets/new")
    public String newForm(Model model) {
        var me = current.require();
        Customer customer = customers.findById(me.id()).orElseThrow(() -> new IllegalArgumentException("Customer account not found"));
        TicketForm form = new TicketForm();
        form.setCustomerName(customer.getName());
        form.setCustomerPhone(customer.getPhone());
        model.addAttribute("ticketForm", form);
        model.addAttribute("editMode", false);
        return "ticket-form";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/tickets")
    public String create(@Valid @ModelAttribute("ticketForm") TicketForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", false);
            return "ticket-form";
        }
        Ticket t = tickets.create(current.require(), form);
        return "redirect:/tickets/" + t.getId() + "?created";
    }

    @GetMapping("/tickets/{id}")
    public String detail(@PathVariable String id, Model model) {
        Ticket t = tickets.visible(id, current.require());
        model.addAttribute("ticket", t);
        model.addAttribute("history", tickets.history(t));
        model.addAttribute("assignable", providers.findAll().stream().filter(p -> p.isEnabled() && p.getRole() != Role.LEVEL1).sorted(Comparator.comparing(ServiceProvider::getName)).toList());
        return "ticket-detail";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/tickets/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        Ticket t = tickets.visible(id, current.require());
        if (t.getStatus() != TicketStatus.OPEN) throw new IllegalArgumentException("Only open complaints can be edited");
        TicketForm f = new TicketForm();
        f.setCustomerName(t.getCustomerName());
        f.setCustomerPhone(t.getCustomerPhone());
        f.setCustomerArea(t.getCustomerArea());
        f.setLocationDetails(t.getLocationDetails());
        f.setCategory(t.getCategory());
        f.setSubject(t.getSubject());
        f.setDescription(t.getDescription());
        model.addAttribute("ticketForm", f);
        model.addAttribute("ticket", t);
        model.addAttribute("editMode", true);
        return "ticket-form";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/tickets/{id}/edit")
    public String saveEdit(@PathVariable String id, @Valid @ModelAttribute("ticketForm") TicketForm f, BindingResult result, Model model) {
        Ticket t = tickets.visible(id, current.require());
        if (result.hasErrors()) {
            model.addAttribute("ticket", t);
            model.addAttribute("editMode", true);
            return "ticket-form";
        }
        tickets.editOwn(id, current.require(), f);
        return "redirect:/tickets/" + id + "?updated";
    }

    @PreAuthorize("hasRole('LEVEL1')")
    @PostMapping("/tickets/{id}/review")
    public String review(@PathVariable String id, @RequestParam String note) {
        tickets.review(id, current.require(), note);
        return "redirect:/tickets/" + id + "?updated";
    }

    @PreAuthorize("hasRole('LEVEL1')")
    @PostMapping("/tickets/{id}/assign")
    public String assign(@PathVariable String id, @RequestParam String providerId, @RequestParam String note, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eta) {
        tickets.assign(id, current.require(), providerId, note, eta);
        return "redirect:/tickets/" + id + "?updated";
    }

    @PreAuthorize("hasRole('LEVEL1')")
    @PostMapping("/tickets/{id}/resolve")
    public String resolve(@PathVariable String id, @RequestParam String note) {
        tickets.resolveLevel1(id, current.require(), note);
        return "redirect:/tickets/" + id + "?updated";
    }

    @PreAuthorize("hasAnyRole('TECHNICAL','FIELD','BILLING')")
    @PostMapping("/tickets/{id}/progress")
    public String progress(@PathVariable String id, @RequestParam TicketStatus status, @RequestParam String note) {
        tickets.updateAssigned(id, current.require(), status, note);
        return "redirect:/tickets/" + id + "?updated";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tickets/{id}/delete")
    public String delete(@PathVariable String id) {
        tickets.delete(id, current.require());
        return "redirect:/tickets?deleted";
    }
}
