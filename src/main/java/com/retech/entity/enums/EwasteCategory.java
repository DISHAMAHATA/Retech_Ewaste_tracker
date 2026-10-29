package com.retech.entity.enums;

public enum EwasteCategory {
    LAPTOPS_COMPUTERS("Laptops & Computers"),
    MOBILE_TABLETS("Mobile Phones & Tablets"),
    ACCESSORIES_PERIPHERALS("Keyboards, Mice & Accessories"),
    CABLES_CHARGERS("Cables & Chargers"),
    TELEVISIONS_DISPLAYS("Televisions & Displays"),
    BATTERIES_POWERBANKS("Batteries & Power Banks"),
    OTHER_ELECTRONICS("Other Household Electronics");

    private final String label;

    EwasteCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
