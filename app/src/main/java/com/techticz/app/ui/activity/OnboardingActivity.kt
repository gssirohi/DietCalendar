package com.techticz.app.ui.activity

import android.os.Build
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

import androidx.fragment.app.FragmentPagerAdapter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.chip.ChipGroup
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.UserResponse
import com.techticz.app.model.food.Nutrition
import com.techticz.app.model.user.*
import com.techticz.app.repo.UserRepository
import com.techticz.app.ui.customView.NutritionDetailsView
import com.techticz.app.util.Utils
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.auth.utils.LoginUtils

import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import timber.log.Timber
import android.R.attr.startYear
import android.app.DatePickerDialog
import android.text.format.DateUtils
import android.widget.DatePicker
import com.google.android.material.chip.Chip
import com.techticz.app.model.food.Nutrients
import kotlinx.android.synthetic.main.activity_onboarding.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class OnboardingActivity : BaseDIActivity(), UserRepository.UserProfileCallback {
    override fun onUserRegistered(userId: String) {
        baseuserViewModel.triggerUserId.value = userId
    }

    override fun onUserRegistrationFailure() {

    }

    override fun onUserUpdated(id: String) {

    }

    override fun onUserUpdateFailure() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        //activityCollapsingToolbar = toolbar_layout

        b_continue.setOnClickListener({proceed()})

        baseuserViewModel.liveUserResponse.observe(this, Observer { res -> onUserLoaded(res) })

    }

    private fun onUserLoaded(res: Resource<UserResponse>?) {
        when (res?.status) {
            Status.SUCCESS -> {
                    hideProgress()
                    showSuccess("Hi " + res?.data?.user?.basicInfo?.name + ", Successfuly On boarded")
                    navigator.startBrowsePlanScreen()
                    finish()

            }
            Status.EMPTY -> {
                Timber.d("User is EMPTY: NOT Registered!!")
            }
        }
    }

    private fun proceed() {
        if(TextUtils.isEmpty(til_name.editText?.text) || til_name.editText?.text?.length!! <= 2){
            til_name.error = "Enter valid display name"
            return
        }

        var newUser = User("local")
        newUser.apply {
            basicInfo.credential = "local.local"
        }
        newUser.basicInfo.name = til_name.editText?.text.toString()
        baseuserViewModel.registerUser(newUser, this)

    }

}
