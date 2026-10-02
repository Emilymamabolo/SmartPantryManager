package com.richfield.smartpantrymanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a recipe. Contains name, preparation steps and list of required ingredients.
 */
public class Recipe implements Serializable {
    private long id;
    private String name;
    private String steps;
    private List<RecipeIngredient> ingredients;

    public Recipe() {
        ingredients = new ArrayList<>();
    }

    public Recipe(String name, String steps) {
        this.name = name;
        this.steps = steps;
        this.ingredients = new ArrayList<>();
    }

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.ingredients = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
    }
}
