package com.techticz.app.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.techticz.app.model.DietPlanResponse
import com.techticz.app.model.NutriPair
import com.techticz.app.ui.Navigator
import com.techticz.app.ui.adapter.DashBoardNutriAdapter
import com.techticz.app.ui.customView.MealView
import com.techticz.app.viewmodel.DietChartViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.UserResponse
import com.techticz.app.model.user.User
import com.techticz.app.viewmodel.UserViewModel
import com.techticz.dietcalendar.ui.DietCalendarApplication
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*
import kotlinx.android.synthetic.main.nav_header_dashboard.view.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DashboardActivity : BaseDIActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var dietChartViewModel:DietChartViewModel

    private lateinit var nutriList: ArrayList<NutriPair>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout

        initData()
        initUI()
        nutriList = ArrayList<NutriPair>()
        nutri_scroller.adapter = DashBoardNutriAdapter(nutriList,null)
        nutri_scroller.setItemTransformer(ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build());

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            navigator.startFoodDetails()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun initUI() {


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }



    private fun initData() {
        dietChartViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(DietChartViewModel::class.java)

        baseuserViewModel.liveUserResponse.observe(this, Observer { res->onUserLoaded(res) })

        var day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        tv_day.text = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.US)
        dietChartViewModel.autoLoadChildren(this, listOf(day))

        dietChartViewModel?.liveDietPlanResponse.observe(this, Observer {
            resource->
            onDietPlanLoaded(resource)
        })
        dietChartViewModel?.liveImage?.observe(this, Observer {
            resource->
            onImageModelLoaded(resource)
        })
        dietChartViewModel.getDayMealViewModels(day)?.observe(this, Observer { resources->
            onDayMealLoaded(resources,day)
        })
    }

    private fun onUserLoaded(res: Resource<UserResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                setupNavigationHeader(res?.data?.user)
                setUpNavigationMenu(res?.data?.user)
                if(!TextUtils.isEmpty(res?.data?.user?.activePlan)){
                    dietChartViewModel.triggerFetchDietPlan.value = res?.data?.user?.activePlan
                } else {
                    navigator.startBrowsePlanScreen()
                   // dietChartViewModel.triggerFetchDietPlan.value = "PLN01"
                }
            }
        }
    }

    private fun setUpNavigationMenu(user: User?) {

    }

    private fun setupNavigationHeader(user: User?) {
        nav_view.getHeaderView(0).ll_user_header.setOnClickListener({navigator.startUserProfileScreen()})
        var ivm = ImageViewModel(this);
        ivm.triggerImageUrl.value = user?.basicInfo?.image//LoginUtils.getUserImageUrl()
        nav_view.getHeaderView(0).aiv_user.setImageViewModel(ImageViewModel(this),this)

        nav_view.getHeaderView(0).tv_user_name.text = user?.basicInfo?.name//LoginUtils.getUserName()
        nav_view.getHeaderView(0).tv_user_email.text = user?.basicInfo?.credential//LoginUtils.getUserCredential()
    }

    private fun onDietPlanLoaded(resource: Resource<DietPlanResponse>?) {
        when(resource?.status) {
            Status.SUCCESS->{
                toolbar_layout.title = resource?.data?.dietPlan?.basicInfo?.name
            }
        }

    }

    private fun onDayMealLoaded(resources: Resource<List<MealPlateViewModel>>?, day: Int) {
        when(resources?.status){
            Status.LOADING->{}
            Status.SUCCESS->{

            }
            Status.COMPLETE->{
                var dayNutrients = dietChartViewModel.getDayMealNutrients(day);

                nutriList.add(NutriPair("Fat",dayNutrients?.principlesAndDietaryFibers?.fat,500f))
                nutriList.add(NutriPair("Carb",dayNutrients?.principlesAndDietaryFibers?.carbohydrate,130f))
                nutriList.add(NutriPair("Calory",dayNutrients?.principlesAndDietaryFibers?.energy,800f))
                nutriList.add(NutriPair("Protine",dayNutrients?.principlesAndDietaryFibers?.protien,20f))
                nutriList.add(NutriPair("Iron",dayNutrients?.mineralsAndTraceElements?.iron,20f))
                nutriList.add(NutriPair("Calcium",dayNutrients?.mineralsAndTraceElements?.calcium,20f))

                nutri_scroller.adapter.notifyDataSetChanged()
                nutri_scroller.layoutManager.scrollToPosition(2)

                var mealView = MealView(day,this)
                mealView.fillDetails(dietChartViewModel.getDayMealViewModels(day)?.value?.data?.get(0)!!)
                main_content.addView(mealView,2)

                cgv_fruits.setImageRes(R.drawable.all_fruits)
                cgv_fruits.start("Fruits",100f,40f,R.color.primaryDarkColor)

                cgv_veggies.setImageRes(R.drawable.all_veggies)
                cgv_veggies.start("Veggies",100f,70f,R.color.primaryDarkColor)
            }
            Status.ERROR->{}
        }
    }


    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d(" dietChartViewModel?.liveImage? : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
                aiv_plan.setImageViewModel(resource?.data,this)
            }
            Status.SUCCESS -> {
                aiv_plan.setImageViewModel(resource?.data,this)
            }
            Status.EMPTY -> {
                /*var imageViewModel = ImageViewModel(context)
                imageViewModel.triggerImageUrl.value = mealPlateViewModel?.liveMealPlateResponse?.value?.data?.mealPlate?.basicInfo?.image
                var imageRes = Resource<ImageViewModel>(Status.SUCCESS,imageViewModel,"Loading meal image model success..",DataSource.REMOTE)
                this.mealPlateViewModel?.liveImage?.value = imageRes*/
            }
            Status.ERROR -> {

            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                navigator.startBrowsePlanScreen()
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {
                navigator.startSettingsScreen()
            }
            R.id.nav_share -> {
                navigator.navigateToLoginActivity(this)
            }
            R.id.nav_send -> {
                navigator.startDeveloperScreen()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
