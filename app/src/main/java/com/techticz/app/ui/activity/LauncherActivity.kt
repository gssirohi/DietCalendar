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
import com.techticz.app.model.kitchen.SyncKitchenRequest
import com.techticz.app.model.kitchen.SyncKitchenResult
import com.techticz.app.model.launch.AppVersionInfo
import com.techticz.app.model.launch.DocumentVersionInfo
import com.techticz.app.model.launch.Launching
import com.techticz.app.viewmodel.KitchenViewModel
import kotlinx.android.synthetic.main.activity_launch.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LauncherActivity : BaseDIActivity() {

    @Inject
    lateinit var welcomeMessage: String

    private var launcherViewModel: LauncherViewModel? = null

    private var kitchenViewModel: KitchenViewModel? = null

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
        kitchenViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(KitchenViewModel::class.java)

        executeLaunchingCheck()

    }

    private fun executeLaunchingCheck() {
        launcherViewModel?.launcherResponse?.observe(this, Observer {
            resource ->
            onLaunchingDataLoaded(resource)

        })
        var handler :Handler = Handler()
        handler.postDelayed({ launcherViewModel?.triggerLaunch?.value = true },1*1000)
    }


    private fun onLaunchingDataLoaded(resource: Resource<LauncherResponse>?) {
        Timber.d("Launcher Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        when(resource?.status){
            Status.LOADING->{
                tv_bottom?.text = "Loading.."
            }
            Status.SUCCESS->
            {
                tv_center?.text = resource.data?.launching?.launchMessage
                if(isAppUpdateRequired(resource.data?.launching?.appVersionInfo)){
                    executeAppUpdateFlow()
                } else if(isDocumentSyncRequired(resource.data?.launching?.documentVersionInfo)){
                    executeDocumentSync(resource.data?.launching?.documentVersionInfo)
                } else {
                    executeUserLoginCheck(resource.data?.launching)
                }

                resource.data?.launching?.let { prefRepo.launching = it }
            }
            Status.ERROR->
            {
                tv_center?.text = resource.message
            }
        }

    }


    private fun isAppUpdateRequired(appVersionInfo: AppVersionInfo?): Boolean {
        return false
    }

    private fun executeAppUpdateFlow() {
        //Do Nothing
    }

    private fun executeDocumentSync(newVersion: DocumentVersionInfo?) {
        var oldVersion = prefRepo.documentVersion

        kitchenViewModel?.liveSyncKitchenResponse?.observe(this, Observer {
            resource ->
            onKitchenSyncDataLoaded(resource)

        })
        var syncRequest = SyncKitchenRequest(
                food = oldVersion?.food!! < newVersion?.food!!,
                recipe = oldVersion?.recipe!! < newVersion?.recipe!!,
                plate = oldVersion?.plate!! < newVersion?.plate!!,
                plan = oldVersion?.plan!! < newVersion?.plan!!
        )
        var handler :Handler = Handler()
        handler.postDelayed({ kitchenViewModel?.triggerKitchenSync(syncRequest,this) },1*1000)
    }

    private fun onKitchenSyncDataLoaded(resource: Resource<String>?) {
        when(resource?.status) {
            Status.LOADING -> {
                tv_bottom?.text = resource?.message
            }
            Status.SUCCESS -> {
                Log.d("Launcher","document synced"+resource.data)
                var current = prefRepo.documentVersion


                if(resource.data.equals("food")) {
                    prefRepo.documentVersion = current?.apply { food = prefRepo.launching?.documentVersionInfo?.food }
                } else if(resource.data.equals("recipe")) {
                    prefRepo.documentVersion = current?.apply { recipe = prefRepo.launching?.documentVersionInfo?.recipe }
                } else if(resource.data.equals("plate")) {
                    prefRepo.documentVersion = current?.apply { plate = prefRepo.launching?.documentVersionInfo?.plate }
                } else if(resource.data.equals("plan")) {
                    prefRepo.documentVersion = current?.apply { plan = prefRepo.launching?.documentVersionInfo?.plan }
                }
                tv_bottom?.text = resource.message
            }
            Status.COMPLETE -> {
                Log.d("Launcher","All sync completed!!!")
                tv_bottom?.text = resource.message
                executeUserLoginCheck(prefRepo?.launching)
            }
            Status.ERROR -> {
                tv_center?.text = resource.message
            }
        }
    }

    private fun isDocumentSyncRequired(new: DocumentVersionInfo?): Boolean {
        var old = prefRepo.documentVersion

        if(old?.food!! < new?.food!!){
            return true
        }
        if(old?.recipe!! < new?.recipe!!){
            return true
        }
        if(old?.plate!! < new?.plate!!){
            return true
        }
        if(old?.plan!! < new?.plan!!){
            return true
        }
        return false
    }

    private fun executeUserLoginCheck(launching: Launching?) {
        if(launching?.loginInfo?.loginType.equals("online")) {
            if (!LoginUtils.isUserLoggedIn(this)) {
                tv_bottom?.text = "Starting login.."
                navigator.navigateToLoginActivity(this);
            } else {
                //check if registered
                //user Id must have been set and data fetched while onCreate of base activity
                Log.d("LOGIN", "Checking online user profile..")
                tv_bottom?.text = "Checking online profile.."
                baseuserViewModel.triggerUserId.value = LoginUtils.getCurrentUserId()
                baseuserViewModel.liveUserResponse.observe(this, Observer { res -> onUserLoaded(res) })
            }
        } else {
            // offline user is always logged In : Just need to check if offline registered
            Log.d("LOGIN", "Checking offline user profile..")
            tv_bottom?.text = "Checking offline user profile.."
            baseuserViewModel.liveUserResponse.observe(this, Observer { res -> onUserLoaded(res) })
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
                Log.d("LOGIN","Onboarding User..")
                tv_bottom?.text = "Onboarding User.."
                // showError(res?.message.toString())
                // navigator.startUserProfileScreen()
                navigator.startOnboarding()
                finish()

            }
        }

    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    companion object {

    }
}
