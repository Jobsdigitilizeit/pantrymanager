package com.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** Binds recipe match results to item_recipe.xml. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {

    public interface Listener {
        void onClick(Recipe recipe);
    }

    private final List<RecipeMatcher.MatchResult> results = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setResults(List<RecipeMatcher.MatchResult> newResults) {
        results.clear();
        results.addAll(newResults);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        RecipeMatcher.MatchResult r = results.get(position);
        h.name.setText(r.recipe.getName());
        if (r.missing.isEmpty()) {
            h.info.setText(r.recipe.getIngredients().size() + " ingredients - you have them all");
        } else {
            h.info.setText("Missing: " + android.text.TextUtils.join(", ", r.missing));
        }
        h.itemView.setOnClickListener(v -> listener.onClick(r.recipe));
    }

    @Override
    public int getItemCount() { return results.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView name, info;

        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.textRecipeName);
            info = v.findViewById(R.id.textRecipeInfo);
        }
    }
}
