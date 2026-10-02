package com.richfield.smartpantrymanager.util;

import com.richfield.smartpantrymanager.model.PantryItem;
import com.richfield.smartpantrymanager.model.Recipe;
import com.richfield.smartpantrymanager.model.RecipeIngredient;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Strict matching: every recipe ingredient must exist in pantry with enough quantity.
 * Names are compared case-insensitively after trim.
 */
public class IngredientMatcher {

    public static boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantry) {
        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return false;
        }

        Map<String, Double> pantryMap = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = normalise(item.getName());
            double current = pantryMap.containsKey(key) ? pantryMap.get(key) : 0.0;
            pantryMap.put(key, current + item.getQuantity());
        }

        for (RecipeIngredient needed : recipe.getIngredients()) {
            String key = normalise(needed.getName());
            Double available = pantryMap.get(key);
            if (available == null) {
                // try simple singular/plural alternate
                available = pantryMap.get(altPlural(key));
            }
            if (available == null || available < needed.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    public static String normalise(String name) {
        if (name == null) return "";
        return name.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    private static String altPlural(String n) {
        if (n.endsWith("s") && n.length() > 3 && !n.endsWith("ss") && !n.endsWith("sauce")) {
            return n.substring(0, n.length() - 1);
        }
        if (!n.endsWith("s")) {
            return n + "s";
        }
        return n;
    }
}
