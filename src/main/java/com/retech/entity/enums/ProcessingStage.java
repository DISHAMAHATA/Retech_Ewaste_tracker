package com.retech.entity.enums;

public enum ProcessingStage {
    RECEIVED("Intake & Weight Verification"),
    SORTING("Material & Component Sorting"),
    DISASSEMBLY("Manual / Mechanical Disassembly"),
    RECOVERY("Material Extraction & Recovery"),
    COMPLETED("Final Processing Completed");

    private final String description;

    ProcessingStage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
