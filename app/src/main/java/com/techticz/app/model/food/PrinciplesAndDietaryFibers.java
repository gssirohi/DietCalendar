
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.util.Utils;

import androidx.room.Embedded;

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
    @Embedded
    private DietaryFiber dietaryFiber = new DietaryFiber();
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

    public PrinciplesAndDietaryFibers applyFactor(Float finalQtyFactor) {
        PrinciplesAndDietaryFibers principlesAndDietaryFibers = new PrinciplesAndDietaryFibers();

        principlesAndDietaryFibers.setEnergy(energy == null?0f :finalQtyFactor * energy);
        principlesAndDietaryFibers.setAsh(ash == null?0f :finalQtyFactor * ash);
        principlesAndDietaryFibers.setProtien(protien == null?0f :finalQtyFactor * protien);
        principlesAndDietaryFibers.setCarbohydrate(carbohydrate == null?0f :finalQtyFactor * carbohydrate);
        principlesAndDietaryFibers.setFat(fat == null?0f :finalQtyFactor * fat);
        principlesAndDietaryFibers.setMoisture(moisture == null?0f :finalQtyFactor * moisture);
        principlesAndDietaryFibers.setDietaryFiber(dietaryFiber == null?new DietaryFiber():dietaryFiber.applyFactor(finalQtyFactor));

        return principlesAndDietaryFibers;

    }

    public void add(PrinciplesAndDietaryFibers principlesAndDietaryFibers) {
        if(principlesAndDietaryFibers == null) return;
        this.energy = Utils.addFloats(this.energy,principlesAndDietaryFibers.energy);
        this.ash = Utils.addFloats(this.ash,principlesAndDietaryFibers.ash);
        this.protien = Utils.addFloats(this.protien,principlesAndDietaryFibers.protien);
        this.carbohydrate = Utils.addFloats(this.carbohydrate,principlesAndDietaryFibers.carbohydrate);
        this.fat = Utils.addFloats(this.fat,principlesAndDietaryFibers.fat);
        this.moisture = Utils.addFloats(this.moisture,principlesAndDietaryFibers.moisture);
        this.dietaryFiber.add(principlesAndDietaryFibers.dietaryFiber);
    }
}
