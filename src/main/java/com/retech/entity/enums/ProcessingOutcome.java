package com.retech.entity.enums;

public enum ProcessingOutcome {
    MATERIAL_RECOVERY("Raw Material Recovery (Metals, Copper, Plastics)"),
    COMPONENT_HARVESTING("Component Harvesting for Repair/Refurbishment"),
    SAFE_HAZARDOUS_DISPOSAL("Safe Hazardous Waste Neutralization"),
    REFURBISHED("Item Refurbished for Secondary Use");

    private final String description;

    ProcessingOutcome(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
