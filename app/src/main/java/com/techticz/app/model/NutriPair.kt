package com.techticz.app.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 3/9/18.
 */
class NutriPair {
    @SerializedName("name")
    @Expose
    var name: String = ""
    @SerializedName("value")
    @Expose
    var value: Float = 0f
    @SerializedName("rda")
    @Expose
    var rda: Float = 0f

    constructor(name: String, value: Float?, rda: Float) {
        this.name = name
        if(value != null)
        this.value = value!!
        this.rda = rda
    }
}