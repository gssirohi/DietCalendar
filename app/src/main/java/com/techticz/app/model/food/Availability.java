
package com.techticz.app.model.food;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Availability {

    @SerializedName("seasonal")
    @Expose
    private Boolean seasonal;
    @SerializedName("seasons")
    @Expose
    private List<String> seasons = null;
    @SerializedName("availabilityRating")
    @Expose
    private Integer availabilityRating;

    public Boolean getSeasonal() {
        return seasonal;
    }

    public void setSeasonal(Boolean seasonal) {
        this.seasonal = seasonal;
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<String> seasons) {
        this.seasons = seasons;
    }

    public Integer getAvailabilityRating() {
        return availabilityRating;
    }

    public void setAvailabilityRating(Integer availabilityRating) {
        this.availabilityRating = availabilityRating;
    }

}
