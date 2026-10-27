package com.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/** Runs the strict-matching rule and lists only recipes the user can cook right now. */
public class SuggestedRecipesActivity extends BaseActivity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private PantryRepository repo;
    private RecipeAdapter strictAdapter, almostAdapter;
    private TextView emptyView, almostHeader;
    private RecyclerView almostList;
    private ListenerRegistration pantryListener, recipeListener;

    private List<PantryItem> pantry; // null until the first snapshot arrives
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle(R.string.menu_suggested);

        repo = new PantryRepository();
        emptyView = findViewById(R.id.textEmpty);
        almostHeader = findViewById(R.id.textAlmostHeader);
        almostList = findViewById(R.id.recyclerAlmost);

        RecyclerView strictList = findViewById(R.id.recyclerStrict);
        strictList.setLayoutManager(new LinearLayoutManager(this));
        almostList.setLayoutManager(new LinearLayoutManager(this));

        strictAdapter = new RecipeAdapter(this::openDetail);
        almostAdapter = new RecipeAdapter(this::openDetail);
        strictList.setAdapter(strictAdapter);
        almostList.setAdapter(almostAdapter);
    }

    /** Listen to both collections; the match re-runs whenever either one changes. */
    @Override
    protected void onStart() {
        super.onStart();
        pantryListener = repo.listenItems(items -> { pantry = items; recompute(); });
        recipeListener = repo.listenRecipes(list -> { recipes = list; recompute(); });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pantryListener != null) pantryListener.remove();
        if (recipeListener != null) recipeListener.remove();
    }

    private void recompute() {
        if (pantry == null || recipes == null) return; // wait for both
        List<RecipeMatcher.MatchResult> all = RecipeMatcher.matchAll(recipes, pantry);

        List<RecipeMatcher.MatchResult> strict = RecipeMatcher.strictMatches(all);
        strictAdapter.setResults(strict);
        emptyView.setVisibility(strict.isEmpty() ? View.VISIBLE : View.GONE);

        // Optional bonus list, kept visually separate from the strict suggestions.
        boolean showAlmost = Prefs.almostThere(this);
        List<RecipeMatcher.MatchResult> almost = RecipeMatcher.almostThere(all);
        almostAdapter.setResults(almost);
        int vis = showAlmost && !almost.isEmpty() ? View.VISIBLE : View.GONE;
        almostHeader.setVisibility(vis);
        almostList.setVisibility(vis);
    }

    private void openDetail(Recipe recipe) {
        Intent i = new Intent(this, RecipeDetailActivity.class);
        i.putExtra(EXTRA_RECIPE_ID, recipe.getId());
        startActivity(i);
    }
}
