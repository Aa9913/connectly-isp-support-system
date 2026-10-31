package com.isp.support.model;

public enum Role {
    CUSTOMER("Customer"), LEVEL1("Level 1 Support"), TECHNICAL("Technical Officer"), FIELD("Field Technician"), BILLING("Billing Support"), ADMIN("Administrator");
    private final String label;
    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isProvider() {
        return this == LEVEL1 || this == TECHNICAL || this == FIELD || this == BILLING;
    }
}
