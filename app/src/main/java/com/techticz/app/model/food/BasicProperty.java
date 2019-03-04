
package com.techticz.app.model.food;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.db.converters.FoodTypeConverters;

import androidx.room.TypeConverters;

public class BasicProperty {

    @SerializedName("servable")
    @Expose
    private Boolean servable;
    @SerializedName("availability")
    @Expose
    private Integer availability;
    @SerializedName("isNatural")
    @Expose
    private Boolean isNatural;
    @SerializedName("naturalForm")
    @Expose
    private String naturalForm;
    @SerializedName("prefMeals")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    private List<String> prefMeals = null;

    public Boolean getServable() {
        return servable;
    }

    public void setServable(Boolean servable) {
        this.servable = servable;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
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

    public List<String> getPrefMeals() {
        return prefMeals;
    }

    public void setPrefMeals(List<String> prefMeals) {
        this.prefMeals = prefMeals;
    }

}
