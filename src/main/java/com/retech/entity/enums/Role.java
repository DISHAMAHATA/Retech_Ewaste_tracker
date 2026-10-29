package com.retech.entity.enums;

public enum Role {
    ROLE_CUSTOMER,
    ROLE_AGENT,
    ROLE_OPERATOR,
    ROLE_ADMIN;

    public String getDisplayName() {
        return switch (this) {
            case ROLE_CUSTOMER -> "Customer";
            case ROLE_AGENT -> "Collection Agent";
            case ROLE_OPERATOR -> "Recycling Operator";
            case ROLE_ADMIN -> "Administrator";
        };
    }
}
