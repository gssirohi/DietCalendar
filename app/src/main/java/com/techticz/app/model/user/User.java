
package com.techticz.app.model.user;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("basicInfo")
    @Expose
    private BasicInfo basicInfo;
    @SerializedName("healthProfile")
    @Expose
    private HealthProfile healthProfile;
    @SerializedName("goal")
    @Expose
    private Goal goal;
    @SerializedName("mealPref")
    @Expose
    private MealPref mealPref;
    @SerializedName("activePlan")
    @Expose
    private String activePlan;
    @SerializedName("myPlans")
    @Expose
    private List<String> myPlans = new ArrayList<>();
    @SerializedName("myRecipes")
    @Expose
    private List<String> myRecipes = new ArrayList<>();
    @SerializedName("myFoods")
    @Expose
    private List<String> myFoods = new ArrayList<>();
    @SerializedName("access")
    @Expose
    private Access access;

    public User(String id) {
        this.id = id;
        basicInfo = new BasicInfo();
        healthProfile = new HealthProfile();
        mealPref = new MealPref();
        access = new Access();

    }

    public User() {
    }

    public BasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(BasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public HealthProfile getHealthProfile() {
        return healthProfile;
    }

    public void setHealthProfile(HealthProfile healthProfile) {
        this.healthProfile = healthProfile;
    }

    public MealPref getMealPref() {
        return mealPref;
    }

    public void setMealPref(MealPref mealPref) {
        this.mealPref = mealPref;
    }

    public String getActivePlan() {
        return activePlan;
    }

    public void setActivePlan(String activePlan) {
        this.activePlan = activePlan;
    }

    public List<String> getMyPlans() {
        return myPlans;
    }

    public void setMyPlans(List<String> myPlans) {
        this.myPlans = myPlans;
    }

    public List<String> getMyRecipes() {
        return myRecipes;
    }

    public void setMyRecipes(List<String> myRecipes) {
        this.myRecipes = myRecipes;
    }

    public List<String> getMyFoods() {
        return myFoods;
    }

    public void setMyFoods(List<String> myFoods) {
        this.myFoods = myFoods;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
