package com.isp.support.model;

public enum Category {
    NO_INTERNET("No internet connection"), SLOW_SPEED("Slow internet"), INTERMITTENT("Frequent disconnections"), ROUTER("Router / Wi-Fi"), FIELD_REPAIR("Cable / equipment damage"), BILLING("Billing / account"), OTHER("Other");
    private final String label;
    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
