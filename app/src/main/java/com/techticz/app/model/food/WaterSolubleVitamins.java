
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.util.Utils;

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

    public WaterSolubleVitamins applyFactor(Float finalQtyFactor) {
        WaterSolubleVitamins waterSolubleVitamins = new WaterSolubleVitamins();
        waterSolubleVitamins.bioinB7 = this.bioinB7 == null?0f: this.bioinB7 * finalQtyFactor;
        waterSolubleVitamins.niacinB3 = this.niacinB3 == null?0f: this.niacinB3 * finalQtyFactor;
        waterSolubleVitamins.pentothenicAcidB5 = this.pentothenicAcidB5 == null?0f: this.pentothenicAcidB5 * finalQtyFactor;
        waterSolubleVitamins.riboflavinB2 = this.riboflavinB2 == null?0f: this.riboflavinB2 * finalQtyFactor;
        waterSolubleVitamins.thiamineB1 = this.thiamineB1 == null?0f: this.thiamineB1 * finalQtyFactor;
        waterSolubleVitamins.totalAscorbicAcid = this.totalAscorbicAcid == null?0f: this.totalAscorbicAcid * finalQtyFactor;
        waterSolubleVitamins.totalB6 = this.totalB6 == null?0f: this.totalB6 * finalQtyFactor;
        waterSolubleVitamins.totalFolatesB9 = this.totalFolatesB9 == null?0f: this.totalFolatesB9 * finalQtyFactor;

        return waterSolubleVitamins;
    }

    public void add(WaterSolubleVitamins waterSolubleVitamins) {
        if(waterSolubleVitamins == null) return;
        this.bioinB7 = Utils.addFloats(this.bioinB7,waterSolubleVitamins.bioinB7);
        this.niacinB3 = Utils.addFloats(this.niacinB3,waterSolubleVitamins.niacinB3);
        this.pentothenicAcidB5 = Utils.addFloats(this.pentothenicAcidB5,waterSolubleVitamins.pentothenicAcidB5);
        this.riboflavinB2 = Utils.addFloats(this.riboflavinB2,waterSolubleVitamins.riboflavinB2);
        this.thiamineB1 = Utils.addFloats(this.thiamineB1,waterSolubleVitamins.thiamineB1);
        this.totalAscorbicAcid = Utils.addFloats(this.totalAscorbicAcid,waterSolubleVitamins.totalAscorbicAcid);
        this.totalB6 = Utils.addFloats(this.totalB6,waterSolubleVitamins.totalB6);
        this.totalFolatesB9 = Utils.addFloats(this.totalFolatesB9,waterSolubleVitamins.totalFolatesB9);
    }
}
