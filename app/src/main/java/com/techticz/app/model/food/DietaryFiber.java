
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DietaryFiber {

    @SerializedName("total")
    @Expose
    private Float total = 0f;
    @SerializedName("soluble")
    @Expose
    private Float soluble = 0f;
    @SerializedName("inSoluble")
    @Expose
    private Float inSoluble = 0f;

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getSoluble() {
        return soluble;
    }

    public void setSoluble(Float soluble) {
        this.soluble = soluble;
    }

    public Float getInSoluble() {
        return inSoluble;
    }

    public void setInSoluble(Float inSoluble) {
        this.inSoluble = inSoluble;
    }

    public DietaryFiber applyFactor(Float finalQtyFactor) {
        DietaryFiber fiber = new DietaryFiber();
        fiber.setSoluble(soluble == null?0f :finalQtyFactor * soluble);
        fiber.setInSoluble(inSoluble == null?0f :finalQtyFactor * inSoluble);
        fiber.setTotal(total == null?0f :finalQtyFactor * total);
        return fiber;
    }

    public void add(DietaryFiber fiber){
        if(fiber != null){
            this.soluble = this.soluble+fiber.soluble;
            this.inSoluble = this.inSoluble+fiber.inSoluble;
            this.total = this.total+fiber.total;
        }

    }
}
