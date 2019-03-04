
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import androidx.room.Embedded;

public class Nutrients {

    @SerializedName("principlesAndDietaryFibers")
    @Expose
    @Embedded
    private PrinciplesAndDietaryFibers principlesAndDietaryFibers = new PrinciplesAndDietaryFibers();
    @SerializedName("waterSolubleVitamins")
    @Expose
    @Embedded
    private WaterSolubleVitamins waterSolubleVitamins = new WaterSolubleVitamins();
    @SerializedName("mineralsAndTraceElements")
    @Expose
    @Embedded
    private MineralsAndTraceElements mineralsAndTraceElements = new MineralsAndTraceElements();
    @SerializedName("starchAndSugars")
    @Expose
    @Embedded
    private StarchAndSugars starchAndSugars = new StarchAndSugars();
    @SerializedName("fatyAcid")
    @Expose
    @Embedded
    private FatyAcid fatyAcid = new FatyAcid();
    @SerializedName("organicAcids")
    @Expose
    @Embedded
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
    public Nutrients applyFactor(Float finalQtyFactor) {
        Nutrients nutrients = new Nutrients();
        nutrients.principlesAndDietaryFibers = this.principlesAndDietaryFibers == null? new PrinciplesAndDietaryFibers(): this.principlesAndDietaryFibers.applyFactor(finalQtyFactor);
        nutrients.mineralsAndTraceElements = this.mineralsAndTraceElements == null? new MineralsAndTraceElements(): this.mineralsAndTraceElements.applyFactor(finalQtyFactor);
        nutrients.waterSolubleVitamins = this.waterSolubleVitamins == null? new WaterSolubleVitamins(): this.waterSolubleVitamins.applyFactor(finalQtyFactor);
        nutrients.fatyAcid = this.fatyAcid == null? new FatyAcid(): this.fatyAcid.applyFactor(finalQtyFactor);
        nutrients.organicAcids = this.organicAcids == null? new OrganicAcids(): this.organicAcids.applyFactor(finalQtyFactor);
        nutrients.starchAndSugars = this.starchAndSugars == null? new StarchAndSugars(): this.starchAndSugars.applyFactor(finalQtyFactor);
        return nutrients;
    }

    public void addUpNutrients(@Nullable Nutrients nutri) {
        if(nutri != null) {
            this.principlesAndDietaryFibers.add(nutri.principlesAndDietaryFibers);
            this.mineralsAndTraceElements.add(nutri.mineralsAndTraceElements);
            this.waterSolubleVitamins.add(nutri.waterSolubleVitamins);
            this.fatyAcid.add(nutri.fatyAcid);
            this.organicAcids.add(nutri.organicAcids);
            this.starchAndSugars.add(nutri.starchAndSugars);
        }
    }


}
