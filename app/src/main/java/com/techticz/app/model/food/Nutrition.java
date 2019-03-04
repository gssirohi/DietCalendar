
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

public class Nutrition {
    public Nutrition() {
        nutrients = new Nutrients();
    }

    @SerializedName("portion")
    @Expose
    @ColumnInfo(name = "nutri_portion")
    private Integer portion;
    @SerializedName("nutrients")
    @Expose
    @Embedded
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
