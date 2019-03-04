
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;

public class StandardServing {

    @SerializedName("portion")
    @Expose
    @ColumnInfo(name = "ser_portion")
    private Integer portion;
    @SerializedName("servingUnit")
    @Expose
    private String servingUnit;

    
    public Integer getPortion() {
        return portion;
    }

    public void setPortion(Integer portion) {
        this.portion = portion;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

}
