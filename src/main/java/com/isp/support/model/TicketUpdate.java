package com.isp.support.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "ticket_updates")
public class TicketUpdate {
    @Id
    private String id;
    private String ticketId;
    private String actorName;
    private String actorRole;
    private TicketStatus status;
    private String message;
    private java.time.LocalDateTime createdAt;

    public TicketUpdate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String value) {
        this.ticketId = value;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String value) {
        this.actorName = value;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String value) {
        this.actorRole = value;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus value) {
        this.status = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime value) {
        this.createdAt = value;
    }
}
