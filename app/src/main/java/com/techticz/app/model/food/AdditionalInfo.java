
package com.techticz.app.model.food;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.db.converters.FoodTypeConverters;

import androidx.room.TypeConverters;

public class AdditionalInfo {

    @SerializedName("alerts")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    private List<String> alerts = null;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("subRegion")
    @Expose
    private String subRegion;

    public List<String> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<String> alerts) {
        this.alerts = alerts;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
    }

}
