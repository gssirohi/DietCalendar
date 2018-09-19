package com.techticz.app.model.recipe.category;

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 5/9/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeCategory {

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
