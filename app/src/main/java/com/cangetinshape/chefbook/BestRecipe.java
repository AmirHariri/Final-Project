package com.cangetinshape.chefbook;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Amir on 12/8/2017.
 */

public class BestRecipe implements Serializable {
    public String recipeTitle;
    public String category;
    public int servings;
    public int prepTime;
    public int cookingTime;
    public int yeildTime;
    public int totalTime;

    public String ingredients;
    public String amount;
    public String scale;

    public String steps;
    public ArrayList<String> images;

    public String userName;
    public String userID;

    public BestRecipe(){
    }

    public BestRecipe(String recipeTitle, String category, int servings, int prepTime, int cookingTime,
                      int yeildTime, int totalTime, String ingredients, String amount, String scale, String  steps,
                      ArrayList<String> images, String userName, String userID) {
        this.recipeTitle = recipeTitle;
        this.category = category;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookingTime = cookingTime;
        this.yeildTime = yeildTime;
        this.totalTime = totalTime;
        this.ingredients = ingredients;
        this.amount = amount;
        this.scale = scale;
        this.steps = steps;
        this.images = images;
        this.userName = userName;
        this.userID = userID;


    }
    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getCategory() {
        return category;
    }

    public int getServings() {
        return servings;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public int getYeildTime() {
        return yeildTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getAmount() {
        return amount;
    }

    public String getScale() {
        return scale;
    }

    public String getSteps() {
        return steps;
    }

    public ArrayList<String> getImages() {
        return images;
    }
    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getUserName() {
        return userName;
    }
    public String getUserID() {
        return userID;
    }



    public static class Ingredients{
        String mIngredients;
        String mAmounts;
        String mScales;



        public Ingredients(String ingredients, String amounts, String scales){
            this.mIngredients = ingredients;
            this.mAmounts = amounts;
            this.mScales = scales;
        }
        public String getIngredients() {
            return mIngredients;
        }

        public String getAmounts() {
            return mAmounts;
        }

        public String getScales() {
            return mScales;
        }

    }



}

