package com.techticz.app.ui.activity

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.ui.adapter.MealPlanPagerAdapter
import com.techticz.app.viewmodel.BrowseDietPlanViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.UserResponse
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_browse_diet_plans.*
import kotlinx.android.synthetic.main.content_browse_diet_plan.*
import timber.log.Timber
import android.widget.Toast
import com.techticz.app.ui.event.FreshLoadPlans
import com.techticz.app.ui.event.FreshLoadUser
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode



class BrowseDietPlansActivity : BaseDIActivity(), MealPlanPagerAdapter.CallBack {

    private var dietPlansViewModel: BrowseDietPlanViewModel? = null

    private var featuredPlanList: List<DietPlan>? = ArrayList<DietPlan>()
    private var myPlanList: List<DietPlan>? = ArrayList<DietPlan>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_diet_plans)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout
        fab.setOnClickListener { view ->
            onCreatePlanClicked()
        }
        scroller_featured_plans.adapter = MealPlanPagerAdapter(this,featuredPlanList,this)

        scroller_featured_plans.setItemTransformer(ScaleTransformer.Builder()
                        .setMaxScale(1.01f)
                        .setMinScale(0.95f)
                        .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                        .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                        .build());

        scroller_my_plans.adapter = MealPlanPagerAdapter(this,myPlanList,this)

        scroller_my_plans.setItemTransformer(ScaleTransformer.Builder()
                .setMaxScale(1.01f)
                .setMinScale(0.95f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build());

        dietPlansViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(BrowseDietPlanViewModel::class.java)
        dietPlansViewModel?.featuredDietPlansResponse?.observe(this, Observer {
            resource ->
            onFeaturedPlanDataLoaded(resource)

        })
        dietPlansViewModel?.myDietPlansResponse?.observe(this, Observer {
            resource ->
            onMyPlanDataLoaded(resource)

        })

        dietPlansViewModel?.triggerFeaturedMealPlans?.value = true
        dietPlansViewModel?.triggerMyMealPlans?.value = true

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onEvent(event: FreshLoadPlans) {
        EventBus.getDefault().removeStickyEvent(event)
        scroller_featured_plans.adapter?.notifyDataSetChanged()
        scroller_my_plans.adapter?.notifyDataSetChanged()
    }
    private fun onUserLoaded(res: Resource<UserResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                var handler : Handler = Handler()
                handler.postDelayed(Runnable { dietPlansViewModel?.triggerFeaturedMealPlans?.value = true },1*1000)

            }


        }

    }

    private fun onFeaturedPlanDataLoaded(resource: Resource<BrowseDietPlanResponse>?) {
        Timber.d("dietPlansViewModel?.featuredDietPlansResponse? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        when(resource?.status){
            Status.LOADING->{
                showProgress()
            }
            Status.SUCCESS->
            {
                showSuccess("Diet Plan Fetched")
                var plans = resource?.data?.plans
                var dailyCal = baseuserViewModel?.liveUserResponse?.value?.data?.user?.dailyRequiredCaloriesAsPerGoal
                plans?.forEach {
                    var diff = it.basicInfo?.dailyCalories!! - dailyCal!!.toInt()
                    if(diff < 0){diff = 0-diff}
                    if(diff<300){
                        it.apply { isRecommonded = true }
                    }
                }
                hideProgress()
                (scroller_featured_plans.adapter as MealPlanPagerAdapter).data.clear()
                (scroller_featured_plans.adapter as MealPlanPagerAdapter).data.addAll(plans!!)
                (scroller_featured_plans.adapter as MealPlanPagerAdapter).notifyDataSetChanged()
              //  scroller.getRecycledViewPool().setMaxRecycledViews(1,0);

            }
            Status.ERROR->
            {
                hideProgress()
                showError("Couldnt fetch diet plans")
            }
        }

    }

    private fun onMyPlanDataLoaded(resource: Resource<BrowseDietPlanResponse>?) {
        Timber.d("dietPlansViewModel?.myDietPlansResponse? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        when(resource?.status){
            Status.LOADING->{
                showProgress()
            }
            Status.SUCCESS->
            {
                //showSuccess("My Diet Plan Fetched")
                hideProgress()
                (scroller_my_plans.adapter as MealPlanPagerAdapter).data.clear()
                var plans:ArrayList<DietPlan> = ArrayList()
                plans.addAll(resource?.data?.plans!!)
                plans.add(DietPlan().apply { id = "add_new" })
                (scroller_my_plans.adapter as MealPlanPagerAdapter).data.addAll(plans)
                (scroller_my_plans.adapter as MealPlanPagerAdapter).notifyDataSetChanged()
                //  scroller.getRecycledViewPool().setMaxRecycledViews(1,0);

            }
            Status.ERROR->
            {
                hideProgress()
                showError("Couldnt fetch diet plans")
            }
        }

    }

    override fun onMealPlanCalendarClicked(plan: DietPlan?) {
        navigator.startDietChartScreen(this,plan)
    }

    override fun onMealPlanItemClicked(plan: DietPlan?) {
        navigator.startDietPlanActivity(DietPlanActivity.MODE_EXPLORE,plan);
    }

    override fun onAddMealPlanClicked() {
        onCreatePlanClicked()
    }

    private fun onCreatePlanClicked() {
        navigator.startDietPlanActivity(DietPlanActivity.MODE_CREATE_NEW,null)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            finish()
        }
    }
}
