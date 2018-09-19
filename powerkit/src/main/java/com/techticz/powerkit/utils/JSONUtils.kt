package com.techticz.powerkit.utils

import android.content.Context
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import android.R.attr.name
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 5/9/18.
 */
class JSONUtils{

    companion object {

        public fun readJsonFromFile(context: Context,fileName:String):String{
            val iStream = context.getAssets().open(fileName)
            val textBuilder = StringBuilder()
            BufferedReader(InputStreamReader(iStream, Charset.forName(StandardCharsets.UTF_8.name()))).use({ reader ->
                var c = 0
                while (c != -1) {
                    c = reader.read()
                    textBuilder.append(c.toChar())
                }
            })
            var text = textBuilder.toString().replace("\uFFFF","")
            Log.d("JSONUtils","Treated Json:"+text)
            return text
        }
    }

}
