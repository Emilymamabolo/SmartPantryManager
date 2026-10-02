package com.richfield.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantrymanager.adapter.RecipeAdapter;
import com.richfield.smartpantrymanager.db.DatabaseHelper;
import com.richfield.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private RecipeAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        setTitle("Suggested Recipes");

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerRecipes);
        tvEmpty = findViewById(R.id.tvEmptyRecipes);
        Button navPantry = findViewById(R.id.navPantry);
        Button navSettings = findViewById(R.id.navSettings);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (navPantry != null) {
            navPantry.setOnClickListener(v ->
                    startActivity(new Intent(this, PantryListActivity.class)));
        }
        if (navSettings != null) {
            navSettings.setOnClickListener(v ->
                    startActivity(new Intent(this, SettingsActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<Recipe> matches = dbHelper.getStrictMatchingRecipes();
        if (matches == null) matches = new ArrayList<>();

        if (matches.isEmpty()) {
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText(getString(R.string.empty_recipes));
            }
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        } else {
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new RecipeAdapter(matches, recipe -> {
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipe_id", recipe.getId());
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(matches);
        }
    }
}
