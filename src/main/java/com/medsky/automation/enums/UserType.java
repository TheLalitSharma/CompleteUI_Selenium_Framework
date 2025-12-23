package com.medsky.automation.enums;

public enum UserType {
    ADMIN("admin"),
    STANDARD("standard");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
