
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.Embedded;
import androidx.room.Entity;


public class BasicInfo {

    @SerializedName("name")
    @Expose
    @Embedded
    private Name name;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("subCategory")
    @Expose
    private String subCategory;
    @SerializedName("caloriesPerStdPortion")
    @Expose
    private Float caloriesPerStdPortion;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Float getCaloriesPerStdPortion() {
        return caloriesPerStdPortion;
    }

    public void setCaloriesPerStdPortion(Float caloriesPerStdPortion) {
        this.caloriesPerStdPortion = caloriesPerStdPortion;
    }

}
