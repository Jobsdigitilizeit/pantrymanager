package com.smartpantry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Core business logic: the strict-matching rule.
 *
 * A recipe is "suggested" only if EVERY ingredient is in the pantry in at least the
 * required quantity. Names are normalised (case, plurals, filler words) and quantities
 * are compared after converting to a common base unit, so "Tomatoes" matches "tomato"
 * and 1 kg of flour satisfies a recipe that needs 500 g.
 */
public final class RecipeMatcher {

    /** Result of matching one recipe against the pantry. */
    public static class MatchResult {
        public final Recipe recipe;
        public final List<String> missing = new ArrayList<>();

        MatchResult(Recipe recipe) { this.recipe = recipe; }

        public boolean isStrictMatch() { return missing.isEmpty(); }
    }

    private static final Set<String> FILLER = new HashSet<>();
    static {
        String[] words = {"fresh", "large", "small", "medium", "ripe", "chopped", "sliced",
                "diced", "whole", "frozen", "dried", "of"};
        for (String w : words) FILLER.add(w);
    }

    private RecipeMatcher() { }

    /** Matches every recipe against the pantry, returning one result per recipe. */
    public static List<MatchResult> matchAll(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> stock = buildStock(pantry);
        List<MatchResult> results = new ArrayList<>();
        for (Recipe recipe : recipes) {
            MatchResult result = new MatchResult(recipe);
            for (RecipeIngredient ing : recipe.getIngredients()) {
                double have = stockOf(stock, ing.getName(), ing.getUnit());
                double need = Units.toBase(ing.getQuantity(), ing.getUnit());
                if (have + 1e-9 < need) {
                    result.missing.add(ing.getName());
                }
            }
            results.add(result);
        }
        return results;
    }

    /** Recipes where nothing is missing (the strict suggestions). */
    public static List<MatchResult> strictMatches(List<MatchResult> all) {
        List<MatchResult> out = new ArrayList<>();
        for (MatchResult r : all) if (r.missing.isEmpty()) out.add(r);
        return out;
    }

    /** Recipes missing exactly one ingredient (the optional "Almost There" list). */
    public static List<MatchResult> almostThere(List<MatchResult> all) {
        List<MatchResult> out = new ArrayList<>();
        for (MatchResult r : all) if (r.missing.size() == 1) out.add(r);
        return out;
    }

    // Total pantry quantity per (normalised name, unit family), in base units.
    private static Map<String, Double> buildStock(List<PantryItem> pantry) {
        Map<String, Double> stock = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = key(item.getName(), item.getUnit());
            double qty = Units.toBase(item.getQuantity(), item.getUnit());
            Double existing = stock.get(key);
            stock.put(key, (existing == null ? 0 : existing) + qty);
        }
        return stock;
    }

    private static double stockOf(Map<String, Double> stock, String name, String unit) {
        Double have = stock.get(key(name, unit));
        return have == null ? 0 : have;
    }

    private static String key(String name, String unit) {
        return normalize(name) + "|" + Units.family(unit);
    }

    /** Lower-cases, strips punctuation and filler words, and singularises each word. */
    static String normalize(String raw) {
        String cleaned = raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", " ").trim();
        StringBuilder sb = new StringBuilder();
        for (String word : cleaned.split("\\s+")) {
            if (word.isEmpty() || FILLER.contains(word)) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(singular(word));
        }
        return sb.toString();
    }

    /** Very small singulariser: tomatoes -> tomato, berries -> berry, eggs -> egg. */
    static String singular(String w) {
        int n = w.length();
        if (n <= 3) return w;
        if (w.endsWith("ies")) return w.substring(0, n - 3) + "y";
        if (w.endsWith("oes") || w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes")) {
            return w.substring(0, n - 2);
        }
        if (w.endsWith("ss") || w.endsWith("us")) return w;
        if (w.endsWith("s")) return w.substring(0, n - 1);
        return w;
    }
}
