package com.techticz.app.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import com.techticz.app.model.BrowseMealPlanResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.ui.Navigator
import com.techticz.app.ui.adapter.MealPlanPagerAdapter
import com.techticz.app.viewmodel.BrowseDietPlanViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseDIActivity
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_browse_diet_plans.*
import kotlinx.android.synthetic.main.content_browse_diet_plan.*
import timber.log.Timber
import javax.inject.Inject

class BrowseDietPlansActivity : BaseDIActivity(), MealPlanPagerAdapter.CallBack {

    @Inject
    lateinit var  navigator: Navigator

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
            Timber.d("Data Changed : Source=" + resource?.dataSource)
            onDataLoaded(resource)

        })

        var handler : Handler = Handler()
        handler.postDelayed(Runnable { dietPlansViewModel?.triggerFetchingMealPlans?.value = true },1*1000)

    }

    private fun onDataLoaded(resource: Resource<BrowseMealPlanResponse>?) {
        //launcherBinding?.viewModel1 = launcherViewModel
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


            }
            Status.ERROR->
            {
                hideProgress()
                showError("Couldnt fetch diet plans")
            }
        }

    }
    override fun onMealPlanItemClicked(plan: DietPlan?) {
        navigator.startDietChartScreen(plan)
    }
}
