
package com.techticz.app.model.recipe;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasicProperty {

    @SerializedName("availability")
    @Expose
    private Integer availability;
    @SerializedName("prefMeals")
    @Expose
    private List<String> prefMeals = null;

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
