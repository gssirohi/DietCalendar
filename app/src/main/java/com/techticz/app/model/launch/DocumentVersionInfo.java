
package com.techticz.app.model.launch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocumentVersionInfo {

    @SerializedName("food")
    @Expose
    private Integer food;

    @SerializedName("recipe")
    @Expose
    private Integer recipe;

    @SerializedName("plate")
    @Expose
    private Integer plate;

    @SerializedName("plan")
    @Expose
    private Integer plan;

    public Integer getFood() {
        return food;
    }

    public void setFood(Integer food) {
        this.food = food;
    }

    public Integer getRecipe() {
        return recipe;
    }

    public void setRecipe(Integer recipe) {
        this.recipe = recipe;
    }

    public Integer getPlate() {
        return plate;
    }

    public void setPlate(Integer plate) {
        this.plate = plate;
    }

    public Integer getPlan() {
        return plan;
    }

    public void setPlan(Integer plan) {
        this.plan = plan;
    }
}
