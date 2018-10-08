
package com.techticz.app.model.mealplate;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Items {

    @SerializedName("recipies")
    @Expose
    private List<RecipeItem> recipies = null;
    @SerializedName("foods")
    @Expose
    private List<FoodItem> foods = null;

    public List<RecipeItem> getRecipies() {
        return recipies;
    }

    public void setRecipies(List<RecipeItem> recipies) {
        this.recipies = recipies;
    }

    public List<FoodItem> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodItem> foods) {
        this.foods = foods;
    }

}
