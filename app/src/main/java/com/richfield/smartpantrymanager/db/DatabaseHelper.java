package com.richfield.smartpantrymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.richfield.smartpantrymanager.model.PantryItem;
import com.richfield.smartpantrymanager.model.Recipe;
import com.richfield.smartpantrymanager.model.RecipeIngredient;
import com.richfield.smartpantrymanager.util.IngredientMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLiteOpenHelper for Smart Pantry Manager.
 * Tables: pantry_items, recipes, recipe_ingredients.
 * Seeds 18 recipes on first creation.
 * Provides full CRUD for pantry and recipe queries with strict matching.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 4;

    // Table names
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Common / Pantry columns
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // Recipe columns
    public static final String COL_STEPS = "steps";
    public static final String COL_RECIPE_ID = "recipe_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Pantry items table
        String createPantry = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY + " TEXT);";
        db.execSQL(createPantry);

        // Recipes table
        String createRecipes = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_STEPS + " TEXT NOT NULL);";
        db.execSQL(createRecipes);

        // Recipe ingredients table
        String createRecipeIngredients = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_ID + "));";
        db.execSQL(createRecipeIngredients);

        // Seed recipes
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ==================== PANTRY CRUD ====================

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName().trim());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit().trim());
        values.put(COL_EXPIRY, item.getExpiryDate() != null ? item.getExpiryDate() : "");
        long id = db.insert(TABLE_PANTRY, null, values);
        // db managed by SQLiteOpenHelper
        return id;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COL_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY))
                );
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // db managed by SQLiteOpenHelper
        return list;
    }

    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = new PantryItem(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY))
            );
        }
        cursor.close();
        // db managed by SQLiteOpenHelper
        return item;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName().trim());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit().trim());
        values.put(COL_EXPIRY, item.getExpiryDate() != null ? item.getExpiryDate() : "");
        int rows = db.update(TABLE_PANTRY, values, COL_ID + "=?",
                new String[]{String.valueOf(item.getId())});
        // db managed by SQLiteOpenHelper
        return rows;
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_ID + "=?", new String[]{String.valueOf(id)});
        // db managed by SQLiteOpenHelper
    }

    // ==================== RECIPES ====================

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COL_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                Recipe recipe = new Recipe(
                        id,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STEPS))
                );
                recipe.setIngredients(getIngredientsForRecipe(db, id));
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // db managed by SQLiteOpenHelper
        return recipes;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = new Recipe(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STEPS))
            );
            recipe.setIngredients(getIngredientsForRecipe(db, id));
        }
        cursor.close();
        // db managed by SQLiteOpenHelper
        return recipe;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new RecipeIngredient(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * STRICT MATCHING: returns only recipes where EVERY required ingredient
     * is present in the current pantry with sufficient quantity.
     * Uses IngredientMatcher for basic singular/plural and unit robustness.
     */
    public List<Recipe> getStrictMatchingRecipes() {
        List<PantryItem> pantry = getAllPantryItems();
        List<Recipe> allRecipes = getAllRecipes();
        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (IngredientMatcher.canMakeRecipe(recipe, pantry)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    // ==================== SEED DATA (18 recipes) ====================

    private void seedRecipes(SQLiteDatabase db) {
        // Preloaded recipe catalogue
        insertRecipe(db, "Herb Scrambled Eggs",
                "1. Beat eggs with salt until slightly frothy.\n2. Warm oil in a non-stick pan over medium heat.\n3. Pour in eggs and fold gently until just set.\n4. Serve immediately while soft and creamy.",
                new String[]{"egg", "oil", "salt"},
                new double[]{2, 1, 0.5},
                new String[]{"pcs", "tbsp", "tsp"});

        insertRecipe(db, "Classic Butter Toast",
                "1. Toast bread slices until golden.\n2. Spread softened butter evenly while toast is warm.\n3. Optional: light pinch of salt.\n4. Serve as a quick breakfast or side.",
                new String[]{"bread", "butter"},
                new double[]{2, 1},
                new String[]{"slices", "tbsp"});

        insertRecipe(db, "Spicy Tomato Shakshuka-Style Eggs",
                "1. Warm oil and cook chopped tomato until soft.\n2. Season with salt.\n3. Crack eggs into the pan and cover until whites set.\n4. Serve with bread if available.",
                new String[]{"egg", "tomato", "oil", "salt"},
                new double[]{2, 2, 1, 0.5},
                new String[]{"pcs", "pcs", "tbsp", "tsp"});

        insertRecipe(db, "Creamy Banana Oat Bowl",
                "1. Mash banana in a bowl.\n2. Stir in oats and milk until creamy.\n3. Sweeten lightly with sugar or honey if preferred.\n4. Enjoy as a no-cook breakfast.",
                new String[]{"banana", "oats", "milk"},
                new double[]{1, 0.5, 1},
                new String[]{"pcs", "cup", "cup"});

        insertRecipe(db, "Tomato Scrambled Eggs",
                "1. Beat eggs with salt.\n2. Heat oil, scramble eggs.\n3. Add chopped tomatoes and cook until soft.\n4. Serve hot.",
                new String[]{"egg", "tomato", "salt", "oil"},
                new double[]{3, 2, 0.5, 1},
                new String[]{"pcs", "pcs", "tsp", "tbsp"});

        insertRecipe(db, "Homestyle Egg Fried Rice",
                "1. Heat oil in a pan.\n2. Add leftover rice and stir.\n3. Add soy sauce, eggs and any vegetables.\n4. Fry until hot and serve.",
                new String[]{"rice", "egg", "soy sauce", "oil"},
                new double[]{2, 2, 2, 1},
                new String[]{"cups", "pcs", "tbsp", "tbsp"});

        insertRecipe(db, "Garlic Butter Pasta",
                "1. Boil pasta until al dente.\n2. Melt butter with minced garlic.\n3. Toss pasta in garlic butter, season with salt and pepper.\n4. Optional: add grated cheese.",
                new String[]{"pasta", "butter", "garlic", "salt"},
                new double[]{200, 2, 3, 0.5},
                new String[]{"g", "tbsp", "cloves", "tsp"});

        insertRecipe(db, "Banana Smoothie",
                "1. Peel bananas and place in blender.\n2. Add milk and a spoon of sugar or honey.\n3. Blend until smooth.\n4. Serve chilled.",
                new String[]{"banana", "milk", "sugar"},
                new double[]{2, 1, 1},
                new String[]{"pcs", "cup", "tbsp"});

        insertRecipe(db, "Cheese Omelette",
                "1. Beat eggs with salt and pepper.\n2. Pour into hot oiled pan.\n3. Sprinkle cheese when almost set.\n4. Fold and serve.",
                new String[]{"egg", "cheese", "salt", "oil"},
                new double[]{3, 50, 0.5, 1},
                new String[]{"pcs", "g", "tsp", "tbsp"});

        insertRecipe(db, "Potato Hash",
                "1. Dice potatoes and boil until just tender.\n2. Fry in oil with onion until golden and crispy.\n3. Season with salt and pepper.\n4. Serve as side or main.",
                new String[]{"potato", "onion", "oil", "salt"},
                new double[]{3, 1, 2, 1},
                new String[]{"pcs", "pcs", "tbsp", "tsp"});

        insertRecipe(db, "Cucumber Salad",
                "1. Slice cucumber thinly.\n2. Mix with vinegar, salt and a pinch of sugar.\n3. Optionally add sliced onion.\n4. Chill and serve.",
                new String[]{"cucumber", "vinegar", "salt", "sugar"},
                new double[]{1, 2, 0.5, 0.5},
                new String[]{"pcs", "tbsp", "tsp", "tsp"});

        insertRecipe(db, "Avocado Toast",
                "1. Toast bread slices.\n2. Mash avocado with salt and lemon juice.\n3. Spread on toast.\n4. Optional: top with egg or tomato.",
                new String[]{"bread", "avocado", "salt", "lemon"},
                new double[]{2, 1, 0.5, 0.5},
                new String[]{"slices", "pcs", "tsp", "pcs"});

        insertRecipe(db, "Peanut Butter Sandwich",
                "1. Spread peanut butter on one slice of bread.\n2. Optionally add jam or banana slices.\n3. Top with second slice.\n4. Cut and serve.",
                new String[]{"bread", "peanut butter"},
                new double[]{2, 2},
                new String[]{"slices", "tbsp"});

        insertRecipe(db, "Vegetable Stir Fry",
                "1. Heat oil in a wok or pan.\n2. Add garlic then mixed vegetables.\n3. Stir fry on high heat, add soy sauce.\n4. Serve with rice if available.",
                new String[]{"carrot", "broccoli", "garlic", "soy sauce", "oil"},
                new double[]{1, 1, 2, 2, 1},
                new String[]{"pcs", "cup", "cloves", "tbsp", "tbsp"});

        insertRecipe(db, "Yogurt Parfait",
                "1. Layer yogurt in a glass.\n2. Add honey and chopped fruit (banana or berries).\n3. Optional: sprinkle nuts or granola.\n4. Serve immediately.",
                new String[]{"yogurt", "honey", "banana"},
                new double[]{1, 1, 1},
                new String[]{"cup", "tbsp", "pcs"});

        insertRecipe(db, "Boiled Egg Salad",
                "1. Hard-boil eggs and cool.\n2. Chop eggs and mix with mayonnaise, salt and pepper.\n3. Add chopped cucumber or onion if available.\n4. Serve on bread or as is.",
                new String[]{"egg", "mayonnaise", "salt"},
                new double[]{3, 2, 0.5},
                new String[]{"pcs", "tbsp", "tsp"});

        insertRecipe(db, "Garlic Bread",
                "1. Mix softened butter with minced garlic and salt.\n2. Spread on bread slices.\n3. Toast in oven or pan until golden.\n4. Serve warm.",
                new String[]{"bread", "butter", "garlic", "salt"},
                new double[]{4, 2, 2, 0.5},
                new String[]{"slices", "tbsp", "cloves", "tsp"});

        insertRecipe(db, "Apple Cinnamon Bowl",
                "1. Slice apple and place in bowl.\n2. Sprinkle cinnamon and a little sugar.\n3. Optional: add yogurt or nuts.\n4. Enjoy as snack or breakfast.",
                new String[]{"apple", "cinnamon", "sugar"},
                new double[]{1, 0.5, 1},
                new String[]{"pcs", "tsp", "tsp"});

        insertRecipe(db, "Lemonade",
                "1. Squeeze lemons into a jug.\n2. Add water and sugar to taste.\n3. Stir well until sugar dissolves.\n4. Serve over ice.",
                new String[]{"lemon", "sugar", "water"},
                new double[]{3, 3, 4},
                new String[]{"pcs", "tbsp", "cups"});

        insertRecipe(db, "Cheese Toastie",
                "1. Place cheese between two bread slices.\n2. Butter the outside.\n3. Toast in pan or sandwich press until golden and cheese melts.\n4. Cut and serve.",
                new String[]{"bread", "cheese", "butter"},
                new double[]{2, 50, 1},
                new String[]{"slices", "g", "tbsp"});

        insertRecipe(db, "Fluffy Breakfast Pancakes",
                "1. Mix flour, egg, milk and a pinch of salt into a batter.\n2. Heat a lightly oiled pan.\n3. Pour batter and cook until bubbles form, then flip.\n4. Serve with sugar, honey or fruit.",
                new String[]{"flour", "egg", "milk", "oil"},
                new double[]{1, 1, 1, 1},
                new String[]{"cup", "pcs", "cup", "tbsp"});

        insertRecipe(db, "Savoury Onion Broth",
                "1. Slice onions and cook slowly in butter until caramelised.\n2. Add water or stock and simmer.\n3. Season with salt and pepper.\n4. Optional: top with bread and cheese.",
                new String[]{"onion", "butter", "salt", "water"},
                new double[]{3, 2, 1, 3},
                new String[]{"pcs", "tbsp", "tsp", "cups"});
    }

    private void insertRecipe(SQLiteDatabase db, String name, String steps,
                              String[] ingredientNames, double[] quantities, String[] units) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_NAME, name);
        recipeValues.put(COL_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (int i = 0; i < ingredientNames.length; i++) {
            ContentValues ingValues = new ContentValues();
            ingValues.put(COL_RECIPE_ID, recipeId);
            ingValues.put(COL_NAME, ingredientNames[i]);
            ingValues.put(COL_QUANTITY, quantities[i]);
            ingValues.put(COL_UNIT, units[i]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingValues);
        }
    }
}
