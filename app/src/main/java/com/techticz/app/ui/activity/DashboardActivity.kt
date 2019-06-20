package com.techticz.app.ui.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.techticz.app.model.DietPlanResponse
import com.techticz.app.model.NutriPair
import com.techticz.app.ui.adapter.DashBoardNutriAdapter
import com.techticz.app.ui.customView.MealView
import com.techticz.app.viewmodel.DietChartViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.UserResponse
import com.techticz.app.model.user.User
import com.techticz.app.util.Utils
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.nav_header_dashboard.view.*
import timber.log.Timber
import java.util.*
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.techticz.app.ui.event.FreshLoadDashboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class DashboardActivity : BaseDIActivity(), NavigationView.OnNavigationItemSelectedListener{


    lateinit var dietChartViewModel:DietChartViewModel

    private lateinit var nutriList: ArrayList<NutriPair>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout

        initData()
        initUI()
        nutriList = ArrayList<NutriPair>()
        nutri_scroller.adapter = DashBoardNutriAdapter(nutriList,null)
        nutri_scroller.setItemTransformer(ScaleTransformer.Builder()
                .setMaxScale(1.1f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                .build());

        fab.setOnClickListener { view ->
            dietChartViewModel?.liveDietPlanResponse?.value?.data?.dietPlan?.let{
                navigator.startDietChartScreen(this,it)
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }

    private fun initUI() {


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public fun onEvent(event:FreshLoadDashboard){
        EventBus.getDefault().removeStickyEvent(event)
        dietChartViewModel?.removeAllObservers(this)
        baseuserViewModel.liveUserResponse.observe(this, Observer { res->onUserLoaded(res) })
    }

    private fun initData() {
        dietChartViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(DietChartViewModel::class.java)
        baseuserViewModel.liveUserResponse.observe(this, Observer { res->onUserLoaded(res) })
    }

    private fun onUserLoaded(res: Resource<UserResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                setupNavigationHeader(res?.data?.user)
                setUpNavigationMenu(res?.data?.user)
                if(!TextUtils.isEmpty(res?.data?.user?.activePlan)){
                    dietChartViewModel.triggerFetchDietPlan.value = res?.data?.user?.activePlan

                    dietChartViewModel?.liveDietPlanResponse.observe(this, Observer {
                        resource->
                        onDietPlanLoaded(resource)
                    })
                    var day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    Timber.d("DASH Day:"+day)
                    tv_day.text = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.US)
                    dietChartViewModel.autoLoadChildren(this, listOf(day))

                    dietChartViewModel?.liveImage?.observe(this, Observer {
                        resource->
                        onImageModelLoaded(resource)
                    })
                    dietChartViewModel.getDayMealViewModels(day)?.observe(this, Observer { resources->
                        onDayMealLoaded(resources,day)
                    })
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
                Timber.d("Day Meal Completed:"+day)
                var dayNutrients = dietChartViewModel.getDayMealNutrients(day);
                var user = baseuserViewModel?.liveUserResponse?.value?.data?.user
                nutriList.clear()
                user?.rda?.fat?.let {
                    nutriList.add(NutriPair("Fat",dayNutrients?.principlesAndDietaryFibers?.fat,it))
                }
                user?.rda?.carbs?.let {
                    nutriList.add(NutriPair("Carbs",dayNutrients?.principlesAndDietaryFibers?.carbohydrate,it))
                }
                user?.rda?.dailyCalories?.let {
                    nutriList.add(NutriPair("Calories",Utils.calories(dayNutrients?.principlesAndDietaryFibers?.energy),it))
                }
                user?.rda?.protine?.let {
                    nutriList.add(NutriPair("Protine",dayNutrients?.principlesAndDietaryFibers?.protien,it))
                }
                user?.rda?.totalFibers?.let {
                    nutriList.add(NutriPair("Total Fibers",dayNutrients?.principlesAndDietaryFibers?.dietaryFiber?.total,it))
                }

                nutri_scroller.adapter?.notifyDataSetChanged()
                nutri_scroller.layoutManager?.scrollToPosition(2)

                var mealView = MealView(day,this)
                var nextMealModel = getDayMealBasedOnTime(day)
                Timber.d("Displaying next meal:"+nextMealModel.triggerMealPlateID?.value?.mealType+" with Plate:"+nextMealModel.triggerMealPlateID?.value?.mealPlateId)
                mealView.fillDetails(nextMealModel)
                var v = main_content.getChildAt(2)
                if(v is MealView){
                    main_content.removeView(v)
                }
                main_content.addView(mealView,2)

                cgv_fruits.setImageRes(R.drawable.all_fruits)
                var fruits = dietChartViewModel.getDayFruitContent(day)
                cgv_fruits.start("Fruits",""+fruits.toInt()+"g",user?.rda?.fruits!!,fruits, R.color.secondaryColor)

                var veggies = dietChartViewModel.getDayVeggiesContent(day)
                cgv_veggies.setImageRes(R.drawable.all_veggies)
                cgv_veggies.start("Veggies",""+veggies.toInt()+"g",user?.rda?.veggies!!,veggies,R.color.secondaryColor)
            }
            Status.ERROR->{}
        }
    }

    private fun getDayMealBasedOnTime(day: Int): MealPlateViewModel {
        var cal = Calendar.getInstance()
        var hour = cal.get(Calendar.HOUR_OF_DAY)
        var mealIndex = 0
        if(hour in 0.. 7){
            mealIndex = 0
        } else if(hour in 8..11){
            mealIndex = 1
        } else if(hour in 12..15){
            mealIndex = 2
        } else if(hour in 16..18){
            mealIndex = 3
        } else if(hour in 19..21){
            mealIndex = 4
        } else if(hour in 22..24){
            mealIndex = 5
        }

        for (i in mealIndex..5) {
            var vm = dietChartViewModel.getDayMealViewModels(day)?.value?.data?.get(i)!!
            if(!TextUtils.isEmpty(vm.triggerMealPlateID?.value?.mealPlateId)){
                return vm
            }
        }

        var vm = dietChartViewModel.getDayMealViewModels(day)?.value?.data?.get(mealIndex)!!
        return vm
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
            R.id.action_diet_chart-> {
                dietChartViewModel?.liveDietPlanResponse?.value?.data?.dietPlan?.let{
                    navigator.startDietChartScreen(this,it)
                }
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_plans -> {
                navigator.startBrowsePlanScreen()
            }
            R.id.nav_plates->{
                navigator.startBrowsePlateScreen(this,"",0,"")
            }
            R.id.nav_recipes -> {

                navigator.startBrowseRecipeScreen(this,null)
            }
            R.id.nav_foods -> {
                navigator.startBrowseFoodScreen(this,null,null)
            }
            R.id.nav_share -> {

            }

            R.id.nav_settings -> {
                navigator.startSettingsScreen()
            }
            R.id.nav_logout -> {
                navigator.navigateToLoginActivity(this)
            }
            R.id.nav_developer_options -> {
                navigator.startDeveloperScreen()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
