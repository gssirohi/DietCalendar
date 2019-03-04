package com.techticz.dietcalendar.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.techticz.app.model.launch.Launching
import com.techticz.networking.model.CommonResponse

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class LauncherResponse : CommonResponse() {
    @SerializedName("launching")
    @Expose
    var launching: Launching? = null
}