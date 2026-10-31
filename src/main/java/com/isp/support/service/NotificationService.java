package com.isp.support.service;

import com.isp.support.model.*;
import com.isp.support.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public void send(RecipientType type, String recipientId, String ticketId, String title, String message) {
        Notification n = new Notification();
        n.setRecipientType(type);
        n.setRecipientId(recipientId);
        n.setTicketId(ticketId);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        repo.save(n);
    }

    private RecipientType type(CurrentUserService.Identity user) {
        return user.role() == Role.CUSTOMER ? RecipientType.CUSTOMER : RecipientType.SERVICE_PROVIDER;
    }

    public List < Notification > forUser(CurrentUserService.Identity user) {
        return repo.findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(type(user), user.id());
    }

    public long unreadCount(CurrentUserService.Identity user) {
        if (user.role() == Role.ADMIN) return 0;
        return repo.countByRecipientTypeAndRecipientIdAndReadFalse(type(user), user.id());
    }

    public void markRead(String id, CurrentUserService.Identity user) {
        Notification n = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!n.getRecipientId().equals(user.id()) || n.getRecipientType() != type(user)) throw new AccessDeniedException("Not your notification");
        n.setRead(true);
        repo.save(n);
    }
}
