
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.util.Utils;

public class OrganicAcids {

    @SerializedName("citricAcid")
    @Expose
    private Float citricAcid;
    @SerializedName("mallicAcid")
    @Expose
    private Float mallicAcid;

    public Float getCitricAcid() {
        return citricAcid;
    }

    public void setCitricAcid(Float citricAcid) {
        this.citricAcid = citricAcid;
    }

    public Float getMallicAcid() {
        return mallicAcid;
    }

    public void setMallicAcid(Float mallicAcid) {
        this.mallicAcid = mallicAcid;
    }

    public OrganicAcids applyFactor(Float finalQtyFactor) {
        OrganicAcids organicAcids = new OrganicAcids();

        organicAcids.citricAcid = this.citricAcid == null? 0f : this.citricAcid * finalQtyFactor;
        organicAcids.mallicAcid = this.mallicAcid == null? 0f : this.mallicAcid * finalQtyFactor;

        return organicAcids;
    }

    public void add(OrganicAcids organicAcids) {
        this.citricAcid = Utils.addFloats(this.citricAcid,organicAcids.citricAcid);
        this.mallicAcid = Utils.addFloats(this.mallicAcid,organicAcids.mallicAcid);
    }
}
