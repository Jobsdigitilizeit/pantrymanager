package com.smartpantry;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cloud Firestore storage. Collections:
 *   pantry  - one document per ingredient (name, quantity, unit, expiry)
 *   recipes - one document per recipe (name, steps, ingredients as "name|qty|unit" strings)
 *
 * Reads use snapshot listeners, so screens update live. Writes are applied to the local
 * cache immediately and synced to the cloud, so the app also works offline.
 */
public class PantryRepository {
    private static final String TAG = "PantryRepository";

    public interface Callback<T> {
        void onResult(T result);
    }

    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    // ------------------------------------------------------------- pantry CRUD

    /** Create. */
    public void addItem(String name, double quantity, String unit, String expiry) {
        fs.collection("pantry").add(values(name, quantity, unit, expiry))
                .addOnFailureListener(e -> Log.e(TAG, "add failed", e));
    }

    /** Update. */
    public void updateItem(String id, String name, double quantity, String unit, String expiry) {
        fs.collection("pantry").document(id).set(values(name, quantity, unit, expiry))
                .addOnFailureListener(e -> Log.e(TAG, "update failed", e));
    }

    /** Delete. */
    public void deleteItem(String id) {
        fs.collection("pantry").document(id).delete()
                .addOnFailureListener(e -> Log.e(TAG, "delete failed", e));
    }

    /** Read one item once (null if it no longer exists). */
    public void getItem(String id, Callback<PantryItem> cb) {
        fs.collection("pantry").document(id).get()
                .addOnSuccessListener(d -> cb.onResult(d.exists() ? itemFrom(d) : null))
                .addOnFailureListener(e -> cb.onResult(null));
    }

    /** Read: live list of all items, alphabetical. Call remove() on the result in onStop. */
    public ListenerRegistration listenItems(Callback<List<PantryItem>> cb) {
        return fs.collection("pantry").addSnapshotListener((snap, err) -> {
            if (snap == null) {
                Log.e(TAG, "pantry listen failed", err);
                return;
            }
            List<PantryItem> items = new ArrayList<>();
            for (DocumentSnapshot d : snap.getDocuments()) items.add(itemFrom(d));
            Collections.sort(items, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            cb.onResult(items);
        });
    }

    private Map<String, Object> values(String name, double quantity, String unit, String expiry) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("quantity", quantity);
        m.put("unit", unit);
        m.put("expiry", expiry); // null clears the date
        return m;
    }

    private PantryItem itemFrom(DocumentSnapshot d) {
        Double qty = d.getDouble("quantity");
        return new PantryItem(d.getId(), String.valueOf(d.getString("name")),
                qty == null ? 0 : qty, String.valueOf(d.getString("unit")), d.getString("expiry"));
    }

    // ----------------------------------------------------------------- recipes

    /** Writes the 21 starter recipes once, if the recipes collection is still empty. */
    public void seedRecipesIfEmpty() {
        fs.collection("recipes").limit(1).get().addOnSuccessListener(snap -> {
            if (!snap.isEmpty()) return;
            WriteBatch batch = fs.batch();
            int[] n = {0};
            RecipeData.load((name, steps, ingredients) -> {
                Map<String, Object> m = new HashMap<>();
                m.put("name", name);
                m.put("steps", steps);
                m.put("ingredients", java.util.Arrays.asList(ingredients));
                // Fixed ids make seeding idempotent if two launches race.
                batch.set(fs.collection("recipes").document(String.format("r%02d", ++n[0])), m);
            });
            batch.commit().addOnFailureListener(e -> Log.e(TAG, "seed failed", e));
        });
    }

    /** Live list of all recipes (updates once seeding finishes). */
    public ListenerRegistration listenRecipes(Callback<List<Recipe>> cb) {
        return fs.collection("recipes").addSnapshotListener((snap, err) -> {
            if (snap == null) {
                Log.e(TAG, "recipes listen failed", err);
                return;
            }
            List<Recipe> recipes = new ArrayList<>();
            for (QueryDocumentSnapshot d : snap) recipes.add(recipeFrom(d));
            Collections.sort(recipes, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            cb.onResult(recipes);
        });
    }

    /** One recipe by id, or null. */
    public void getRecipe(String id, Callback<Recipe> cb) {
        fs.collection("recipes").document(id).get()
                .addOnSuccessListener(d -> cb.onResult(d.exists() ? recipeFrom(d) : null))
                .addOnFailureListener(e -> cb.onResult(null));
    }

    @SuppressWarnings("unchecked")
    private Recipe recipeFrom(DocumentSnapshot d) {
        Recipe r = new Recipe(d.getId(), String.valueOf(d.getString("name")),
                String.valueOf(d.getString("steps")));
        List<String> lines = (List<String>) d.get("ingredients");
        if (lines != null) {
            for (String line : lines) {
                String[] p = line.split("\\|");
                r.getIngredients().add(new RecipeIngredient(p[0], Double.parseDouble(p[1]), p[2]));
            }
        }
        return r;
    }
}
