
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Nutrients {

    @SerializedName("principlesAndDietaryFibers")
    @Expose
    private PrinciplesAndDietaryFibers principlesAndDietaryFibers = new PrinciplesAndDietaryFibers();
    @SerializedName("waterSolubleVitamins")
    @Expose
    private WaterSolubleVitamins waterSolubleVitamins = new WaterSolubleVitamins();
    @SerializedName("mineralsAndTraceElements")
    @Expose
    private MineralsAndTraceElements mineralsAndTraceElements = new MineralsAndTraceElements();
    @SerializedName("starchAndSugars")
    @Expose
    private StarchAndSugars starchAndSugars = new StarchAndSugars();
    @SerializedName("fatyAcid")
    @Expose
    private FatyAcid fatyAcid = new FatyAcid();
    @SerializedName("organicAcids")
    @Expose
    private OrganicAcids organicAcids = new OrganicAcids();
    @SerializedName("totalCarotenids")
    @Expose
    private Float totalCarotenids;

    public PrinciplesAndDietaryFibers getPrinciplesAndDietaryFibers() {
        return principlesAndDietaryFibers;
    }

    public void setPrinciplesAndDietaryFibers(PrinciplesAndDietaryFibers principlesAndDietaryFibers) {
        this.principlesAndDietaryFibers = principlesAndDietaryFibers;
    }

    public WaterSolubleVitamins getWaterSolubleVitamins() {
        return waterSolubleVitamins;
    }

    public void setWaterSolubleVitamins(WaterSolubleVitamins waterSolubleVitamins) {
        this.waterSolubleVitamins = waterSolubleVitamins;
    }

    public MineralsAndTraceElements getMineralsAndTraceElements() {
        return mineralsAndTraceElements;
    }

    public void setMineralsAndTraceElements(MineralsAndTraceElements mineralsAndTraceElements) {
        this.mineralsAndTraceElements = mineralsAndTraceElements;
    }

    public StarchAndSugars getStarchAndSugars() {
        return starchAndSugars;
    }

    public void setStarchAndSugars(StarchAndSugars starchAndSugars) {
        this.starchAndSugars = starchAndSugars;
    }

    public FatyAcid getFatyAcid() {
        return fatyAcid;
    }

    public void setFatyAcid(FatyAcid fatyAcid) {
        this.fatyAcid = fatyAcid;
    }

    public OrganicAcids getOrganicAcids() {
        return organicAcids;
    }

    public void setOrganicAcids(OrganicAcids organicAcids) {
        this.organicAcids = organicAcids;
    }

    public Float getTotalCarotenids() {
        return totalCarotenids;
    }

    public void setTotalCarotenids(Float totalCarotenids) {
        this.totalCarotenids = totalCarotenids;
    }

    @NotNull
    public Nutrients applyFactor(int finalQtyFactor) {
        PrinciplesAndDietaryFibers principlesAndDietaryFibers = new PrinciplesAndDietaryFibers();
        if(this.principlesAndDietaryFibers.getEnergy() == null){
            principlesAndDietaryFibers.setEnergy(0f);
        } else {
            principlesAndDietaryFibers.setEnergy(finalQtyFactor * this.principlesAndDietaryFibers.getEnergy());
        }
        Nutrients nutrients = new Nutrients();
        nutrients.setPrinciplesAndDietaryFibers(principlesAndDietaryFibers);
        return nutrients;
    }

    public void addUpNutrients(@Nullable Nutrients nutri) {
        if(nutri != null) {
            if(nutri.principlesAndDietaryFibers != null) {
                this.principlesAndDietaryFibers.setEnergy(addFloats(this.principlesAndDietaryFibers.getEnergy(), nutri.principlesAndDietaryFibers.getEnergy()));
            }
        }
    }

    private Float addFloats(Float output, Float input) {
        if(output != null && input != null){
            output = output+input;
        } else if(input != null){
            output = 0f+input;
        }
        return output;
    }
}
