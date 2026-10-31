package com.isp.support.model;

public enum TicketStatus {
    OPEN("Open"), UNDER_REVIEW("Under review"), PENDING("Pending"), IN_PROGRESS("In progress"), RESOLVED("Resolved");
    private final String label;
    TicketStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
