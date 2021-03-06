
package com.techticz.app.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Goal {

    @SerializedName("targetWeight")
    @Expose
    private Float targetWeight = 60f;

    @SerializedName("goalType")
    @Expose
    private String goalType;

    @SerializedName("durationInWeek")
    @Expose
    private Integer durationInWeek = 30;

    public Float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public Integer getDurationInWeek() {
        return durationInWeek;
    }

    public void setDurationInWeek(Integer durationInWeek) {
        this.durationInWeek = durationInWeek;
    }

}
