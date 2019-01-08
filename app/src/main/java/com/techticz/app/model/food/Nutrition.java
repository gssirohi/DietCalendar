
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nutrition {
    public Nutrition() {
        nutrients = new Nutrients();
    }

    @SerializedName("portion")
    @Expose
    private Integer portion;
    @SerializedName("nutrients")
    @Expose
    private Nutrients nutrients;

    public Integer getPortion() {
        return portion;
    }

    public void setPortion(Integer portion) {
        this.portion = portion;
    }

    public Nutrients getNutrients() {
        return nutrients;
    }

    public void setNutrients(Nutrients nutrients) {
        this.nutrients = nutrients;
    }

}
