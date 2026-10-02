package com.richfield.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantrymanager.R;
import com.richfield.smartpantrymanager.model.Recipe;

import java.util.List;

/**
 * Adapter for displaying suggested (or all) recipes in a RecyclerView.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    public void updateData(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvName.setText(recipe.getName());
        int count = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
        holder.tvInfo.setText(count + " ingredients");

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRecipeName);
            tvInfo = itemView.findViewById(R.id.tvRecipeInfo);
        }
    }
}
