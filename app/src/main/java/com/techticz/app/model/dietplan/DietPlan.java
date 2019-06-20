
package com.techticz.app.model.dietplan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Parcel
@Entity
public class DietPlan {

    public DietPlan() {
        this.basicInfo = new BasicInfo();
        this.calendar = new Calendar();
        this.caloryDistribution = new CaloryDistribution();
        this.adminInfo = new AdminInfo();
    }

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @NonNull
    String id;
    @SerializedName("basicInfo")
    @Expose
    @Embedded
    BasicInfo basicInfo;
    @SerializedName("calendar")
    @Expose
    @Embedded
    Calendar calendar;
    @SerializedName("caloryDistribution")
    @Expose
    @Embedded
    CaloryDistribution caloryDistribution;

    @SerializedName("adminInfo")
    @Expose
    @Embedded
    AdminInfo adminInfo;

    @Ignore
    boolean recommonded = false;

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

    public CaloryDistribution getCaloryDistribution() {
        if(caloryDistribution == null){
            caloryDistribution = new CaloryDistribution(10,20,30,10,20,10);
        }
        return caloryDistribution;
    }

    public void setCaloryDistribution(CaloryDistribution caloryDistribution) {
        this.caloryDistribution = caloryDistribution;
    }

    public boolean isRecommonded() {
        return recommonded;
    }

    public void setRecommonded(boolean recommonded) {
        this.recommonded = recommonded;
    }
}
