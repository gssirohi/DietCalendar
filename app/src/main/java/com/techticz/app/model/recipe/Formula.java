
package com.techticz.app.model.recipe;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.db.converters.FoodTypeConverters;
import com.techticz.app.model.mealplate.FoodItem;

import org.parceler.Parcel;

import androidx.room.TypeConverters;

@Parcel
public class Formula {
    public Formula() {
        ingredients = new ArrayList();
        steps = new ArrayList();
    }

    @SerializedName("ingredients")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    List<FoodItem> ingredients = null;
    @SerializedName("steps")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    ArrayList<String> steps = new ArrayList();

    public List<FoodItem> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<FoodItem> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }

}
