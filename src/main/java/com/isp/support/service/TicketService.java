package com.isp.support.service;

import com.isp.support.form.TicketForm;
import com.isp.support.model.*;
import com.isp.support.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketService {
    private final TicketRepository tickets;
    private final TicketUpdateRepository updates;
    private final NotificationRepository notifications;
    private final NotificationService notify;
    private final ServiceProviderRepository providers;

    public TicketService(TicketRepository tickets, TicketUpdateRepository updates, NotificationRepository notifications, NotificationService notify, ServiceProviderRepository providers) {
        this.tickets = tickets;
        this.updates = updates;
        this.notifications = notifications;
        this.notify = notify;
        this.providers = providers;
    }

    private Ticket byId(String id) {
        return tickets.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    }

    public Ticket visible(String id, CurrentUserService.Identity me) {
        Ticket t = byId(id);
        if (me.role() == Role.ADMIN || me.role() == Role.LEVEL1 ||(me.role() == Role.CUSTOMER && me.id().equals(t.getCustomerId())) ||(me.role().isProvider() && me.id().equals(t.getAssignedProviderId()))) return t;
        throw new AccessDeniedException("You cannot view this ticket");
    }

    public List < TicketUpdate > history(Ticket t) {
        return updates.findByTicketIdOrderByCreatedAtAsc(t.getId());
    }

    public List < Ticket > list(CurrentUserService.Identity me) {
        if (me.role() == Role.CUSTOMER) return tickets.findByCustomerIdOrderByCreatedAtDesc(me.id());
        if (me.role() == Role.ADMIN || me.role() == Role.LEVEL1) return tickets.findAllByOrderByCreatedAtDesc();
        return tickets.findByAssignedProviderIdOrderByUpdatedAtDesc(me.id());
    }

    private void log(Ticket t, CurrentUserService.Identity actor, String note) {
        TicketUpdate u = new TicketUpdate();
        u.setTicketId(t.getId());
        u.setActorName(actor.name());
        u.setActorRole(actor.role().getLabel());
        u.setStatus(t.getStatus());
        u.setMessage(note);
        u.setCreatedAt(LocalDateTime.now());
        updates.save(u);
    }

    private void informCustomer(Ticket t, String title, String text) {
        notify.send(RecipientType.CUSTOMER, t.getCustomerId(), t.getId(), title, text);
    }

    private static String mustNote(String note) {
        if (note == null || note.trim().length() < 5 || note.length() > 1200) throw new IllegalArgumentException("Please enter a note between 5 and 1200 characters");
        return note.trim();
    }

    public Ticket create(CurrentUserService.Identity me, TicketForm f) {
        if (me.role() != Role.CUSTOMER) throw new AccessDeniedException("Customers only");
        Ticket t = new Ticket();
        t.setTicketNumber("ISP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        t.setCustomerId(me.id());
        t.setCustomerName(f.getCustomerName().trim());
        t.setCustomerPhone(f.getCustomerPhone().trim());
        t.setCustomerArea(f.getCustomerArea().trim());
        t.setLocationDetails(f.getLocationDetails() == null ? null : f.getLocationDetails().trim());
        t.setSubject(f.getSubject().trim());
        t.setDescription(f.getDescription().trim());
        t.setCategory(f.getCategory());
        t.setStatus(TicketStatus.OPEN);
        t.setPriority(Priority.NORMAL);
        t.setCreatedAt(LocalDateTime.now());
        t.setUpdatedAt(t.getCreatedAt());
        t = tickets.save(t);
        log(t, me, "Complaint submitted. Awaiting Level 1 review.");
        informCustomer(t, "Ticket created", "Your ticket " + t.getTicketNumber() + " was received.");
        for (ServiceProvider p : providers.findByRole(Role.LEVEL1)) if (p.isEnabled()) notify.send(RecipientType.SERVICE_PROVIDER, p.getId(), t.getId(), "New complaint", t.getTicketNumber() + ": " + t.getSubject());
        return t;
    }

    public void editOwn(String id, CurrentUserService.Identity me, TicketForm f) {
        Ticket t = visible(id, me);
        if (me.role() != Role.CUSTOMER || t.getStatus() != TicketStatus.OPEN) throw new AccessDeniedException("Only open tickets can be edited by their customer");
        t.setCustomerName(f.getCustomerName().trim());
        t.setCustomerPhone(f.getCustomerPhone().trim());
        t.setCustomerArea(f.getCustomerArea().trim());
        t.setLocationDetails(f.getLocationDetails() == null ? null : f.getLocationDetails().trim());
        t.setCategory(f.getCategory());
        t.setSubject(f.getSubject().trim());
        t.setDescription(f.getDescription().trim());
        t.setUpdatedAt(LocalDateTime.now());
        tickets.save(t);
        log(t, me, "Customer edited complaint details.");
    }

    private void requireLevel1(CurrentUserService.Identity me) {
        if (me.role() != Role.LEVEL1) throw new AccessDeniedException("Level 1 only");
    }

    public void review(String id, CurrentUserService.Identity me, String note) {
        requireLevel1(me);
        Ticket t = byId(id);
        if (t.getStatus() == TicketStatus.RESOLVED) throw new IllegalArgumentException("Ticket is already resolved");
        String msg = mustNote(note);
        t.setStatus(TicketStatus.UNDER_REVIEW);
        t.setUpdatedAt(LocalDateTime.now());
        tickets.save(t);
        log(t, me, msg);
        informCustomer(t, "Under review", t.getTicketNumber() + ": " + msg);
    }

    public void assign(String id, CurrentUserService.Identity me, String providerId, String note, LocalDateTime eta) {
        requireLevel1(me);
        Ticket t = byId(id);
        if (t.getStatus() == TicketStatus.RESOLVED) throw new IllegalArgumentException("Ticket is already resolved");
        ServiceProvider p = providers.findById(providerId == null ? "" : providerId).orElseThrow(() -> new IllegalArgumentException("Choose a valid staff member"));
        if (!p.isEnabled() || !(p.getRole() == Role.TECHNICAL || p.getRole() == Role.FIELD || p.getRole() == Role.BILLING)) throw new IllegalArgumentException("Choose an active technical, field or billing staff member");
        if (eta != null && !eta.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("Estimated resolution must be in the future");
        String msg = mustNote(note);
        t.setAssignedProviderId(p.getId());
        t.setAssignedProviderName(p.getName());
        t.setAssignedRole(p.getRole());
        t.setStatus(TicketStatus.PENDING);
        t.setEstimatedResolutionAt(eta);
        t.setUpdatedAt(LocalDateTime.now());
        tickets.save(t);
        log(t, me, "Forwarded to " + p.getName() + " (" + p.getRole().getLabel() + "). " + msg);
        String etaText = eta == null ? "Not yet specified" : eta.toString().replace('T', ' ');
        informCustomer(t, "Ticket forwarded", t.getTicketNumber() + " assigned to " + p.getRole().getLabel() + ". Estimated resolution: " + etaText + ". " + msg);
        notify.send(RecipientType.SERVICE_PROVIDER, p.getId(), t.getId(), "New assigned task", t.getTicketNumber() + ": " + t.getSubject());
    }

    public void resolveLevel1(String id, CurrentUserService.Identity me, String note) {
        requireLevel1(me);
        Ticket t = byId(id);
        if (t.getStatus() == TicketStatus.RESOLVED) throw new IllegalArgumentException("Already resolved");
        resolve(t, me, mustNote(note));
    }

    private void resolve(Ticket t, CurrentUserService.Identity me, String note) {
        t.setStatus(TicketStatus.RESOLVED);
        t.setResolutionNote(note);
        t.setResolvedAt(LocalDateTime.now());
        t.setUpdatedAt(LocalDateTime.now());
        tickets.save(t);
        log(t, me, "Problem resolved. " + note);
        informCustomer(t, "Complaint resolved", t.getTicketNumber() + ": " + note);
    }

    public void updateAssigned(String id, CurrentUserService.Identity me, TicketStatus newStatus, String note) {
        Ticket t = visible(id, me);
        if (!(me.role() == Role.TECHNICAL || me.role() == Role.FIELD || me.role() == Role.BILLING) || !me.id().equals(t.getAssignedProviderId())) throw new AccessDeniedException("Assigned service provider only");
        if (t.getStatus() == TicketStatus.RESOLVED) throw new IllegalArgumentException("Ticket already resolved");
        if (newStatus != TicketStatus.PENDING && newStatus != TicketStatus.IN_PROGRESS && newStatus != TicketStatus.RESOLVED) throw new IllegalArgumentException("Invalid staff status");
        String msg = mustNote(note);
        if (newStatus == TicketStatus.RESOLVED) {
            resolve(t, me, msg);
            return;
        }
        t.setStatus(newStatus);
        t.setUpdatedAt(LocalDateTime.now());
        tickets.save(t);
        log(t, me, msg);
        informCustomer(t, "Ticket " + newStatus.getLabel().toLowerCase(Locale.ROOT), t.getTicketNumber() + ": " + msg);
    }

    public void delete(String id, CurrentUserService.Identity me) {
        if (me.role() != Role.ADMIN) throw new AccessDeniedException("Admin only");
        Ticket t = byId(id);
        updates.deleteByTicketId(t.getId());
        notifications.deleteByTicketId(t.getId());
        tickets.delete(t);
    }
}
