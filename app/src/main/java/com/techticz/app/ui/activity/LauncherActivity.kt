package com.techticz.dietcalendar.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.techticz.app.ui.Navigator
import com.techticz.dietcalendar.R
import com.techticz.dietcalendar.databinding.ActivityLauncherBinding
import com.techticz.dietcalendar.model.LauncherResponse
import com.techticz.dietcalendar.viewmodel.LauncherViewModel
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseDIActivity
import dagger.android.DispatchingAndroidInjector
import kotlinx.android.synthetic.main.activity_launcher.*
import timber.log.Timber
import javax.inject.Inject

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LauncherActivity : BaseDIActivity() {

    @Inject
    lateinit var welcomeMessage: String
    @Inject
    lateinit var  navigator:Navigator

    private var launcherViewModel: LauncherViewModel? = null
    private var launcherBinding: ActivityLauncherBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE


        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.hide()
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //setContentView(R.layout.activity_launcher)
        launcherBinding = DataBindingUtil.setContentView(this, R.layout.activity_launcher)
        Timber.d(welcomeMessage)
        launcherViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(LauncherViewModel::class.java)
        launcherViewModel?.launcherResponse?.observe(this, Observer {
            resource ->
            onDataLoaded(resource)

        })
        showToast(welcomeMessage);

        launcherBinding?.viewModel1 = launcherViewModel

        var handler :Handler = Handler()
        handler.postDelayed(Runnable { launcherViewModel?.triggerLaunch?.value = true },1*1000)

        //launcherViewModel?.triggerFetchingMealPlans?.value = true
    }


    private fun onDataLoaded(resource: Resource<LauncherResponse>?) {
        Timber.d("Launcher Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        when(resource?.status){
            Status.LOADING->{
                launcherBinding?.tvTop?.text = "Loading.."
            }
            Status.SUCCESS->
            {
                launcherBinding?.tvCenter?.text = resource.data?.launchMessage
                Toast.makeText(this, resource.data?.launchMessage, Toast.LENGTH_SHORT).show()
                navigator.startDashBoard()
            }
            Status.ERROR->
            {
                launcherBinding?.tvCenter?.text = resource.message
            }
        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    companion object {

    }
}
