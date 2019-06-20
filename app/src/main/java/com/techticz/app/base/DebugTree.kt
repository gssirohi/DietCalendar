package com.techticz.app.base

import android.util.Log
import timber.log.Timber

class DebugTree: Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if(REPOSITORY == false){
            if(tag?.contains("Repository")!!){
                return
            }
        }
        if(IMAGE == false){
            if(tag?.contains("Image")!!){
                return
            }
        }
        super.log(priority,tag,message,t)
       /* if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }*/


    }

    companion object {
        val REPOSITORY = false
        val NETWORK = false
        val DOCUMENT = false
        val FOOD = false
        val IMAGE = false
    }
}