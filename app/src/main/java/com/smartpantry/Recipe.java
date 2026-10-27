package com.smartpantry;

import java.util.ArrayList;
import java.util.List;

/** A recipe with its required ingredients and preparation steps. */
public class Recipe {
    private final String id;
    private final String name;
    private final String steps;
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(String id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSteps() { return steps; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
