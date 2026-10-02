package com.richfield.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantrymanager.adapter.PantryAdapter;
import com.richfield.smartpantrymanager.db.DatabaseHelper;
import com.richfield.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class PantryListActivity extends AppCompatActivity {

    private static final String TAG = "PantryListActivity";

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private PantryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_pantry_list);
            setTitle("My Pantry");

            dbHelper = new DatabaseHelper(this);
            recyclerView = findViewById(R.id.recyclerPantry);
            tvEmpty = findViewById(R.id.tvEmptyPantry);
            Button btnAdd = findViewById(R.id.btnAddIngredient);
            Button navPantry = findViewById(R.id.navPantry);
            Button navRecipes = findViewById(R.id.navRecipes);
            Button navSettings = findViewById(R.id.navSettings);

            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            }

            if (btnAdd != null) {
                btnAdd.setOnClickListener(v ->
                        startActivity(new Intent(this, AddEditIngredientActivity.class)));
            }
            if (navRecipes != null) {
                navRecipes.setOnClickListener(v ->
                        startActivity(new Intent(this, SuggestedRecipesActivity.class)));
            }
            if (navSettings != null) {
                navSettings.setOnClickListener(v ->
                        startActivity(new Intent(this, SettingsActivity.class)));
            }
            // navPantry stays on this screen
        } catch (Exception e) {
            Log.e(TAG, "onCreate crash", e);
            showCrashDialog(e);
        }
    }

    private void showCrashDialog(Exception e) {
        try {
            String msg = e.getClass().getSimpleName() + ":\n" + e.getMessage();
            if (e.getCause() != null) {
                msg += "\n\nCause: " + e.getCause().getMessage();
            }
            new AlertDialog.Builder(this)
                    .setTitle("App error")
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception ignored) {
            Toast.makeText(this, "Crash: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadPantry();
        } catch (Exception e) {
            Log.e(TAG, "onResume crash", e);
            showCrashDialog(e);
        }
    }

    private void loadPantry() {
        if (dbHelper == null) return;
        List<PantryItem> items = dbHelper.getAllPantryItems();
        if (items == null) items = new ArrayList<>();

        if (items.isEmpty()) {
            if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        } else {
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new PantryAdapter(items, new PantryAdapter.OnItemClickListener() {
                @Override
                public void onEditClick(PantryItem item) {
                    Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
                    intent.putExtra("item_id", item.getId());
                    startActivity(intent);
                }

                @Override
                public void onDeleteClick(PantryItem item) {
                    new AlertDialog.Builder(PantryListActivity.this)
                            .setTitle("Delete item")
                            .setMessage("Remove \"" + item.getName() + "\" from your pantry?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                dbHelper.deletePantryItem(item.getId());
                                Toast.makeText(PantryListActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                loadPantry();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
            if (recyclerView != null) recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(items);
        }
    }
}
