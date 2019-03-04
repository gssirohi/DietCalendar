package com.techticz.app.repo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import com.google.gson.Gson
import com.techticz.app.model.launch.DocumentVersionInfo
import com.techticz.app.model.launch.Launching
import com.techticz.app.model.user.User
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefRepo @Inject constructor(val context: Context) {
    val PREFS_FILENAME = "om.techticz.app.prefs"
    val USER_PREF = "user_pref"
    val LAUNCHING_PREF = "launching_pref"
    val DOC_VERSION_PREF = "doc_version_pref"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);
    val gson:Gson = Gson()

    var user: User?
        get() = gson.fromJson(prefs.getString(USER_PREF, ""),User::class.java)
        set(value) = prefs.edit().putString(USER_PREF, gson.toJson(value)).apply()

    var launching: Launching?
        get() = gson.fromJson(prefs.getString(LAUNCHING_PREF, ""),Launching::class.java)
        set(value) = prefs.edit().putString(LAUNCHING_PREF, gson.toJson(value)).apply()

    var documentVersion:DocumentVersionInfo?
        get() = gson.fromJson(prefs.getString(DOC_VERSION_PREF, "{\"food\":0,\"recipe\":0,\"plate\":0,\"plan\":0}"),DocumentVersionInfo::class.java)
        set(value) = prefs.edit().putString(DOC_VERSION_PREF, gson.toJson(value)).apply()
    init {
        Timber.d("Injecting:"+this)
    }
}