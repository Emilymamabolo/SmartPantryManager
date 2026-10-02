package com.richfield.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantrymanager.db.DatabaseHelper;
import com.richfield.smartpantrymanager.model.PantryItem;

/**
 * Activity for adding a new pantry item or editing an existing one.
 * Includes input validation.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etExpiry;
    private Spinner spinnerUnit;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private long itemId = -1; // -1 means new item

    private static final String[] UNITS = {"pcs", "g", "kg", "ml", "l", "cup", "cups",
            "tbsp", "tsp", "cloves", "slices", "pinch"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        // Check if we are editing
        if (getIntent().hasExtra("item_id")) {
            itemId = getIntent().getLongExtra("item_id", -1);
            setTitle("Edit Ingredient");
            loadItem(itemId);
        } else {
            setTitle("Add Ingredient");
        }

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void loadItem(long id) {
        PantryItem item = dbHelper.getPantryItem(id);
        if (item != null) {
            etName.setText(item.getName());
            etQuantity.setText(String.valueOf(item.getQuantity()));
            etExpiry.setText(item.getExpiryDate());
            // Select unit in spinner
            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equalsIgnoreCase(item.getUnit())) {
                    spinnerUnit.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(qtyStr)) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                etQuantity.setError("Quantity must be greater than 0");
                etQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        // Optional simple date format check (YYYY-MM-DD)
        if (!TextUtils.isEmpty(expiry) && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            etExpiry.setError("Use format YYYY-MM-DD or leave blank");
            etExpiry.requestFocus();
            return;
        }

        PantryItem item = new PantryItem(name, quantity, unit, expiry);

        if (itemId == -1) {
            long id = dbHelper.addPantryItem(item);
            if (id > 0) {
                Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
            }
        } else {
            item.setId(itemId);
            int rows = dbHelper.updatePantryItem(item);
            if (rows > 0) {
                Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
