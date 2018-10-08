
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nutrients {

    @SerializedName("principlesAndDietaryFibers")
    @Expose
    private PrinciplesAndDietaryFibers principlesAndDietaryFibers;
    @SerializedName("waterSolubleVitamins")
    @Expose
    private WaterSolubleVitamins waterSolubleVitamins;
    @SerializedName("mineralsAndTraceElements")
    @Expose
    private MineralsAndTraceElements mineralsAndTraceElements;
    @SerializedName("starchAndSugars")
    @Expose
    private StarchAndSugars starchAndSugars;
    @SerializedName("fatyAcid")
    @Expose
    private FatyAcid fatyAcid;
    @SerializedName("organicAcids")
    @Expose
    private OrganicAcids organicAcids;
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

}
