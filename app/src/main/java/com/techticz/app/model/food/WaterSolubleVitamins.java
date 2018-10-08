
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WaterSolubleVitamins {

    @SerializedName("thiamineB1")
    @Expose
    private Float thiamineB1;
    @SerializedName("riboflavinB2")
    @Expose
    private Float riboflavinB2;
    @SerializedName("niacinB3")
    @Expose
    private Float niacinB3;
    @SerializedName("pentothenicAcidB5")
    @Expose
    private Float pentothenicAcidB5;
    @SerializedName("totalB6")
    @Expose
    private Float totalB6;
    @SerializedName("bioinB7")
    @Expose
    private Float bioinB7;
    @SerializedName("totalFolatesB9")
    @Expose
    private Float totalFolatesB9;
    @SerializedName("totalAscorbicAcid")
    @Expose
    private Float totalAscorbicAcid;

    public Float getThiamineB1() {
        return thiamineB1;
    }

    public void setThiamineB1(Float thiamineB1) {
        this.thiamineB1 = thiamineB1;
    }

    public Float getRiboflavinB2() {
        return riboflavinB2;
    }

    public void setRiboflavinB2(Float riboflavinB2) {
        this.riboflavinB2 = riboflavinB2;
    }

    public Float getNiacinB3() {
        return niacinB3;
    }

    public void setNiacinB3(Float niacinB3) {
        this.niacinB3 = niacinB3;
    }

    public Float getPentothenicAcidB5() {
        return pentothenicAcidB5;
    }

    public void setPentothenicAcidB5(Float pentothenicAcidB5) {
        this.pentothenicAcidB5 = pentothenicAcidB5;
    }

    public Float getTotalB6() {
        return totalB6;
    }

    public void setTotalB6(Float totalB6) {
        this.totalB6 = totalB6;
    }

    public Float getBioinB7() {
        return bioinB7;
    }

    public void setBioinB7(Float bioinB7) {
        this.bioinB7 = bioinB7;
    }

    public Float getTotalFolatesB9() {
        return totalFolatesB9;
    }

    public void setTotalFolatesB9(Float totalFolatesB9) {
        this.totalFolatesB9 = totalFolatesB9;
    }

    public Float getTotalAscorbicAcid() {
        return totalAscorbicAcid;
    }

    public void setTotalAscorbicAcid(Float totalAscorbicAcid) {
        this.totalAscorbicAcid = totalAscorbicAcid;
    }

}
