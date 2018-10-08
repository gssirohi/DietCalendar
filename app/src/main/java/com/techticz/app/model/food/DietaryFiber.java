
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DietaryFiber {

    @SerializedName("total")
    @Expose
    private Float total;
    @SerializedName("soluble")
    @Expose
    private Float soluble;
    @SerializedName("inSoluble")
    @Expose
    private Float inSoluble;

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getSoluble() {
        return soluble;
    }

    public void setSoluble(Float soluble) {
        this.soluble = soluble;
    }

    public Float getInSoluble() {
        return inSoluble;
    }

    public void setInSoluble(Float inSoluble) {
        this.inSoluble = inSoluble;
    }

}
