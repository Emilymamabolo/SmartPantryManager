package com.richfield.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantrymanager.db.DatabaseHelper;
import com.richfield.smartpantrymanager.model.Recipe;
import com.richfield.smartpantrymanager.model.RecipeIngredient;

/**
 * Displays full details of a selected recipe: ingredients list + preparation steps.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        long recipeId = getIntent().getLongExtra("recipe_id", -1);
        if (recipeId == -1) {
            finish();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Recipe recipe = dbHelper.getRecipe(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        setTitle(recipe.getName());

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvSteps = findViewById(R.id.tvDetailSteps);

        tvName.setText(recipe.getName());

        StringBuilder sb = new StringBuilder();
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ing : recipe.getIngredients()) {
                sb.append("• ")
                        .append(ing.getQuantity())
                        .append(" ")
                        .append(ing.getUnit())
                        .append(" ")
                        .append(ing.getName())
                        .append("\n");
            }
        }
        tvIngredients.setText(sb.toString().trim());
        tvSteps.setText(recipe.getSteps());
    }
}
