
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;

public class StandardServing {

    @SerializedName("measPortion")
    @Expose
    //@ColumnInfo(name = "ser_portion")
    private Integer measPortion;
    @SerializedName("measPortionUnit")
    @Expose
    private String measPortionUnit;

    @SerializedName("stdPortion")
    @Expose
    private Integer stdPortion;

    @SerializedName("popularServing")
    @Expose
    private Integer popularServing;

    @SerializedName("popularServingType")
    @Expose
    private String popularServingType;

    public Integer getMeasPortion() {
        return measPortion;
    }

    public void setMeasPortion(Integer measPortion) {
        this.measPortion = measPortion;
    }

    public String getMeasPortionUnit() {
        return measPortionUnit;
    }

    public void setMeasPortionUnit(String measPortionUnit) {
        this.measPortionUnit = measPortionUnit;
    }

    public Integer getStdPortion() {
        return stdPortion;
    }

    public void setStdPortion(Integer stdPortion) {
        this.stdPortion = stdPortion;
    }

    public Integer getPopularServing() {
        return popularServing;
    }

    public void setPopularServing(Integer popularServing) {
        this.popularServing = popularServing;
    }

    public String getPopularServingType() {
        return popularServingType;
    }

    public void setPopularServingType(String popularServingType) {
        this.popularServingType = popularServingType;
    }
}
