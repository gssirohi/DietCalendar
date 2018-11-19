package com.techticz.dietcalendar.ui.activity

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.R
import com.techticz.dietcalendar.model.LauncherResponse
import com.techticz.dietcalendar.viewmodel.LauncherViewModel
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import timber.log.Timber
import javax.inject.Inject
import com.techticz.app.model.UserResponse
import kotlinx.android.synthetic.main.activity_launch.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LauncherActivity : BaseDIActivity() {

    @Inject
    lateinit var welcomeMessage: String

    private var launcherViewModel: LauncherViewModel? = null

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
        setContentView(R.layout.activity_launch)
        Timber.d(welcomeMessage)
        launcherViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(LauncherViewModel::class.java)
        launcherViewModel?.launcherResponse?.observe(this, Observer {
            resource ->
            onDataLoaded(resource)

        })
        showToast(welcomeMessage);

        var handler :Handler = Handler()
        handler.postDelayed(Runnable { launcherViewModel?.triggerLaunch?.value = true },1*1000)

        //launcherViewModel?.triggerFeaturedMealPlans?.value = true
    }

    private fun onUserLoaded(res: Resource<UserResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                if(res?.data?.user != null){
                    //user is registered
                    if(TextUtils.isEmpty(res?.data?.user?.activePlan)){
                        Log.d("LOGIN", "Starting Browse Plan..")
                        tv_bottom?.text = "Starting Browse Plan.."
                        navigator.startBrowsePlanScreen()
                    } else {
                        Log.d("LOGIN", "Starting Dashboard..")
                        tv_bottom?.text = "Starting Dashboard.."
                        navigator.startDashBoard()
                    }
                    finish()
                }
            }

            Status.EMPTY->{
                Log.d("LOGIN","Registering User..")
                tv_bottom?.text = "Registering User.."
                showError(res?.message.toString())
                navigator.startUserProfileScreen()
                finish()

            }
        }

    }


    private fun onDataLoaded(resource: Resource<LauncherResponse>?) {
        Timber.d("Launcher Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        when(resource?.status){
            Status.LOADING->{
                tv_bottom?.text = "Loading.."
            }
            Status.SUCCESS->
            {

                tv_center?.text = resource.data?.launchMessage
                Toast.makeText(this, resource.data?.launchMessage, Toast.LENGTH_SHORT).show()
                if(TextUtils.isEmpty(LoginUtils.getFirbaseUserId(this)) || FirebaseAuth.getInstance().getCurrentUser() == null) {
                    tv_bottom?.text = "starting login.."
                    navigator.navigateToLoginActivity(this);
                } else {
                    //check if registered
                    Log.d("LOGIN","Checking profile..")
                    tv_bottom?.text = "checking profile.."
                   // baseuserViewModel.triggerUserId.value = LoginUtils.getCurrentUserId()

                    baseuserViewModel.liveUserResponse.observe(this, Observer { res->onUserLoaded(res) })

                }

            }
            Status.ERROR->
            {
                tv_center?.text = resource.message
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            if (!TextUtils.isEmpty(LoginUtils.getFirbaseUserId(this))) {
                //check if user is already registered
                Log.d("LOGIN","Checking profile..")
                baseuserViewModel.liveUserResponse.observe(this, Observer { res->onUserLoaded(res) })

                baseuserViewModel.triggerUserId.value = LoginUtils.getCurrentUserId()
            }
        }
    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    companion object {

    }
}
