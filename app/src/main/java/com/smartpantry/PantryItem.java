package com.smartpantry;

/** A single ingredient the user currently owns (one row of the pantry table). */
public class PantryItem {
    private String id;
    private String name;
    private double quantity;
    private String unit;
    private String expiry; // yyyy-MM-dd, or null when no expiry date was given

    public PantryItem(String id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiry() { return expiry; }
}
