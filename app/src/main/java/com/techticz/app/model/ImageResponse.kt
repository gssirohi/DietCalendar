package com.techticz.app.model

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 3/9/18.
 */
class ImageResponse {
    @SerializedName("url")
    @Expose
    var url: String? = ""
    @SerializedName("bitmap")
    @Expose
    var bitmap: Bitmap? = null
    @SerializedName("name")
    @Expose
    var name: String? = ""

    constructor(url: String?, name: String?, bitmap: Bitmap?){
        this.url =url
        this.name = name
        this.bitmap = bitmap
    }

}