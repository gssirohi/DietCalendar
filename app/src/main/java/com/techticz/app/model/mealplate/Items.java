
package com.techticz.app.model.mealplate;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.db.converters.FoodTypeConverters;

import org.parceler.Parcel;

import androidx.room.Embedded;
import androidx.room.TypeConverters;

@Parcel
public class Items {
    public Items() {
    }

    @SerializedName("recipies")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    List<RecipeItem> recipies = null;
    @SerializedName("foods")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
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
