
package com.techticz.app.model.mealplate;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.constants.FoodServings;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcel;

import timber.log.Timber;

@Parcel
public class FoodItem {
    public FoodItem() {
    }

    public FoodItem(String id, Integer qty, String serving) {
        this.id = id;
        this.qty = qty;
        this.serving = serving;
    }

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("q")
    @Expose
    Integer qty;
    @SerializedName("s")
    @Expose
    String serving;

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

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }


    public FoodServings getFoodServing() {
        if(serving == null){
            Timber.w("Serving is null in FoodItem:"+id);
            return FoodServings.STANDARD_PORTION;
        } else {
            return FoodServings.Companion.getEnum(serving);
        }
    }
}
