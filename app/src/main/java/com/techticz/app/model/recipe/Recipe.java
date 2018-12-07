
package com.techticz.app.model.recipe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Recipe {
    public Recipe() {
        basicInfo = new BasicInfo();
        standardServing = new StandardServing();
        basicProperty = new BasicProperty();
        additionalInfo = new AdditionalInfo();
        adminInfo = new AdminInfo();
        formula = new Formula();
    }

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("basicInfo")
    @Expose
    BasicInfo basicInfo;
    @SerializedName("standardServing")
    @Expose
    StandardServing standardServing;
    @SerializedName("basicProperty")
    @Expose
    BasicProperty basicProperty;
    @SerializedName("additionalInfo")
    @Expose
    AdditionalInfo additionalInfo;
    @SerializedName("formula")
    @Expose
    Formula formula;
    @SerializedName("adminInfo")
    @Expose
    AdminInfo adminInfo;

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
