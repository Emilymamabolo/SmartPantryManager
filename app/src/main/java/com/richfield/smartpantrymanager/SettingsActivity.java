package com.richfield.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartPantryPrefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_METRIC = "use_metric";

    private Switch switchExpiry;
    private Switch switchMetric;
    private TextView tvInfo;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        switchExpiry = findViewById(R.id.switchExpiryAlerts);
        switchMetric = findViewById(R.id.switchMetric);
        tvInfo = findViewById(R.id.tvSettingsInfo);
        Button navPantry = findViewById(R.id.navPantry);
        Button navRecipes = findViewById(R.id.navRecipes);

        switchExpiry.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        switchMetric.setChecked(prefs.getBoolean(KEY_METRIC, true));

        switchExpiry.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
            updateInfo();
        });

        switchMetric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_METRIC, isChecked).apply();
            updateInfo();
        });

        if (navPantry != null) {
            navPantry.setOnClickListener(v ->
                    startActivity(new Intent(this, PantryListActivity.class)));
        }
        if (navRecipes != null) {
            navRecipes.setOnClickListener(v ->
                    startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        }

        updateInfo();
    }

    private void updateInfo() {
        if (tvInfo == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Expiry alerts: ").append(switchExpiry.isChecked() ? "ON" : "OFF").append("\n");
        sb.append("Preferred units: ").append(switchMetric.isChecked() ? "Metric" : "Imperial");
        tvInfo.setText(sb.toString());
    }
}
