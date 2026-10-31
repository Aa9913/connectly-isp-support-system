package com.isp.support.form;

import com.isp.support.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TicketForm {
    @NotBlank(message = "Enter the customer's full name")
    @Size(max = 90)
    private String customerName;
    @NotBlank(message = "Enter a contact number")
    @Pattern(regexp = "^[+0-9()\\s-]{7,25}$", message = "Enter a valid phone number (7–25 characters)")
    private String customerPhone;
    @NotBlank(message = "Enter the area where the problem is occurring")
    @Size(max = 120)
    private String customerArea;
    @Size(max = 220)
    private String locationDetails;
    @NotNull(message = "Choose an issue category")
    private Category category;
    @NotBlank
    @Size(min = 6, max = 130)
    private String subject;
    @NotBlank
    @Size(min = 15, max = 2500)
    private String description;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerArea() {
        return customerArea;
    }

    public void setCustomerArea(String customerArea) {
        this.customerArea = customerArea;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
