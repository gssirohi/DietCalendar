package com.techticz.app.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.techticz.app.model.food.Food
import com.techticz.app.model.user.User
import com.techticz.networking.model.CommonResponse

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class UserResponse : CommonResponse() {
    @SerializedName("user")
    @Expose
    var user: User? = null

}