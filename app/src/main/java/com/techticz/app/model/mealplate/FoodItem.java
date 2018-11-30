
package com.techticz.app.model.mealplate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class FoodItem {
    public FoodItem() {
    }

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("qty")
    @Expose
    Integer qty;

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
