package com.techticz.app.model.food.category;

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 5/9/18.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FoodCategory {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("subCategories")
    @Expose
    private List<String> subCategories = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<String> subCategories) {
        this.subCategories = subCategories;
    }

}
