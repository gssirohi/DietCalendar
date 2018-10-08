
package com.techticz.app.model.dietplan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class DayPlan {
    public DayPlan() {
    }

    @SerializedName("earlyMorning")
    @Expose
    String earlyMorning;
    @SerializedName("breakfast")
    @Expose
    String breakfast;
    @SerializedName("lunch")
    @Expose
    String lunch;
    @SerializedName("brunch")
    @Expose
    String brunch;
    @SerializedName("dinner")
    @Expose
    String dinner;
    @SerializedName("bedTime")
    @Expose
    String bedTime;

    public String getEarlyMorning() {
        return earlyMorning;
    }

    public void setEarlyMorning(String earlyMorning) {
        this.earlyMorning = earlyMorning;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getBrunch() {
        return brunch;
    }

    public void setBrunch(String brunch) {
        this.brunch = brunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getBedTime() {
        return bedTime;
    }

    public void setBedTime(String bedTime) {
        this.bedTime = bedTime;
    }

}
