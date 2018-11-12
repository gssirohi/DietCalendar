package com.techticz.app.ui.customView

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.repo.FoodRepository
import com.techticz.app.repo.RecipeRepository
import com.techticz.app.ui.adapter.MealFoodAdapter
import com.techticz.app.ui.adapter.MealRecipesAdapter
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import kotlinx.android.synthetic.main.meal_layout.view.*
import kotlinx.android.synthetic.main.meal_plate_desc_layout.view.*
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class MealView(daySection:Int?, parent:Context?) : FrameLayout(parent) {
    var mealPlateViewModel: MealPlateViewModel? = null

    init {
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams = params
        init(parent)
    }

    private fun init(parent: Context?) {
        val itemView = LayoutInflater.from(context)
                .inflate(R.layout.meal_layout, null, false) as ViewGroup
        addView(itemView)
        b_add_plate.setOnClickListener({addMealPlate()})
    }

    private fun addMealPlate() {
        (context as BaseDIActivity).showError("Add plate for meal:"+mealPlateViewModel?.triggerMealPlateID?.value?.mealType?.mealName)
    }

    private fun onViewModelDataLoaded(resource: Resource<MealPlateResponse>?) {
        Timber.d("mealPlateViewModel?.liveMealPlateResponse? Data Changed : Status=" + resource?.status + " : Source=" + resource?.dataSource)
        onMealPlateLoaded(resource)
    }

    fun fillDetails(mealViewModel: MealPlateViewModel) {
        this.mealPlateViewModel = mealViewModel
        tv_meal_name.setText(mealViewModel.triggerMealPlateID.value?.mealType?.mealName)

        if(TextUtils.isEmpty(mealViewModel.triggerMealPlateID.value?.mealPlateId)){
            // meal plate is empty
            ll_meal_plate_desc.visibility = View.GONE
            ll_meal_content.visibility = View.GONE
            b_add_plate.visibility = View.VISIBLE
            aiv_meal_plate.setImageResource(R.drawable.bg_night)


        } else {
            ll_meal_plate_desc.visibility = View.VISIBLE
            ll_meal_content.visibility = View.VISIBLE
            b_add_plate.visibility = View.GONE


 //           tv_meal_plate_name.setText(mealViewModel.triggerMealPlateID.value?.mealPlateId)

            mealViewModel?.liveMealPlateResponse?.observe(context as BaseDIActivity, Observer { resource ->
                onViewModelDataLoaded(resource)

            })
        }

    }

    private fun onMealPlateLoaded(resource: Resource<MealPlateResponse>?) {
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {

                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_name.text = resource.data?.mealPlate?.basicInfo?.name?.english
                tv_meal_plate_name.visibility = View.VISIBLE

                observeChildViewModels(resource)

                recipeRecyclerView.layoutManager = (LinearLayoutManager(context))
                recipeRecyclerView.adapter = MealRecipesAdapter(this, null)

                foodRecyclerView.layoutManager = LinearLayoutManager(context)
                foodRecyclerView.adapter = MealFoodAdapter(this, null)

            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_name.text = resource?.message
                tv_meal_plate_name.visibility = View.VISIBLE
            }
        }

    }

    private fun observeChildViewModels(resource: Resource<MealPlateResponse>) {
        mealPlateViewModel?.liveImage?.observe(context as BaseDIActivity, Observer {
            resource->
            onImageModelLoaded(resource)
        })

        mealPlateViewModel?.liveRecipeViewModelList?.observe(context as BaseDIActivity, Observer { resource ->
            Timber.d("mealPlateViewModel?.liveRecipeViewModelList? Data Changed : Status=" + resource?.status + " : Source=" + resource?.dataSource)
            onChildRecipeViewModelsDataChanged(resource)
        })

        mealPlateViewModel?.liveFoodViewModelList?.observe(context as BaseDIActivity, Observer { resource ->
            Timber.d("mealPlateViewModel?.liveFoodViewModelList? Data Changed : Status=" + resource?.status + " : Source=" + resource?.dataSource)
            onChildFoodViewModelsDataChanged(resource)
        })
    }
    private fun onChildRecipeViewModelsDataChanged(resource: Resource<List<RecipeViewModel>>?) {
        resource?.isFresh = false
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.COMPLETE -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = "" + mealPlateViewModel?.getNutrients()?.principlesAndDietaryFibers?.energy+" Cals"
                tv_meal_plate_calories.visibility = View.VISIBLE
                when(mealPlateViewModel?.isVeg()){
                    true->tv_meal_plate_type.setTextColor(Color.parseColor("#ff669900"))
                    else->tv_meal_plate_type.setTextColor(Color.parseColor("#ffcc0000"))
                }
            }

            Status.EMPTY-> {
                /*var recipes = mealPlateViewModel?.liveMealPlateResponse?.value?.data?.mealPlate?.items?.recipies

                var recipeViewModelList = ArrayList<RecipeViewModel>()

                if (recipes != null) {
                    for (recipe in recipes) {
                        var recipeViewModel = RecipeViewModel(RecipeRepository(FirebaseFirestore.getInstance()))
                        recipeViewModel.triggerRecipeItem.value = recipe
                        recipeViewModelList.add(recipeViewModel)
                    }
                }
                var recipeViewModelListResource = Resource<List<RecipeViewModel>>(Status.LOADING, recipeViewModelList, "Loading Meal recipes..", DataSource.LOCAL)
                mealPlateViewModel?.liveRecipeViewModelList?.value = recipeViewModelListResource*/

            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = resource?.message
                tv_meal_plate_calories.visibility = View.VISIBLE
            }
        }
    }

    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d("this.mealPlateViewModel?.liveImage? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.INVISIBLE
                aiv_meal_plate.setImageViewModel(resource?.data,context as LifecycleOwner)
            }
            Status.SUCCESS -> {

            }
            Status.EMPTY -> {
                /*var imageViewModel = ImageViewModel(context)
                imageViewModel.triggerImageUrl.value = mealPlateViewModel?.liveMealPlateResponse?.value?.data?.mealPlate?.basicInfo?.image
                var imageRes = Resource<ImageViewModel>(Status.SUCCESS,imageViewModel,"Loading meal image model success..",DataSource.REMOTE)
                this.mealPlateViewModel?.liveImage?.value = imageRes*/
            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = resource?.message
                tv_meal_plate_calories.visibility = View.VISIBLE
            }
        }
    }
    private fun onChildFoodViewModelsDataChanged(resource: Resource<List<FoodViewModel>>?) {
        resource?.isFresh = false
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit.visibility = View.VISIBLE
            }
            Status.COMPLETE -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = "" + mealPlateViewModel?.getNutrients()?.principlesAndDietaryFibers?.energy+" Cals"
                when(mealPlateViewModel?.isVeg()){
                    true->tv_meal_plate_type.setTextColor(Color.parseColor("#ff669900"))
                    else->tv_meal_plate_type.setTextColor(Color.parseColor("#ffcc0000"))
                }

            }
            Status.EMPTY->{
              /*  var foods = mealPlateViewModel?.liveMealPlateResponse?.value?.data?.mealPlate?.items?.foods

                var foodViewModelList = ArrayList<FoodViewModel>()

                if (foods != null) {
                    for (food in foods) {
                        var foodViewModel = FoodViewModel(FoodRepository(FirebaseFirestore.getInstance()))
                        foodViewModel.triggerFoodItem.value = food
                        foodViewModelList.add(foodViewModel)
                    }
                }
                var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.LOADING, foodViewModelList, "Loading Meal foods..", DataSource.LOCAL)
                mealPlateViewModel?.liveFoodViewModelList?.value = foodViewModelListResource*/
            }
            Status.ERROR -> {
                spin_kit.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = resource?.message
                tv_meal_plate_calories.visibility = View.VISIBLE
            }
        }
    }

}