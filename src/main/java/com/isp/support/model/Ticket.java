package com.isp.support.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;
    @Indexed(unique = true)
    private String ticketNumber;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerArea;
    private String locationDetails;
    private Category category;
    private String subject;
    private String description;
    private TicketStatus status;
    private Priority priority;
    private String assignedProviderId;
    private String assignedProviderName;
    private Role assignedRole;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private java.time.LocalDateTime estimatedResolutionAt;
    private java.time.LocalDateTime resolvedAt;
    private String resolutionNote;

    public Ticket() {
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String value) {
        this.ticketNumber = value;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String value) {
        this.customerId = value;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String value) {
        this.customerName = value;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String value) {
        this.customerPhone = value;
    }

    public String getCustomerArea() {
        return customerArea;
    }

    public void setCustomerArea(String value) {
        this.customerArea = value;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String value) {
        this.locationDetails = value;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category value) {
        this.category = value;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String value) {
        this.subject = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus value) {
        this.status = value;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority value) {
        this.priority = value;
    }

    public String getAssignedProviderId() {
        return assignedProviderId;
    }

    public void setAssignedProviderId(String value) {
        this.assignedProviderId = value;
    }

    public String getAssignedProviderName() {
        return assignedProviderName;
    }

    public void setAssignedProviderName(String value) {
        this.assignedProviderName = value;
    }

    public Role getAssignedRole() {
        return assignedRole;
    }

    public void setAssignedRole(Role value) {
        this.assignedRole = value;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime value) {
        this.createdAt = value;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime value) {
        this.updatedAt = value;
    }

    public java.time.LocalDateTime getEstimatedResolutionAt() {
        return estimatedResolutionAt;
    }

    public void setEstimatedResolutionAt(java.time.LocalDateTime value) {
        this.estimatedResolutionAt = value;
    }

    public java.time.LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(java.time.LocalDateTime value) {
        this.resolvedAt = value;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String value) {
        this.resolutionNote = value;
    }
}
