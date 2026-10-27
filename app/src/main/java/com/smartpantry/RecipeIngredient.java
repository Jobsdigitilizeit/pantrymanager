package com.smartpantry;

/** One ingredient line of a recipe, e.g. "200 g pasta". */
public class RecipeIngredient {
    private final String name;
    private final double quantity;
    private final String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    /** Human readable form such as "200 g pasta". */
    @Override
    public String toString() {
        return Units.format(quantity) + " " + unit + " " + name;
    }
}
