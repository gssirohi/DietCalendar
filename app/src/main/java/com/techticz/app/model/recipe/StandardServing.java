
package com.techticz.app.model.recipe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class StandardServing {
    public StandardServing() {
    }

    @SerializedName("qty")
    @Expose
    Integer qty;
    @SerializedName("servingType")
    @Expose
    String servingType;

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getServingType() {
        return servingType;
    }

    public void setServingType(String servingType) {
        this.servingType = servingType;
    }

}
