package com.smartpantry;

/** Android-free recipe collection, shared by the Android app and the web version. */
public final class RecipeData {
    private RecipeData() { }

    /** Receives each recipe; ingredients are "name|quantity|unit". */
    public interface Sink {
        void recipe(String name, String steps, String[] ingredients);
    }

    // Ingredient format: "name|quantity|unit"
    static void load(Sink sink) {
        add(sink, "Tomato Pasta",
                "1. Boil the pasta in salted water until al dente.\n2. Fry garlic in olive oil, add chopped tomatoes and simmer 10 minutes.\n3. Toss the pasta through the sauce and serve.",
                "pasta|200|g", "tomato|3|pcs", "garlic|2|pcs", "olive oil|2|tbsp");
        add(sink, "Cheese Omelette",
                "1. Whisk the eggs with a pinch of salt.\n2. Melt butter in a pan and pour in the eggs.\n3. Add cheese, fold and cook until set.",
                "egg|3|pcs", "cheese|50|g", "butter|1|tbsp");
        add(sink, "Pancakes",
                "1. Whisk flour, egg and milk into a smooth batter.\n2. Heat a little butter in a pan.\n3. Cook ladlefuls until golden on both sides.",
                "flour|200|g", "egg|2|pcs", "milk|300|ml", "butter|1|tbsp");
        add(sink, "Egg Fried Rice",
                "1. Scramble the eggs in a hot pan and set aside.\n2. Fry the rice with soy sauce.\n3. Stir the egg back in and serve hot.",
                "rice|200|g", "egg|2|pcs", "soy sauce|2|tbsp", "onion|1|pcs");
        add(sink, "Garlic Butter Toast",
                "1. Mash butter with crushed garlic.\n2. Spread on the bread.\n3. Toast under the grill until golden.",
                "bread|4|pcs", "butter|2|tbsp", "garlic|2|pcs");
        add(sink, "Cheese Toastie",
                "1. Butter the bread on the outside.\n2. Fill with cheese.\n3. Fry on both sides until melted and golden.",
                "bread|2|pcs", "cheese|60|g", "butter|1|tbsp");
        add(sink, "Potato Mash",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with butter and milk.\n3. Season with salt.",
                "potato|500|g", "butter|2|tbsp", "milk|100|ml");
        add(sink, "Vegetable Soup",
                "1. Fry the onion in oil.\n2. Add chopped carrots, potatoes and water.\n3. Simmer 25 minutes and season.",
                "onion|1|pcs", "carrot|2|pcs", "potato|2|pcs", "olive oil|1|tbsp");
        add(sink, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Scramble the eggs gently in butter.\n3. Pile onto the toast.",
                "egg|3|pcs", "bread|2|pcs", "butter|1|tbsp");
        add(sink, "Rice and Beans",
                "1. Cook the rice.\n2. Fry onion and garlic, add the beans and heat through.\n3. Serve beans over rice.",
                "rice|200|g", "beans|1|cup", "onion|1|pcs", "garlic|1|pcs");
        add(sink, "Tuna Pasta",
                "1. Cook the pasta.\n2. Mix the tuna with olive oil and lemon juice.\n3. Combine with the pasta.",
                "pasta|200|g", "tuna|1|pcs", "olive oil|1|tbsp", "lemon|1|pcs");
        add(sink, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix in eggs and flour.\n3. Fry spoonfuls until golden.",
                "banana|2|pcs", "egg|2|pcs", "flour|100|g");
        add(sink, "Fried Rice with Vegetables",
                "1. Stir-fry onion, carrot and peas.\n2. Add the cooked rice and soy sauce.\n3. Fry until piping hot.",
                "rice|200|g", "carrot|1|pcs", "pea|100|g", "soy sauce|2|tbsp", "onion|1|pcs");
        add(sink, "Mac and Cheese",
                "1. Boil the macaroni.\n2. Make a sauce of butter, flour and milk, then stir in cheese.\n3. Mix with the macaroni.",
                "macaroni|200|g", "cheese|100|g", "milk|250|ml", "butter|2|tbsp", "flour|2|tbsp");
        add(sink, "Chicken Rice Bowl",
                "1. Season and pan-fry the chicken.\n2. Cook the rice.\n3. Slice the chicken over the rice and serve.",
                "chicken breast|300|g", "rice|200|g", "olive oil|1|tbsp", "garlic|2|pcs");
        add(sink, "Tomato Soup",
                "1. Fry onion and garlic.\n2. Add tomatoes and stock, simmer 20 minutes.\n3. Blend until smooth.",
                "tomato|6|pcs", "onion|1|pcs", "garlic|2|pcs", "olive oil|1|tbsp");
        add(sink, "French Toast",
                "1. Whisk eggs with milk.\n2. Dip the bread slices.\n3. Fry in butter until golden.",
                "bread|4|pcs", "egg|2|pcs", "milk|100|ml", "butter|1|tbsp");
        add(sink, "Cheesy Baked Potatoes",
                "1. Bake the potatoes at 200C for 1 hour.\n2. Split open and fill with butter and cheese.\n3. Return to the oven until melted.",
                "potato|4|pcs", "cheese|100|g", "butter|2|tbsp");
        add(sink, "Lemon Pancakes",
                "1. Make the pancake batter from flour, egg and milk.\n2. Fry thin pancakes.\n3. Serve with lemon juice and sugar.",
                "flour|150|g", "egg|1|pcs", "milk|250|ml", "lemon|1|pcs", "sugar|2|tbsp");
        add(sink, "Garlic Rice",
                "1. Fry crushed garlic in butter.\n2. Add the rice and stir to coat.\n3. Add water and cook until tender.",
                "rice|200|g", "garlic|4|pcs", "butter|1|tbsp");
        add(sink, "Onion Omelette",
                "1. Fry sliced onion in butter until soft.\n2. Pour over whisked eggs.\n3. Cook until set and fold.",
                "egg|3|pcs", "onion|1|pcs", "butter|1|tbsp");
    }

    private static void add(Sink sink, String name, String steps, String... ingredients) {
        sink.recipe(name, steps, ingredients);
    }
}
