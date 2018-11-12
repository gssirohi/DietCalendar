
package com.techticz.app.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Access {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("premium")
    @Expose
    private Boolean premium;
    @SerializedName("canEditPlan")
    @Expose
    private Boolean canEditPlan;
    @SerializedName("canEditRecipe")
    @Expose
    private Boolean canEditRecipe;
    @SerializedName("canEditFood")
    @Expose
    private Boolean canEditFood;
    @SerializedName("canCreatePlan")
    @Expose
    private Boolean canCreatePlan;
    @SerializedName("canCreateRecipe")
    @Expose
    private Boolean canCreateRecipe;
    @SerializedName("canCreateFood")
    @Expose
    private Boolean canCreateFood;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public Boolean getCanEditPlan() {
        return canEditPlan;
    }

    public void setCanEditPlan(Boolean canEditPlan) {
        this.canEditPlan = canEditPlan;
    }

    public Boolean getCanEditRecipe() {
        return canEditRecipe;
    }

    public void setCanEditRecipe(Boolean canEditRecipe) {
        this.canEditRecipe = canEditRecipe;
    }

    public Boolean getCanEditFood() {
        return canEditFood;
    }

    public void setCanEditFood(Boolean canEditFood) {
        this.canEditFood = canEditFood;
    }

    public Boolean getCanCreatePlan() {
        return canCreatePlan;
    }

    public void setCanCreatePlan(Boolean canCreatePlan) {
        this.canCreatePlan = canCreatePlan;
    }

    public Boolean getCanCreateRecipe() {
        return canCreateRecipe;
    }

    public void setCanCreateRecipe(Boolean canCreateRecipe) {
        this.canCreateRecipe = canCreateRecipe;
    }

    public Boolean getCanCreateFood() {
        return canCreateFood;
    }

    public void setCanCreateFood(Boolean canCreateFood) {
        this.canCreateFood = canCreateFood;
    }

}
