
package com.techticz.app.model.mealplate;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Items {
    public Items() {
    }

    @SerializedName("recipies")
    @Expose
    List<RecipeItem> recipies = null;
    @SerializedName("foods")
    @Expose
    List<FoodItem> foods = null;

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
