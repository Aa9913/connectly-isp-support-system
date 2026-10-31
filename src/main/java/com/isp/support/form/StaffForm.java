package com.isp.support.form;

import jakarta.validation.constraints.*;

public class StaffForm {
    @NotBlank
    @Size(max = 90)
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(max = 25)
    private String phone;
    @NotBlank
    @Size(min = 8, max = 72)
    private String password;
    @NotNull
    private com.isp.support.model.Role role;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String value) {
        this.phone = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public com.isp.support.model.Role getRole() {
        return role;
    }

    public void setRole(com.isp.support.model.Role value) {
        this.role = value;
    }
}
