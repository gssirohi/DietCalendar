
package com.techticz.app.model.dietplan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class BasicInfo {

    public BasicInfo() {
    }

    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("desc")
    @Expose
    String desc;
    @SerializedName("image")
    @Expose
    String image;
    @SerializedName("dailyCalories")
    @Expose
    Integer dailyCalories;
    @SerializedName("type")
    @Expose
    String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getDailyCalories() {
        return dailyCalories;
    }

    public void setDailyCalories(Integer dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
