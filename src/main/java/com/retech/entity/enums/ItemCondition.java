package com.retech.entity.enums;

public enum ItemCondition {
    WORKING("Working / Functional"),
    PARTIALLY_WORKING("Partially Working"),
    NON_WORKING("Non-working / Damaged"),
    OBSOLETE_SCRAP("Obsolete / E-Waste Scrap");

    private final String label;

    ItemCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
