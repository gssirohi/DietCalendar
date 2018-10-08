
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrinciplesAndDietaryFibers {

    @SerializedName("moisture")
    @Expose
    private Float moisture;
    @SerializedName("protien")
    @Expose
    private Float protien;
    @SerializedName("ash")
    @Expose
    private Float ash;
    @SerializedName("fat")
    @Expose
    private Float fat;
    @SerializedName("dietaryFiber")
    @Expose
    private DietaryFiber dietaryFiber;
    @SerializedName("carbohydrate")
    @Expose
    private Float carbohydrate;
    @SerializedName("energy")
    @Expose
    private Float energy;

    public Float getMoisture() {
        return moisture;
    }

    public void setMoisture(Float moisture) {
        this.moisture = moisture;
    }

    public Float getProtien() {
        return protien;
    }

    public void setProtien(Float protien) {
        this.protien = protien;
    }

    public Float getAsh() {
        return ash;
    }

    public void setAsh(Float ash) {
        this.ash = ash;
    }

    public Float getFat() {
        return fat;
    }

    public void setFat(Float fat) {
        this.fat = fat;
    }

    public DietaryFiber getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(DietaryFiber dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public Float getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(Float carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public Float getEnergy() {
        return energy;
    }

    public void setEnergy(Float energy) {
        this.energy = energy;
    }

}
