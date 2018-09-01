package com.techticz.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 13/12/17.
 */

public class AppUpdateFeatures {
    @SerializedName("feature")
    String feature;
    @SerializedName("description")
    String description;

    public AppUpdateFeatures() {
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

