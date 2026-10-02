package com.richfield.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Not used as launcher. PantryListActivity is the main entry point.
 * Kept so old references do not break the project.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Redirect to the real home screen
        startActivity(new Intent(this, PantryListActivity.class));
        finish();
    }
}
