package com.smartpantry;

import java.util.Locale;

/**
 * Unit helper. Every unit belongs to a family (mass, volume or count) and can be
 * converted to that family's base unit (g, ml or pcs) so quantities can be compared.
 */
public final class Units {
    public static final String[] ALL = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"};

    public static final int MASS = 0;
    public static final int VOLUME = 1;
    public static final int COUNT = 2;

    private Units() { }

    public static int family(String unit) {
        switch (unit) {
            case "g":
            case "kg":
                return MASS;
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
                return VOLUME;
            default:
                return COUNT;
        }
    }

    /** Converts a quantity to the base unit of its family (g, ml or pcs). */
    public static double toBase(double quantity, String unit) {
        switch (unit) {
            case "kg":   return quantity * 1000;
            case "l":    return quantity * 1000;
            case "tsp":  return quantity * 5;
            case "tbsp": return quantity * 15;
            case "cup":  return quantity * 240;
            default:     return quantity; // g, ml, pcs
        }
    }

    /** Prints 2.0 as "2" and 0.5 as "0.5". */
    public static String format(double q) {
        if (q == Math.floor(q)) {
            return String.format(Locale.US, "%d", (long) q);
        }
        return String.format(Locale.US, "%.2f", q).replaceAll("0+$", "");
    }
}
