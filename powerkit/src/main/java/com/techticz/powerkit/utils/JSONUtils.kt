package com.techticz.powerkit.utils

import android.content.Context
import android.R.attr.name
import android.os.Environment
import android.util.Log
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 5/9/18.
 */
class JSONUtils{

    companion object {
        var mDir:String = Environment.DIRECTORY_DOCUMENTS;
        var mPath : File = Environment.getExternalStoragePublicDirectory(mDir);
        public fun readJsonFromFile(context: Context,fileName:String):String{
            var textfileName = fileName.replace("json","txt")
            var f:File =  File(mPath, textfileName);
            val iStream = f.inputStream()
            //val iStream = context.getAssets().open(fileName)
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
