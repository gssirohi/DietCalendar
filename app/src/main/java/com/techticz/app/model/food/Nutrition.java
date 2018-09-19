
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nutrition {

    @SerializedName("perQty")
    @Expose
    private Integer perQty;
    @SerializedName("perQtyUnit")
    @Expose
    private String perQtyUnit;
    @SerializedName("nutrients")
    @Expose
    private Nutrients nutrients;

    public Integer getPerQty() {
        return perQty;
    }

    public void setPerQty(Integer perQty) {
        this.perQty = perQty;
    }

    public String getPerQtyUnit() {
        return perQtyUnit;
    }

    public void setPerQtyUnit(String perQtyUnit) {
        this.perQtyUnit = perQtyUnit;
    }

    public Nutrients getNutrients() {
        return nutrients;
    }

    public void setNutrients(Nutrients nutrients) {
        this.nutrients = nutrients;
    }

}
