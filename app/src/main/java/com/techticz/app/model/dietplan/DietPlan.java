
package com.techticz.app.model.dietplan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class DietPlan {

    public DietPlan() {
    }

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("basicInfo")
    @Expose
    BasicInfo basicInfo;
    @SerializedName("calendar")
    @Expose
    Calendar calendar;
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

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public AdminInfo getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(AdminInfo adminInfo) {
        this.adminInfo = adminInfo;
    }

}
