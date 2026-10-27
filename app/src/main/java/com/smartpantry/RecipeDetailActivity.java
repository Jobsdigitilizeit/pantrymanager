package com.smartpantry;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/** Shows the full ingredient list and method for the recipe id passed in by Intent. */
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra(SuggestedRecipesActivity.EXTRA_RECIPE_ID);
        if (id == null) {
            finish();
            return;
        }
        new PantryRepository().getRecipe(id, recipe -> {
            if (recipe == null) {
                finish();
                return;
            }
            setTitle(recipe.getName());
            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient ing : recipe.getIngredients()) {
                sb.append("• ").append(ing).append('\n');
            }
            ((TextView) findViewById(R.id.textIngredients)).setText(sb.toString().trim());
            ((TextView) findViewById(R.id.textSteps)).setText(recipe.getSteps());
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
