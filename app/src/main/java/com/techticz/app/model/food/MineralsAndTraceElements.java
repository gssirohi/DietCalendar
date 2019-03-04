
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techticz.app.util.Utils;

public class MineralsAndTraceElements {

    @SerializedName("aluminium")
    @Expose
    private Float aluminium;
    @SerializedName("arsenic")
    @Expose
    private Float arsenic;
    @SerializedName("cadium")
    @Expose
    private Float cadium;
    @SerializedName("calcium")
    @Expose
    private Float calcium;
    @SerializedName("chromium")
    @Expose
    private Float chromium;
    @SerializedName("cobalt")
    @Expose
    private Float cobalt;
    @SerializedName("copper")
    @Expose
    private Float copper;
    @SerializedName("iron")
    @Expose
    private Float iron;
    @SerializedName("led")
    @Expose
    private Float led;
    @SerializedName("lithium")
    @Expose
    private Float lithium;
    @SerializedName("magnesium")
    @Expose
    private Float magnesium;
    @SerializedName("manganees")
    @Expose
    private Float manganees;
    @SerializedName("mercury")
    @Expose
    private Float mercury;
    @SerializedName("molebdeum")
    @Expose
    private Float molebdeum;
    @SerializedName("nickle")
    @Expose
    private Float nickle;
    @SerializedName("phosphorus")
    @Expose
    private Float phosphorus;
    @SerializedName("potassium")
    @Expose
    private Float potassium;
    @SerializedName("selenium")
    @Expose
    private Float selenium;
    @SerializedName("sodium")
    @Expose
    private Float sodium;
    @SerializedName("zinc")
    @Expose
    private Float zinc;

    public Float getAluminium() {
        return aluminium;
    }

    public void setAluminium(Float aluminium) {
        this.aluminium = aluminium;
    }

    public Float getArsenic() {
        return arsenic;
    }

    public void setArsenic(Float arsenic) {
        this.arsenic = arsenic;
    }

    public Float getCadium() {
        return cadium;
    }

    public void setCadium(Float cadium) {
        this.cadium = cadium;
    }

    public Float getCalcium() {
        return calcium;
    }

    public void setCalcium(Float calcium) {
        this.calcium = calcium;
    }

    public Float getChromium() {
        return chromium;
    }

    public void setChromium(Float chromium) {
        this.chromium = chromium;
    }

    public Float getCobalt() {
        return cobalt;
    }

    public void setCobalt(Float cobalt) {
        this.cobalt = cobalt;
    }

    public Float getCopper() {
        return copper;
    }

    public void setCopper(Float copper) {
        this.copper = copper;
    }

    public Float getIron() {
        return iron;
    }

    public void setIron(Float iron) {
        this.iron = iron;
    }

    public Float getLed() {
        return led;
    }

    public void setLed(Float led) {
        this.led = led;
    }

    public Float getLithium() {
        return lithium;
    }

    public void setLithium(Float lithium) {
        this.lithium = lithium;
    }

    public Float getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(Float magnesium) {
        this.magnesium = magnesium;
    }

    public Float getManganees() {
        return manganees;
    }

    public void setManganees(Float manganees) {
        this.manganees = manganees;
    }

    public Float getMercury() {
        return mercury;
    }

    public void setMercury(Float mercury) {
        this.mercury = mercury;
    }

    public Float getMolebdeum() {
        return molebdeum;
    }

    public void setMolebdeum(Float molebdeum) {
        this.molebdeum = molebdeum;
    }

    public Float getNickle() {
        return nickle;
    }

    public void setNickle(Float nickle) {
        this.nickle = nickle;
    }

    public Float getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(Float phosphorus) {
        this.phosphorus = phosphorus;
    }

    public Float getPotassium() {
        return potassium;
    }

    public void setPotassium(Float potassium) {
        this.potassium = potassium;
    }

    public Float getSelenium() {
        return selenium;
    }

    public void setSelenium(Float selenium) {
        this.selenium = selenium;
    }

    public Float getSodium() {
        return sodium;
    }

    public void setSodium(Float sodium) {
        this.sodium = sodium;
    }

    public Float getZinc() {
        return zinc;
    }

    public void setZinc(Float zinc) {
        this.zinc = zinc;
    }

    public MineralsAndTraceElements applyFactor(Float finalQtyFactor) {
        MineralsAndTraceElements mineralsAndTraceElements = new MineralsAndTraceElements();

        mineralsAndTraceElements.aluminium = this.aluminium == null?0f:this.aluminium * finalQtyFactor;
        mineralsAndTraceElements.arsenic = this.arsenic == null?0f:this.arsenic * finalQtyFactor;
        mineralsAndTraceElements.cadium = this.cadium == null?0f:this.cadium * finalQtyFactor;
        mineralsAndTraceElements.calcium = this.calcium == null?0f:this.calcium * finalQtyFactor;
        mineralsAndTraceElements.chromium = this.chromium == null?0f:this.chromium * finalQtyFactor;
        mineralsAndTraceElements.cobalt = this.cobalt == null?0f:this.cobalt * finalQtyFactor;
        mineralsAndTraceElements.copper = this.copper == null?0f:this.copper * finalQtyFactor;
        mineralsAndTraceElements.iron = this.iron == null?0f:this.iron * finalQtyFactor;
        mineralsAndTraceElements.led = this.led == null?0f:this.led * finalQtyFactor;
        mineralsAndTraceElements.lithium = this.lithium == null?0f:this.lithium * finalQtyFactor;
        mineralsAndTraceElements.magnesium = this.magnesium == null?0f:this.magnesium * finalQtyFactor;
        mineralsAndTraceElements.manganees = this.manganees == null?0f:this.manganees * finalQtyFactor;
        mineralsAndTraceElements.mercury = this.mercury == null?0f:this.mercury * finalQtyFactor;
        mineralsAndTraceElements.molebdeum = this.molebdeum == null?0f:this.molebdeum * finalQtyFactor;
        mineralsAndTraceElements.nickle = this.nickle == null?0f:this.nickle * finalQtyFactor;
        mineralsAndTraceElements.phosphorus = this.phosphorus == null?0f:this.phosphorus * finalQtyFactor;
        mineralsAndTraceElements.potassium = this.potassium == null?0f:this.potassium * finalQtyFactor;
        mineralsAndTraceElements.selenium = this.selenium == null?0f:this.selenium * finalQtyFactor;
        mineralsAndTraceElements.sodium = this.sodium == null?0f:this.sodium * finalQtyFactor;
        mineralsAndTraceElements.zinc = this.zinc == null?0f:this.zinc * finalQtyFactor;

        return mineralsAndTraceElements;

    }

    public void add(MineralsAndTraceElements mineralsAndTraceElements) {
        this.aluminium = Utils.addFloats(this.aluminium,mineralsAndTraceElements.aluminium);
        this.arsenic = Utils.addFloats(this.arsenic,mineralsAndTraceElements.arsenic);
        this.cadium = Utils.addFloats(this.cadium,mineralsAndTraceElements.cadium);
        this.calcium = Utils.addFloats(this.calcium,mineralsAndTraceElements.calcium);
        this.chromium = Utils.addFloats(this.chromium,mineralsAndTraceElements.chromium);
        this.cobalt = Utils.addFloats(this.cobalt,mineralsAndTraceElements.cobalt);
        this.copper = Utils.addFloats(this.copper,mineralsAndTraceElements.copper);
        this.iron = Utils.addFloats(this.iron,mineralsAndTraceElements.iron);
        this.led = Utils.addFloats(this.led,mineralsAndTraceElements.led);
        this.lithium = Utils.addFloats(this.lithium,mineralsAndTraceElements.lithium);
        this.magnesium = Utils.addFloats(this.magnesium,mineralsAndTraceElements.magnesium);
        this.manganees = Utils.addFloats(this.manganees,mineralsAndTraceElements.manganees);
        this.mercury = Utils.addFloats(this.mercury,mineralsAndTraceElements.mercury);
        this.molebdeum = Utils.addFloats(this.molebdeum,mineralsAndTraceElements.molebdeum);
        this.nickle = Utils.addFloats(this.nickle,mineralsAndTraceElements.nickle);
        this.phosphorus = Utils.addFloats(this.phosphorus,mineralsAndTraceElements.phosphorus);
        this.potassium = Utils.addFloats(this.potassium,mineralsAndTraceElements.potassium);
        this.selenium = Utils.addFloats(this.selenium,mineralsAndTraceElements.selenium);
        this.sodium = Utils.addFloats(this.sodium,mineralsAndTraceElements.sodium);
        this.zinc = Utils.addFloats(this.zinc,mineralsAndTraceElements.zinc);
    }
}
