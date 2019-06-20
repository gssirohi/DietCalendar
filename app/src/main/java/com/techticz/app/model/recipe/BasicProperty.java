
package com.techticz.app.model.recipe;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.db.converters.FoodTypeConverters;

import org.parceler.Parcel;

import androidx.room.TypeConverters;

@Parcel
public class BasicProperty {
    public BasicProperty() {
    }

    @SerializedName("course")
    @Expose
    String course;
    @SerializedName("cookTime")
    @Expose
    String cookTime;
    @SerializedName("availability")
    @Expose
    Integer availability;
    @SerializedName("prefMeals")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    List<String> prefMeals = null;

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public List<String> getPrefMeals() {
        return prefMeals;
    }

    public void setPrefMeals(List<String> prefMeals) {
        this.prefMeals = prefMeals;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }
}
