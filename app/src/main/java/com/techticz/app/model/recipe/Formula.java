
package com.techticz.app.model.recipe;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.model.mealplate.FoodItem;

import org.parceler.Parcel;

@Parcel
public class Formula {
    public Formula() {
        ingredients = new ArrayList();
        steps = new ArrayList();
    }

    @SerializedName("ingredients")
    @Expose
    List<FoodItem> ingredients = null;
    @SerializedName("steps")
    @Expose
    List<String> steps = null;

    public List<FoodItem> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<FoodItem> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

}
