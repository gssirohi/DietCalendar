
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Food {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("basicInfo")
    @Expose
    private BasicInfo basicInfo;
    @SerializedName("standardServing")
    @Expose
    private StandardServing standardServing;
    @SerializedName("basicProperty")
    @Expose
    private BasicProperty basicProperty;
    @SerializedName("additionalInfo")
    @Expose
    private AdditionalInfo additionalInfo;
    @SerializedName("cost")
    @Expose
    private Cost cost;
    @SerializedName("nutrition")
    @Expose
    private Nutrition nutrition;
    @SerializedName("adminInfo")
    @Expose
    private AdminInfo adminInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(BasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public StandardServing getStandardServing() {
        return standardServing;
    }

    public void setStandardServing(StandardServing standardServing) {
        this.standardServing = standardServing;
    }

    public BasicProperty getBasicProperty() {
        return basicProperty;
    }

    public void setBasicProperty(BasicProperty basicProperty) {
        this.basicProperty = basicProperty;
    }

    public AdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(AdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public Nutrition getNutrition() {
        return nutrition;
    }

    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
    }

    public AdminInfo getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(AdminInfo adminInfo) {
        this.adminInfo = adminInfo;
    }

}
