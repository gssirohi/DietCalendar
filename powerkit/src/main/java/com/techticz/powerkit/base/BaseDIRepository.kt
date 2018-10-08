package com.techticz.powerkit.base;

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.techticz.networking.util.RateLimiter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject;

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 5/10/18.
 */

open class BaseDIRepository  {
    @Inject
    lateinit var appExecutors: com.techticz.networking.model.AppExecutors

    var hostActivityContext: Context? = null

    // Fetch again if the data is older than 48 hours
    private val rateLimiter = RateLimiter<String>(48, TimeUnit.HOURS)

    private var progressDialog: MaterialDialog? = null
    init {
        Timber.d("Injecting:" + this)
    }


    fun hideProgress() {
        appExecutors.mainThread().execute({progressDialog?.dismiss()})
    }


    fun showProgress(){
        showProgress("Hold On","work in progress..")
    }
    fun showProgress(title:String,message:String) {

        progressDialog = MaterialDialog.Builder(hostActivityContext!!)
                    .title(title)
                    .content(message)
                    .progress(true, 0).build()

        progressDialog!!.show()

    }
    fun showSuccess(s:String) {
        (hostActivityContext as BaseDIActivity).showSuccess(s)
    }

    fun showError(s:String) {
        (hostActivityContext as BaseDIActivity).showError(s)
    }
    fun setHostContext(context: Context) {
        this.hostActivityContext = context
    }

    fun execute(runnable:Runnable){
        appExecutors.networkIO().execute(runnable)
    }
}
