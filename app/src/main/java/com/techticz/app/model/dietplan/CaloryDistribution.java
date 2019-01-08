
package com.techticz.app.model.dietplan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class CaloryDistribution {

    public CaloryDistribution() {
    }

    @SerializedName("earlyMorning")
    @Expose
    Integer earlyMorning;

    @SerializedName("breakfast")
    @Expose
    Integer breakfast;

    @SerializedName("lunch")
    @Expose
    Integer lunch;

    @SerializedName("eveningSnacks")
    @Expose
    Integer eveningSnacks;

    @SerializedName("dinner")
    @Expose
    Integer dinner;

    @SerializedName("bedTime")
    @Expose
    Integer bedTime;

    public CaloryDistribution(int em, int bf, int ln, int es, int dn, int bt) {
        earlyMorning = em;
        breakfast = bf;
        lunch = ln;
        eveningSnacks = es;
        dinner = dn;
        bedTime = bt;
    }

    public Integer getEarlyMorning() {
        return earlyMorning;
    }

    public void setEarlyMorning(Integer earlyMorning) {
        this.earlyMorning = earlyMorning;
    }

    public Integer getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(Integer breakfast) {
        this.breakfast = breakfast;
    }

    public Integer getLunch() {
        return lunch;
    }

    public void setLunch(Integer lunch) {
        this.lunch = lunch;
    }

    public Integer getEveningSnacks() {
        return eveningSnacks;
    }

    public void setEveningSnacks(Integer eveningSnacks) {
        this.eveningSnacks = eveningSnacks;
    }

    public Integer getDinner() {
        return dinner;
    }

    public void setDinner(Integer dinner) {
        this.dinner = dinner;
    }

    public Integer getBedTime() {
        return bedTime;
    }

    public void setBedTime(Integer bedTime) {
        this.bedTime = bedTime;
    }
}
