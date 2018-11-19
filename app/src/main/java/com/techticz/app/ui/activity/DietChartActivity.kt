package com.techticz.app.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.google.android.material.snackbar.Snackbar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.techticz.app.model.DietPlanResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.ui.adapter.DietChartPagerAdapter

import com.techticz.dietcalendar.R
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.UserResponse
import com.techticz.app.model.meal.Meal
import com.techticz.app.repo.DietPlanRepository
import kotlinx.android.synthetic.main.activity_diet_chart.*
import org.parceler.Parcels
import com.techticz.app.repo.UserRepository
import com.techticz.app.ui.customView.MealView
import com.techticz.app.viewmodel.DietChartViewModel
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.ui.DietCalendarApplication
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import timber.log.Timber
import javax.inject.Inject


class DietChartActivity : BaseDIActivity(), UserRepository.UserProfileCallback, DietPlanRepository.DietPlanCallBack {


    override fun onUpdated(id: String) {
        showError("Plan activated successfully..")
        baseuserViewModel.triggerUserId.value = LoginUtils.getCurrentUserId()
        baseuserViewModel.liveUserResponse.observe(this, Observer {
            res-> onUserLoaded(res)
        })
    }

    override fun onUpdateFailure() {
        hideProgress()
        showError("Error while activating plan..")
    }

    @Inject
    lateinit var dietChartViewModel: DietChartViewModel

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: DietChartPagerAdapter? = null
    lateinit var dietPlan: DietPlan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_chart)

        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout

        initData()
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = DietChartPagerAdapter(supportFragmentManager)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }

    private fun initData() {
        dietPlan = Parcels.unwrap<Any>(intent.getParcelableExtra("plan")) as DietPlan
        dietChartViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(DietChartViewModel::class.java)
        dietChartViewModel.liveDietPlanResponse.observe(this, Observer { resources->
            onDietChartLoaded(resources)
        })

        dietChartViewModel.triggerFetchDietPlan.value = dietPlan.id
    }

    private fun onDietChartLoaded(resources: Resource<DietPlanResponse>?) {
        when(resources?.status){
            Status.LOADING->{}
            Status.SUCCESS->{
                // Set up the ViewPager with the sections adapter.
                dietChartViewModel.autoLoadChildren(this, listOf(1,2,3,4,5,6,7))
                container.adapter = mSectionsPagerAdapter
                container.setOffscreenPageLimit(6);
                container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
                tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

            }
            Status.ERROR->{}
        }
    }


    private lateinit var menu: Menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_diet_chart, menu)
        this.menu  = menu;
        var activePlanId = baseuserViewModel.liveUserResponse?.value?.data?.user?.activePlan
        var dietPlan = Parcels.unwrap<Any>(intent.getParcelableExtra("plan")) as DietPlan
        if(dietPlan.id != activePlanId){

        } else {
            menu.findItem(R.id.action_activate).setVisible(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        } else if(id == R.id.action_activate){
            activatePlan()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun activatePlan() {
        var user  = baseuserViewModel.liveUserResponse.value?.data?.user
        user?.activePlan  = dietChartViewModel?.liveDietPlanResponse?.value?.data?.dietPlan?.id
        baseuserViewModel.updateUser(user,this)
    }


    private fun onUserLoaded(res: Resource<UserResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                Timber.d("User fetched with Plan ID:"+res?.data?.user?.activePlan)
                hideProgress()
                navigator.startDashBoard()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            var plateId = data?.getStringExtra("plateId")
            var daySection = data?.getIntExtra("daySection",0)
            var mealType = data?.getStringExtra("mealType")
            //add this plate to this plan
            requestingMealView?.mealPlateViewModel?.triggerMealPlateID?.value = Meal(requestingMealView?.mealPlateViewModel?.triggerMealPlateID?.value?.mealType!!,plateId)
            requestingMealView?.fillDetails(requestingMealView?.mealPlateViewModel!!)
            dietChartViewModel?.addMealInDietPlan(daySection,mealType,plateId,this)
        }
    }

    override fun onPlanCreated(planId: DietPlan) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreatePlanFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlanUpdated(plan: DietPlan) {
        showSuccess("Plan updated successfully..")

    }

    override fun onPlanUpdateFailure() {
        showError("Plan could not be updated!")
    }

    override fun onRegistered(userId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRegistrationFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var requestingMealView: MealView

    fun startBrowsePlateScreen(mealView: MealView) {
        requestingMealView = mealView
        navigator.startBrowsePlateScreen(this,dietChartViewModel?.triggerFetchDietPlan?.value!!,mealView?.daySection!!,mealView?.mealPlateViewModel?.triggerMealPlateID?.value?.mealType?.id!!)
    }
}
