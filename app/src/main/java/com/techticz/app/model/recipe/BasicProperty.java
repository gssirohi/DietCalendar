
package com.techticz.app.model.recipe;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class BasicProperty {
    public BasicProperty() {
    }

    @SerializedName("availability")
    @Expose
    Integer availability;
    @SerializedName("prefMeals")
    @Expose
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
