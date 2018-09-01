package com.techticz.dietcalendar.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.techticz.networking.model.CommonResponse

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class LauncherResponse : CommonResponse() {
    @SerializedName("uid")
    @Expose
    var uid: Int = 0
        get() = field

    @SerializedName("launchMessage")
    @Expose
    var launchMessage: String = ""
}