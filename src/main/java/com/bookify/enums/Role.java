package com.bookify.enums;

/**
 *Defines user roles in the system
 */
public enum Role {
    CLIENT("Client"),
    STAFF("Staff"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}