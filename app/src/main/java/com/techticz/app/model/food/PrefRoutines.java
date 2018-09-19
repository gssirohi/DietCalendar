
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrefRoutines {

    @SerializedName("earlyMorning")
    @Expose
    private Boolean earlyMorning;
    @SerializedName("breakfast")
    @Expose
    private Boolean breakfast;
    @SerializedName("lunch")
    @Expose
    private Boolean lunch;
    @SerializedName("eveningSnaks")
    @Expose
    private Boolean eveningSnaks;
    @SerializedName("dinner")
    @Expose
    private Boolean dinner;
    @SerializedName("bedTime")
    @Expose
    private Boolean bedTime;

    public Boolean getEarlyMorning() {
        return earlyMorning;
    }

    public void setEarlyMorning(Boolean earlyMorning) {
        this.earlyMorning = earlyMorning;
    }

    public Boolean getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(Boolean breakfast) {
        this.breakfast = breakfast;
    }

    public Boolean getLunch() {
        return lunch;
    }

    public void setLunch(Boolean lunch) {
        this.lunch = lunch;
    }

    public Boolean getEveningSnaks() {
        return eveningSnaks;
    }

    public void setEveningSnaks(Boolean eveningSnaks) {
        this.eveningSnaks = eveningSnaks;
    }

    public Boolean getDinner() {
        return dinner;
    }

    public void setDinner(Boolean dinner) {
        this.dinner = dinner;
    }

    public Boolean getBedTime() {
        return bedTime;
    }

    public void setBedTime(Boolean bedTime) {
        this.bedTime = bedTime;
    }

}
