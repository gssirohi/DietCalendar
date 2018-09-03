package com.techticz.app.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.techticz.networking.model.CommonResponse

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 3/9/18.
 */
class User {
    @SerializedName("firstName")
    @Expose
    var userFirstName: String = ""
    @SerializedName("lastName")
    @Expose
    var userLastName: String = ""
    @SerializedName("email")
    @Expose
    var userEmail: String = ""

    constructor(s: String, s1: String, s2: String){
        userFirstName = s
        userLastName = s1
        userEmail = s2
    }

    var userMiddleName: String = ""
}