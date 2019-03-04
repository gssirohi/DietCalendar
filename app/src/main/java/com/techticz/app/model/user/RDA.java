package com.techticz.app.model.user;

public class RDA {

    private String activityLevel;
    private String gender;
    private Integer age;
    private Float dailyCalories;

    private CaloryDistributionSourceWise caloryDistribution = null;

    private RDA() {//blocked
    }


    public RDA(Integer age,String gender,String activityLevel,Float dailyCalories){
        this.age = age;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.dailyCalories = dailyCalories;
        caloryDistribution = new CaloryDistributionSourceWise(age,gender);

        //carbs 4 cal per gram
        carbs = (caloryDistribution.carbs * dailyCalories/100)/4;

        //protine 4 cal per gram
        protine = (caloryDistribution.protine * dailyCalories/100)/4;

        //fat 7.5 cal per gram
        fat = (caloryDistribution.fat * dailyCalories/100)/7.5f;

        //fiber 1.5 % of calories
        totalFibers = (1.5f * dailyCalories)/100;

        //fruits 10 % of calories
        fruits = (10 * dailyCalories)/100;

        //veggies 10 % of calories
        veggies = (10 * dailyCalories)/100;

        //water
        if(activityLevel.equals("low") || activityLevel.equals("moderate")){
            waterGlasses = 8;
        } else {
            waterGlasses = 9;
        }
    }


    private Float protine;
    private Float carbs;
    private Float fat;
    private Float totalFibers;
    private Float fruits;
    private Float veggies;
    private Integer waterGlasses;

    public Float getDailyCalories() {
        return dailyCalories;
    }

    public Float getProtine() {
        return protine;
    }

    public Float getCarbs() {
        return carbs;
    }

    public Float getFat() {
        return fat;
    }

    public Float getTotalFibers() {
        return totalFibers;
    }

    public Float getFruits() {
        return fruits;
    }

    public Float getVeggies() {
        return veggies;
    }

    public Integer getWaterGlasses() {
        return waterGlasses;
    }

    class CaloryDistributionSourceWise{
        private Integer protine;
        private Integer carbs;
        private Integer fat;

        public CaloryDistributionSourceWise(Integer age, String gender) {
            protine = 20;
            carbs = 50;
            fat = 30;
        }
    }
}
