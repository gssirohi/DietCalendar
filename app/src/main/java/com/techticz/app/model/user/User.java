
package com.techticz.app.model.user;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.model.dietplan.Calendar;
import com.techticz.app.util.Utils;

import org.jetbrains.annotations.Nullable;

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

    public Float getBMI() {
        Float weightInKg = getHealthProfile().getWeight();
        Float heightInCm = getHealthProfile().getHeight();

        if(weightInKg != null && heightInCm != null){
            Float bmi = (weightInKg / (heightInCm * heightInCm))*10000;
            return Utils.roundUpFloatToOneDecimal(bmi);
        }
        return 0f;
    }

    public Float getIdealWeight() {
        Float heightInCm = getHealthProfile().getHeight();

        if(heightInCm != null){
            Float idealBMI = 21.5f;
            Float idealWeightInKg = (idealBMI * (heightInCm * heightInCm))/10000;
            return Utils.roundUpFloatToOneDecimal(idealWeightInKg);
        }
        return 0f;
    }

    public Integer getAge(){
        String dob = getBasicInfo().getDob();
        if(dob != null){
            String[] args = dob.split("-");
            Integer birthYear = Integer.parseInt(args[2]);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            Integer currentYear = cal.get(java.util.Calendar.YEAR);
            Integer age = currentYear-birthYear;
            return age;
        }

        return 24;
    }
    public Float getBMR() {
        String gender = getBasicInfo().getGender();
        Float weightInKg = getHealthProfile().getWeight();
        Float heightInCm = getHealthProfile().getHeight();

        Integer age = getAge();
        Double bmr = null;
        if(gender != null && gender.equalsIgnoreCase("male")){
            bmr =  66+(6.3 * 2.2* weightInKg) + (12.9 * 0.39 * heightInCm) - (6.8 * age);
        } else {
            bmr =  655+(4.3 * 2.2* weightInKg) + (4.7 * 0.39 * heightInCm) - (4.7 * age);
        }

        return bmr.floatValue();
    }

    public Float getDailyCaloriesToMaintainWeight(){

        Float bmr = getBMR();

        String activityLevel = getHealthProfile().getActivityLevel();
        if(activityLevel == null){
            activityLevel = "low";
        }

        Float activityFactor = null;
        if(activityLevel.equalsIgnoreCase("low")){
            activityFactor = 1.2f;
        } else if(activityLevel.equalsIgnoreCase("moderate")){
            activityFactor = 1.55f;
        } else if(activityLevel.equalsIgnoreCase("high")){
            activityFactor = 1.725f;
        } else if(activityLevel.equalsIgnoreCase("extreme")){
            activityFactor = 1.9f;
        }

        if(bmr == null || activityFactor == null) return 0f;

        Float maintainCalory = bmr * activityFactor;
        return maintainCalory;
    }

    public Float getDailyRequiredCaloriesAsPerGoal() {
        Float targetWeight = getGoal().getTargetWeight();
        Integer duration = getGoal().getDurationInWeek();

        Float weightInKg = getHealthProfile().getWeight();

        Float maintainCalory = getDailyCaloriesToMaintainWeight();

        Float weightDiff = 0f;
        if(targetWeight < weightInKg){
            weightDiff = weightInKg - targetWeight;
        } else {
            weightDiff = targetWeight - weightInKg;
        }


        Float perWeekWeightDiff = weightDiff/duration;
                // for loosing/gaining 1 kg weight in 1 week calory deficit 3500 * 2.2 Calories with no activity
        Double deficitCaloryPerKgPerWeek = (3500 * 2.2)/7;

        Double extraRequiredCalories = perWeekWeightDiff * deficitCaloryPerKgPerWeek;

        Float totalRequiredCalory= null;

        if(targetWeight < weightInKg){
            totalRequiredCalory = maintainCalory - extraRequiredCalories.floatValue();
        } else {
            totalRequiredCalory = maintainCalory + extraRequiredCalories.floatValue();
        }

        return totalRequiredCalory;

    }

    public RDA getRDA(){
        RDA rda = new RDA(getAge(),getBasicInfo().getGender(),getHealthProfile().getActivityLevel(),getDailyRequiredCaloriesAsPerGoal());
        return rda;
    }
}
