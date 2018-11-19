
package com.techticz.app.model.dietplan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Calendar {

    public Calendar() {
        monday = new DayPlan();
        tuesday = new DayPlan();
        wednesday = new DayPlan();
        thursday = new DayPlan();
        friday = new DayPlan();
        saturday = new DayPlan();
        sunday = new DayPlan();
    }

    @SerializedName("monday")
    @Expose
    DayPlan monday;
    @SerializedName("tuesday")
    @Expose
    DayPlan tuesday;
    @SerializedName("wednesday")
    @Expose
    DayPlan wednesday;
    @SerializedName("thursday")
    @Expose
    DayPlan thursday;
    @SerializedName("friday")
    @Expose
    DayPlan friday;
    @SerializedName("saturday")
    @Expose
    DayPlan saturday;
    @SerializedName("sunday")
    @Expose
    DayPlan sunday;

    public DayPlan getMonday() {
        return monday;
    }

    public void setMonday(DayPlan monday) {
        this.monday = monday;
    }

    public DayPlan getTuesday() {
        return tuesday;
    }

    public void setTuesday(DayPlan tuesday) {
        this.tuesday = tuesday;
    }

    public DayPlan getWednesday() {
        return wednesday;
    }

    public void setWednesday(DayPlan wednesday) {
        this.wednesday = wednesday;
    }

    public DayPlan getThursday() {
        return thursday;
    }

    public void setThursday(DayPlan thursday) {
        this.thursday = thursday;
    }

    public DayPlan getFriday() {
        return friday;
    }

    public void setFriday(DayPlan friday) {
        this.friday = friday;
    }

    public DayPlan getSaturday() {
        return saturday;
    }

    public void setSaturday(DayPlan saturday) {
        this.saturday = saturday;
    }

    public DayPlan getSunday() {
        return sunday;
    }

    public void setSunday(DayPlan sunday) {
        this.sunday = sunday;
    }
}
