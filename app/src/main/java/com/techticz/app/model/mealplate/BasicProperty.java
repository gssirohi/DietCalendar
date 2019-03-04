
package com.techticz.app.model.mealplate;

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

}
