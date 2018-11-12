
package com.techticz.app.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HealthProfile {

    @SerializedName("weight")
    @Expose
    private Float weight;
    @SerializedName("height")
    @Expose
    private Float height;
    @SerializedName("heightUnit")
    @Expose
    private String heightUnit;
    @SerializedName("activityLevel")
    @Expose
    private String activityLevel;
    @SerializedName("targetWeight")
    @Expose
    private Float targetWeight;

    @SerializedName("drink")
    @Expose
    private Boolean drink;

    @SerializedName("smoke")
    @Expose
    private Boolean smoke;

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Boolean getDrink() {
        return drink;
    }

    public void setDrink(Boolean drink) {
        this.drink = drink;
    }

    public Boolean getSmoke() {
        return smoke;
    }

    public void setSmoke(Boolean smoke) {
        this.smoke = smoke;
    }
}
