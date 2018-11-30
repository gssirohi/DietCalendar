
package com.techticz.app.model.mealplate;

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
    @SerializedName("prefmeals")
    @Expose
    List<String> prefmeals = null;

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public List<String> getPrefmeals() {
        return prefmeals;
    }

    public void setPrefmeals(List<String> prefmeals) {
        this.prefmeals = prefmeals;
    }

}
