
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.util.Utils;

public class StarchAndSugars {

    @SerializedName("cho")
    @Expose
    private Float cho;
    @SerializedName("starch")
    @Expose
    private Float starch;
    @SerializedName("fructose")
    @Expose
    private Float fructose;
    @SerializedName("glucose")
    @Expose
    private Float glucose;
    @SerializedName("sucrose")
    @Expose
    private Float sucrose;
    @SerializedName("maltos")
    @Expose
    private Float maltos;
    @SerializedName("totalFreeSugar")
    @Expose
    private Float totalFreeSugar;

    public Float getCho() {
        return cho;
    }

    public void setCho(Float cho) {
        this.cho = cho;
    }

    public Float getStarch() {
        return starch;
    }

    public void setStarch(Float starch) {
        this.starch = starch;
    }

    public Float getFructose() {
        return fructose;
    }

    public void setFructose(Float fructose) {
        this.fructose = fructose;
    }

    public Float getGlucose() {
        return glucose;
    }

    public void setGlucose(Float glucose) {
        this.glucose = glucose;
    }

    public Float getSucrose() {
        return sucrose;
    }

    public void setSucrose(Float sucrose) {
        this.sucrose = sucrose;
    }

    public Float getMaltos() {
        return maltos;
    }

    public void setMaltos(Float maltos) {
        this.maltos = maltos;
    }

    public Float getTotalFreeSugar() {
        return totalFreeSugar;
    }

    public void setTotalFreeSugar(Float totalFreeSugar) {
        this.totalFreeSugar = totalFreeSugar;
    }

    public StarchAndSugars applyFactor(Float finalQtyFactor) {
        StarchAndSugars starchAndSugars = new StarchAndSugars();
        starchAndSugars.cho = this.cho == null? 0f : this.cho * finalQtyFactor;
        starchAndSugars.fructose = this.fructose == null? 0f : this.fructose * finalQtyFactor;
        starchAndSugars.glucose = this.glucose == null? 0f : this.glucose * finalQtyFactor;
        starchAndSugars.maltos = this.maltos == null? 0f : this.maltos * finalQtyFactor;
        starchAndSugars.starch = this.starch == null? 0f : this.starch * finalQtyFactor;
        starchAndSugars.sucrose = this.sucrose == null? 0f : this.sucrose * finalQtyFactor;
        starchAndSugars.totalFreeSugar = this.totalFreeSugar == null? 0f : this.totalFreeSugar * finalQtyFactor;

        return starchAndSugars;
    }

    public void add(StarchAndSugars starchAndSugars) {
        if(starchAndSugars == null) return;
        this.cho = Utils.addFloats(this.cho,starchAndSugars.cho);
        this.fructose = Utils.addFloats(this.fructose,starchAndSugars.fructose);
        this.glucose = Utils.addFloats(this.glucose,starchAndSugars.glucose);
        this.maltos = Utils.addFloats(this.maltos,starchAndSugars.maltos);
        this.starch = Utils.addFloats(this.starch,starchAndSugars.starch);
        this.sucrose = Utils.addFloats(this.sucrose,starchAndSugars.sucrose);
        this.totalFreeSugar = Utils.addFloats(this.totalFreeSugar,starchAndSugars.totalFreeSugar);
    }
}
