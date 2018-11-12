
package com.techticz.app.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MealPref {

    @SerializedName("egg")
    @Expose
    private Boolean egg;
    @SerializedName("milk")
    @Expose
    private Boolean milk;
    @SerializedName("chicken")
    @Expose
    private Boolean chicken;
    @SerializedName("fish")
    @Expose
    private Boolean fish;
    @SerializedName("mutton")
    @Expose
    private Boolean mutton;
    @SerializedName("cheese")
    @Expose
    private Boolean cheese;
    @SerializedName("onion")
    @Expose
    private Boolean onion;
    @SerializedName("garlic")
    @Expose
    private Boolean garlic;

    public Boolean getEgg() {
        return egg;
    }

    public void setEgg(Boolean egg) {
        this.egg = egg;
    }

    public Boolean getChicken() {
        return chicken;
    }

    public void setChicken(Boolean chicken) {
        this.chicken = chicken;
    }

    public Boolean getFish() {
        return fish;
    }

    public void setFish(Boolean fish) {
        this.fish = fish;
    }

    public Boolean getMutton() {
        return mutton;
    }

    public void setMutton(Boolean mutton) {
        this.mutton = mutton;
    }

    public Boolean getCheese() {
        return cheese;
    }

    public void setCheese(Boolean cheese) {
        this.cheese = cheese;
    }

    public Boolean getOnion() {
        return onion;
    }

    public void setOnion(Boolean onion) {
        this.onion = onion;
    }

    public Boolean getGarlic() {
        return garlic;
    }

    public void setGarlic(Boolean garlic) {
        this.garlic = garlic;
    }

    public Boolean getMilk() {
        return milk;
    }

    public void setMilk(Boolean milk) {
        this.milk = milk;
    }
}
