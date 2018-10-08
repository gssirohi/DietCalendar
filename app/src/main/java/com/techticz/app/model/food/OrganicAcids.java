
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

}
