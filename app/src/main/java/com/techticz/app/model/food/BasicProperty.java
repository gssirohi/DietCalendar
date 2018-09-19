
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasicProperty {

    @SerializedName("servable")
    @Expose
    private Boolean servable;
    @SerializedName("availability")
    @Expose
    private Availability availability;
    @SerializedName("isNatural")
    @Expose
    private Boolean isNatural;
    @SerializedName("naturalForm")
    @Expose
    private String naturalForm;
    @SerializedName("prefRoutines")
    @Expose
    private PrefRoutines prefRoutines;

    public Boolean getServable() {
        return servable;
    }

    public void setServable(Boolean servable) {
        this.servable = servable;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public Boolean getIsNatural() {
        return isNatural;
    }

    public void setIsNatural(Boolean isNatural) {
        this.isNatural = isNatural;
    }

    public String getNaturalForm() {
        return naturalForm;
    }

    public void setNaturalForm(String naturalForm) {
        this.naturalForm = naturalForm;
    }

    public PrefRoutines getPrefRoutines() {
        return prefRoutines;
    }

    public void setPrefRoutines(PrefRoutines prefRoutines) {
        this.prefRoutines = prefRoutines;
    }

}
