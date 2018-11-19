package com.techticz.app.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.constants.Meals
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.model.meal.Meal
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.ui.customView.PlateView
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status

import kotlinx.android.synthetic.main.activity_meal_plate.*
import kotlinx.android.synthetic.main.meal_plate_content_layout.view.*
import kotlinx.android.synthetic.main.plate_desc_layout.view.*
import kotlinx.android.synthetic.main.plate_layout.view.*
import timber.log.Timber

class MealPlateActivity : BaseDIActivity() {
    private var mode: Int = 0
    private var plateId: String? = null
    private lateinit var plateViewModel: MealPlateViewModel
    companion object{
        var MODE_NEW = 0
        var MODE_EXPLORE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plate)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        fab.setOnClickListener { view ->
            //proceedClicked()
        }
        initData()
        initUI()
    }

    private fun initData() {
        plateViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(MealPlateViewModel::class.java)

        mode = intent.getIntExtra("mode",0)
        if(mode == MODE_NEW){

        } else if(mode == MODE_EXPLORE){
            plateId = intent.getStringExtra("plateId")

        }

    }
    lateinit var plateView:PlateView

    private fun initUI() {

        if(mode == MODE_EXPLORE) {
            plateViewModel?.triggerMealPlateID.value = Meal(Meals.BREAKFAST, plateId)
            supportActionBar?.title = "Plate Details"
            plateView = PlateView(1,this,PlateView.MODE_EXPANDED)
        } else if(mode == MODE_NEW){
            supportActionBar?.title = "Create Plate"
            plateView = PlateView(1,this,PlateView.MODE_NEW)
        }
        plateViewModel?.autoLoadChildren(this)

        plateView.fillDetails(plateViewModel)
        plateView.aiv_meal_plate.visibility = View.GONE
        fl_plate_container.addView(plateView)

        plateViewModel?.liveImage?.observe(this, Observer {
            resource->
            onImageModelLoaded(resource)
        })
        plateViewModel?.liveMealPlateResponse?.observe(this, Observer { resource->onPlateLoaded(resource) })
    }

    private fun onPlateLoaded(resource: Resource<MealPlateResponse>?) {
        when(resource?.status) {
            Status.SUCCESS -> {
                supportActionBar?.title = resource?.data?.mealPlate?.basicInfo?.name?.english
            }

        }
    }

    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d("this.mealPlateViewModel?.liveImage? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
                aiv_meal_plate_app_bar.setImageViewModel(resource?.data,this)
            }

        }
    }

    fun startBrowsingRecipe() {
        navigator.startBrowseRecipeScreen(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            var recipeId = data?.getStringExtra("recipeId")


            //add this plate to this plan
             var res = plateView?.mealPlateViewModel?.liveRecipeViewModelList?.value
            var newList = ArrayList<RecipeViewModel>()
            res?.data?.let { newList.addAll(it) }
            var vm = RecipeViewModel()
            vm.autoLoadChildren(this)
            vm.triggerRecipeItem.value = RecipeItem(recipeId,1)
            newList.add(vm)

            var recipeViewModelListResource = Resource<List<RecipeViewModel>>(Status.COMPLETE, newList, "Added recipes..", DataSource.LOCAL)
            plateView?.mealPlateViewModel?.liveRecipeViewModelList?.value= recipeViewModelListResource
            plateView?.recipeRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }


}
