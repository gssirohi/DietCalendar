
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.util.Utils;

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

    public FatyAcid applyFactor(Float finalQtyFactor) {
        FatyAcid fatyAcid = new FatyAcid();
        fatyAcid.cholesterol = this.cholesterol == null? 0f : this.cholesterol * finalQtyFactor;
        fatyAcid.totalMonoUnsaturatedFattyAcids = this.totalMonoUnsaturatedFattyAcids == null? 0f : this.totalMonoUnsaturatedFattyAcids * finalQtyFactor;
        fatyAcid.totalPolyUnsaturatedFattyAcids = this.totalPolyUnsaturatedFattyAcids == null? 0f : this.totalPolyUnsaturatedFattyAcids * finalQtyFactor;
        fatyAcid.totalSaturatedFatyAcids = this.totalSaturatedFatyAcids == null? 0f : this.totalSaturatedFatyAcids * finalQtyFactor;

        return fatyAcid;
    }

    public void add(FatyAcid fatyAcid) {
        this.cholesterol = Utils.addFloats(this.cholesterol,fatyAcid.cholesterol);
        this.totalMonoUnsaturatedFattyAcids = Utils.addFloats(this.totalMonoUnsaturatedFattyAcids,fatyAcid.totalMonoUnsaturatedFattyAcids);
        this.totalPolyUnsaturatedFattyAcids = Utils.addFloats(this.totalPolyUnsaturatedFattyAcids,fatyAcid.totalPolyUnsaturatedFattyAcids);
        this.totalSaturatedFatyAcids = Utils.addFloats(this.totalSaturatedFatyAcids,fatyAcid.totalSaturatedFatyAcids);
    }
}
