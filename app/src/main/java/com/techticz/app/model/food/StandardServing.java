
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StandardServing {

    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("servingType")
    @Expose
    private String servingType;
    @SerializedName("perServeQty")
    @Expose
    private Float perServeQty;

    @SerializedName("perServeQtyUnit")
    @Expose
    private String perServeQtyUnit;
    
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

    public Float getPerServeQty() {
        return perServeQty;
    }

    public void setPerServeQty(Float perServeQty) {
        this.perServeQty = perServeQty;
    }

    public String getPerServeQtyUnit() {
        return perServeQtyUnit;
    }

    public void setPerServeQtyUnit(String perServeQtyUnit) {
        this.perServeQtyUnit = perServeQtyUnit;
    }
}
