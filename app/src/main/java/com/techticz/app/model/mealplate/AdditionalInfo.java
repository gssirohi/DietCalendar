
package com.techticz.app.model.mealplate;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.db.converters.FoodTypeConverters;

import org.parceler.Parcel;

import androidx.room.TypeConverters;

@Parcel
public class AdditionalInfo {
    public AdditionalInfo() {
    }

    @SerializedName("alerts")
    @Expose
    @TypeConverters(FoodTypeConverters.class)
    List<String> alerts = null;
    @SerializedName("region")
    @Expose
    String region;
    @SerializedName("subregion")
    @Expose
    String subregion;

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

    public String getSubregion() {
        return subregion;
    }

    public void setSubregion(String subregion) {
        this.subregion = subregion;
    }

}
