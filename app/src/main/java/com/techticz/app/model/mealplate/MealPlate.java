
package com.techticz.app.model.mealplate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Parcel
@Entity
public class MealPlate {
    public MealPlate() {
        basicInfo = new BasicInfo();
        basicProperty = new BasicProperty();
        additionalInfo = new AdditionalInfo();
        items = new Items();
        adminInfo = new AdminInfo();
    }

    @PrimaryKey
    @SerializedName("id")
    @Expose
    @NonNull
    String id;
    @SerializedName("basicInfo")
    @Expose
    @Embedded
    BasicInfo basicInfo;
    @SerializedName("basicProperty")
    @Expose
    @Embedded
    BasicProperty basicProperty;
    @SerializedName("additionalInfo")
    @Expose
    @Embedded
    AdditionalInfo additionalInfo;
    @SerializedName("items")
    @Expose
    @Embedded
    Items items;
    @SerializedName("adminInfo")
    @Expose
    @Embedded
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

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public AdminInfo getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(AdminInfo adminInfo) {
        this.adminInfo = adminInfo;
    }

}
