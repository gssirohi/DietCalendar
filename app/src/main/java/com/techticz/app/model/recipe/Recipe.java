
package com.techticz.app.model.recipe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recipe {

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
    @SerializedName("formula")
    @Expose
    private Formula formula;
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

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public AdminInfo getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(AdminInfo adminInfo) {
        this.adminInfo = adminInfo;
    }

}
