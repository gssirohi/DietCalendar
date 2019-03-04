
package com.techticz.app.model.dietplan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.db.converters.FoodTypeConverters;

import org.parceler.Parcel;

import androidx.room.Embedded;
import androidx.room.TypeConverters;

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
    @TypeConverters(FoodTypeConverters.class)
    DayPlan monday;
    @SerializedName("tuesday")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    DayPlan tuesday;
    @SerializedName("wednesday")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    DayPlan wednesday;
    @SerializedName("thursday")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    DayPlan thursday;
    @SerializedName("friday")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    DayPlan friday;
    @SerializedName("saturday")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    DayPlan saturday;
    @SerializedName("sunday")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
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
