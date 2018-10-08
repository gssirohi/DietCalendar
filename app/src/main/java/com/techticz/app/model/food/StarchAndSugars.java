
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

}
