
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FatyAcid {

    @SerializedName("totalSaturatedFatyAcids")
    @Expose
    private Float totalSaturatedFatyAcids;
    @SerializedName("totalMonoUnsaturatedFattyAcids")
    @Expose
    private Float totalMonoUnsaturatedFattyAcids;
    @SerializedName("totalPolyUnsaturatedFattyAcids")
    @Expose
    private Float totalPolyUnsaturatedFattyAcids;
    @SerializedName("cholesterol")
    @Expose
    private Float cholesterol;

    public Float getTotalSaturatedFatyAcids() {
        return totalSaturatedFatyAcids;
    }

    public void setTotalSaturatedFatyAcids(Float totalSaturatedFatyAcids) {
        this.totalSaturatedFatyAcids = totalSaturatedFatyAcids;
    }

    public Float getTotalMonoUnsaturatedFattyAcids() {
        return totalMonoUnsaturatedFattyAcids;
    }

    public void setTotalMonoUnsaturatedFattyAcids(Float totalMonoUnsaturatedFattyAcids) {
        this.totalMonoUnsaturatedFattyAcids = totalMonoUnsaturatedFattyAcids;
    }

    public Float getTotalPolyUnsaturatedFattyAcids() {
        return totalPolyUnsaturatedFattyAcids;
    }

    public void setTotalPolyUnsaturatedFattyAcids(Float totalPolyUnsaturatedFattyAcids) {
        this.totalPolyUnsaturatedFattyAcids = totalPolyUnsaturatedFattyAcids;
    }

    public Float getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(Float cholesterol) {
        this.cholesterol = cholesterol;
    }

}
