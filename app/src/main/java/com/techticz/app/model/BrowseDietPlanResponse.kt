package com.techticz.app.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.networking.model.CommonResponse

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class BrowseDietPlanResponse : CommonResponse() {
    @SerializedName("plans")
    @Expose
    var plans: List<DietPlan> = ArrayList()
        get() = field

}