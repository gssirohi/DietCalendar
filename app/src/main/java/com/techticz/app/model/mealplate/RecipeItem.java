
package com.techticz.app.model.mealplate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeItem {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("qty")
    @Expose
    private Integer qty;

    public RecipeItem() {
    }

    public RecipeItem(String id, Integer qty) {
        this.id = id;
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

}
