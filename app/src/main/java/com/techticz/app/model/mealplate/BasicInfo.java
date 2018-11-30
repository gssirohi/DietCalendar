
package com.techticz.app.model.mealplate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class BasicInfo {
    public BasicInfo() {
        name = new Name();
    }

    @SerializedName("name")
    @Expose
    Name name;
    @SerializedName("desc")
    @Expose
    String desc;
    @SerializedName("image")
    @Expose
    String image;
    @SerializedName("category")
    @Expose
    String category;
    @SerializedName("subcategory")
    @Expose
    String subcategory;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("calories")
    @Expose
    Float calories;

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

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

}
