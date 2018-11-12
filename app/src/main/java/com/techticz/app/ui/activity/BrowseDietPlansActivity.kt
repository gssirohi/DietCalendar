package com.techticz.app.ui.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.ui.Navigator
import com.techticz.app.ui.adapter.MealPlanPagerAdapter
import com.techticz.app.viewmodel.BrowseDietPlanViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.UserResponse
import com.techticz.app.viewmodel.UserViewModel
import com.techticz.auth.utils.LoginUtils
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_browse_diet_plans.*
import kotlinx.android.synthetic.main.content_browse_diet_plan.*
import timber.log.Timber
import javax.inject.Inject

class BrowseDietPlansActivity : BaseDIActivity(), MealPlanPagerAdapter.CallBack {

    private var dietPlansViewModel: BrowseDietPlanViewModel? = null

    private var planList: List<DietPlan>? = ArrayList<DietPlan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_diet_plans)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout

        scroller.adapter = MealPlanPagerAdapter(planList,this)

        scroller.setItemTransformer(ScaleTransformer.Builder()
                        .setMaxScale(1.05f)
                        .setMinScale(0.8f)
                        .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                        .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                        .build());

        dietPlansViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(BrowseDietPlanViewModel::class.java)
        dietPlansViewModel?.dietPlansResponse?.observe(this, Observer {
            resource ->
            onDataLoaded(resource)

        })

        dietPlansViewModel?.triggerFetchingMealPlans?.value = true

    }
    private fun onUserLoaded(res: Resource<UserResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                var handler : Handler = Handler()
                handler.postDelayed(Runnable { dietPlansViewModel?.triggerFetchingMealPlans?.value = true },1*1000)

            }


        }

    }
    private fun onDataLoaded(resource: Resource<BrowseDietPlanResponse>?) {
        Timber.d("dietPlansViewModel?.dietPlansResponse? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        when(resource?.status){
            Status.LOADING->{
                showProgress()
            }
            Status.SUCCESS->
            {
                showSuccess("Diet Plan Fetched")
                hideProgress()
                (scroller.adapter as MealPlanPagerAdapter).data.clear()
                (scroller.adapter as MealPlanPagerAdapter).data.addAll(resource?.data?.plans!!)
                (scroller.adapter as MealPlanPagerAdapter).notifyDataSetChanged()
              //  scroller.getRecycledViewPool().setMaxRecycledViews(1,0);

            }
            Status.ERROR->
            {
                hideProgress()
                showError("Couldnt fetch diet plans")
            }
        }

    }
    override fun onMealPlanItemClicked(plan: DietPlan?) {
        navigator.startDietChartScreen(this,plan)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            finish()
        }
    }
}
