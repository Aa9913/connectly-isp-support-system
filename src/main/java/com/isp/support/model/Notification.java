package com.isp.support.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "notification")
public class Notification {
    @Id
    private String id;
    private RecipientType recipientType;
    private String recipientId;
    private String ticketId;
    private String title;
    private String message;
    private boolean read;
    private java.time.LocalDateTime createdAt;

    public Notification() {
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType value) {
        this.recipientType = value;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String value) {
        this.recipientId = value;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String value) {
        this.ticketId = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean value) {
        this.read = value;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime value) {
        this.createdAt = value;
    }
}
