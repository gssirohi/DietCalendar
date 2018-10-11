package com.techticz.app.model.recipe

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.techticz.networking.model.CommonResponse

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class RecipeResponse : CommonResponse() {
    @SerializedName("recipe")
    @Expose
    var recipe: Recipe? = null

}