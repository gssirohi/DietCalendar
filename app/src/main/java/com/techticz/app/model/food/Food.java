
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Food {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    @NonNull
    private String id;
    @SerializedName("basicInfo")
    @Expose
    @Embedded
    private BasicInfo basicInfo;
    @SerializedName("standardServing")
    @Expose
    @Embedded
    private StandardServing standardServing;
    @SerializedName("basicProperty")
    @Expose
    @Embedded
    private BasicProperty basicProperty;
    @SerializedName("additionalInfo")
    @Expose
    @Embedded
    private AdditionalInfo additionalInfo;
    @SerializedName("cost")
    @Expose
    @Embedded
    private Cost cost;
    @SerializedName("nutrition")
    @Expose
    @Embedded
    private Nutrition nutrition;
    @SerializedName("adminInfo")
    @Expose
    @Embedded
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

    public Float getCaloriesPerStdPortion() {
        try {
            Integer portionFact = this.nutrition.getPortion();
            Nutrients portionFactNutrients = this.getNutrition().getNutrients();
            Integer stdPortion = this.getStandardServing().getPortion();
            if (stdPortion == null) stdPortion = nutrition.getPortion();

            Float factor = (Float) (stdPortion.floatValue() / portionFact.floatValue());
            Nutrients factoredNutrients = portionFactNutrients.applyFactor(factor);
            return factoredNutrients.getPrinciplesAndDietaryFibers().getEnergy() * 0.239f;
        } catch (Exception e){
            return null;
        }
    }
}
