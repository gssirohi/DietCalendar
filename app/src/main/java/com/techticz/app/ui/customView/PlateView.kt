package com.techticz.app.ui.customView

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.ui.adapter.MealFoodAdapter
import com.techticz.app.ui.adapter.MealRecipesAdapter
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.mealplate.Items
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.ui.activity.DietChartActivity
import com.techticz.app.ui.activity.MealPlateActivity
import com.techticz.app.util.Utils
import com.techticz.auth.utils.LoginUtils

import kotlinx.android.synthetic.main.meal_plate_content_layout.view.*
import kotlinx.android.synthetic.main.plate_desc_layout.view.*
import kotlinx.android.synthetic.main.plate_layout.view.*

import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class PlateView(val daySection:Int?, context:Context?,var mode:Int, var parent: ViewGroup) : FrameLayout(context) {
    companion object {
        var MODE_EXPLORE = 0
        var MODE_NEW = 1 // will not have plate or plateID
        var MODE_EDIT = 2
        var MODE_COPY_FROM_PLATE = 3
    }
    var mealPlateViewModel: MealPlateViewModel? = null

    init {
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams = params
        init(parent)
    }

    private fun init(parent: ViewGroup?) {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.plate_layout, parent, false) as ViewGroup

        addView(itemView)
    }

    private fun onAddRecipeClicked() {
        if(context is MealPlateActivity){
            (context as MealPlateActivity).startBrowsingRecipe()
        }
    }


    fun fillDetails(mealViewModel: MealPlateViewModel) {
        this.mealPlateViewModel = mealViewModel
        if(TextUtils.isEmpty(mealViewModel.triggerMealPlateID.value?.mealPlateId)){
            // meal plate is empty
            if(mode == MODE_NEW || mode == MODE_COPY_FROM_PLATE || mode == MODE_EDIT){
                configureUIinEditMode(true)

                tv_recipes_header.visibility = View.VISIBLE
                tv_foods_header.visibility = View.VISIBLE
                prepareChildrenUI()
            } else {
                //something is wrong
            }



        } else {

            if(mode == MODE_COPY_FROM_PLATE){
                configureUIinEditMode(true)
            } else if(mode == MODE_EDIT){
                configureUIinEditMode(true)
            } else if(mode == MODE_EXPLORE){
                configureUIinEditMode(false)
            }

            ll_meal_plate_desc.visibility = View.VISIBLE
            tv_plate_desc.visibility = View.VISIBLE

            mealViewModel?.liveMealPlateResponse?.observe(context as BaseDIActivity, Observer { resource ->
                onMealPlateLoaded(resource)

            })
        }

        b_add_recipe.setOnClickListener {
            onAddRecipeClicked()
        }

        if(context is MealPlateActivity){
            fab_explore_plate.visibility = View.GONE
        } else {
            fab_explore_plate.setOnClickListener({ explorePlate() })
        }
    }

    private fun explorePlate() {
        if(context is DietChartActivity){
            (context as DietChartActivity).navigator.startExplorePlateScreen(mealPlateViewModel?.triggerMealPlateID?.value?.mealPlateId)
        }
    }

    private fun onMealPlateLoaded(resource: Resource<MealPlateResponse>?) {
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit_plate_desc.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                spin_kit_plate_desc.visibility = View.INVISIBLE

                if(mode == MODE_COPY_FROM_PLATE){
                    til_plate_name.editText?.setText(resource.data?.mealPlate?.basicInfo?.name?.english+"_Copy"+"_"+LoginUtils.getUserName().substring(0,2))
                    til_plate_desc.editText?.setText(resource.data?.mealPlate?.basicInfo?.desc)

                } else {
                    til_plate_name.editText?.setText(resource.data?.mealPlate?.basicInfo?.name?.english)
                    til_plate_desc.editText?.setText(resource.data?.mealPlate?.basicInfo?.desc)

                    tv_plate_name.setText(resource.data?.mealPlate?.basicInfo?.name?.english)
                    tv_plate_desc.setText(resource.data?.mealPlate?.basicInfo?.desc)
                }

                prepareChildrenUI()

            }
            Status.ERROR -> {
                spin_kit_plate_desc.visibility = View.INVISIBLE
                tv_plate_error.text = resource?.message
                tv_plate_error.visibility = View.VISIBLE
            }
        }

    }

    private fun prepareChildrenUI() {
        observeChildViewModels()

        recipeRecyclerView.layoutManager = (androidx.recyclerview.widget.LinearLayoutManager(context))
        recipeRecyclerView.adapter = MealRecipesAdapter(this, null)

        foodRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        foodRecyclerView.adapter = MealFoodAdapter(this, null)
    }

    private fun observeChildViewModels() {
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
                spin_kit_plate_recipes.visibility = View.VISIBLE
            }
            Status.COMPLETE -> {
                spin_kit_plate_recipes.visibility = View.INVISIBLE
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
                spin_kit_plate_recipes.visibility = View.INVISIBLE
                tv_plate_error.text = resource?.message
                tv_plate_error.visibility = View.VISIBLE
            }
        }
    }

    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d("this.mealPlateViewModel?.liveImage? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
                spin_kit_plate_analysis.visibility = View.INVISIBLE
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
                spin_kit_plate_analysis.visibility = View.INVISIBLE
                tv_meal_plate_calories.text = resource?.message
                tv_meal_plate_calories.visibility = View.VISIBLE
            }
        }
    }
    private fun onChildFoodViewModelsDataChanged(resource: Resource<List<FoodViewModel>>?) {
        resource?.isFresh = false
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit_plate_foods.visibility = View.VISIBLE
            }
            Status.COMPLETE -> {
                spin_kit_plate_foods.visibility = View.INVISIBLE
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
                spin_kit_plate_foods.visibility = View.INVISIBLE
                tv_plate_error.text = resource?.message
                tv_plate_error.visibility = View.VISIBLE
            }
        }
    }

    fun configureUIinEditMode(b: Boolean) {
        if(b){
            til_plate_name.visibility = View.VISIBLE
            til_plate_desc.visibility = View.VISIBLE

            tv_plate_name.visibility = View.GONE
            tv_plate_desc.visibility = View.GONE

            b_add_recipe.visibility = View.VISIBLE
            b_add_food.visibility = View.VISIBLE
        } else {
            til_plate_name.visibility = View.GONE
            til_plate_desc.visibility = View.GONE

            tv_plate_name.visibility = View.VISIBLE
            tv_plate_desc.visibility = View.VISIBLE

            b_add_recipe.visibility = View.GONE
            b_add_food.visibility = View.GONE
        }
        recipeRecyclerView.adapter?.notifyDataSetChanged()
        foodRecyclerView.adapter?.notifyDataSetChanged()
    }

    fun removeRecipe(lifecycleOwner: LifecycleOwner,recipe: RecipeItem?) {
        //add recipe to this plate
        mealPlateViewModel?.removeRecipeViewModel(lifecycleOwner,recipe!!)
        //notify adapter
        recipeRecyclerView?.adapter?.notifyDataSetChanged()
    }

    fun validateData(): String? {
        if(TextUtils.isEmpty(til_plate_name.editText?.text.toString())){
            return "please enter plate name"
        }
        if(TextUtils.isEmpty(til_plate_desc.editText?.text.toString())){
            return "please enter plate description"
        }
        if(!mealPlateViewModel?.hasItems()!!){
            return "Please add recipes/foods to your plate"
        }
        return null;
    }

    fun prepareNewPlate(): MealPlate {
        var plate = MealPlate()
        plate?.basicInfo?.name?.english = til_plate_name.editText?.text.toString()
        plate?.basicInfo?.desc = til_plate_desc.editText?.text.toString()
        plate.basicInfo.image = "https://res.cloudinary.com/techticz/image/upload/v1507368860/meals/Daal%20Roti%20Meal_2017.10.07.15.04.19.jpg"

        plate?.items = Items()
        plate?.items?.recipies = ArrayList()
        plate?.items?.foods = ArrayList()
        mealPlateViewModel?.liveRecipeViewModelList?.value?.data?.let { for(recipe in it){
            plate?.items?.recipies?.add(recipe?.triggerRecipeItem?.value)
        } }
        mealPlateViewModel?.liveFoodViewModelList?.value?.data?.let { for(food in it){
            plate?.items?.foods?.add(food?.triggerFoodItem?.value)
        } }

        plate.adminInfo?.createdBy = LoginUtils.getUserCredential()
        plate.adminInfo?.createdOn = Utils.timeStamp()

        plate.id = "PLT_"+LoginUtils.getUserName().substring(0,3)+"_"+plate.adminInfo?.createdOn

        return plate!!
    }

    fun getModifiedPlate(): MealPlate {
        var plate = mealPlateViewModel?.liveMealPlateResponse?.value?.data?.mealPlate
        plate?.basicInfo?.name?.english = til_plate_name.editText?.text.toString()
        plate?.basicInfo?.desc = til_plate_desc.editText?.text.toString()

        plate?.items = Items()
        plate?.items?.recipies = ArrayList()
        plate?.items?.foods = ArrayList()
        mealPlateViewModel?.liveRecipeViewModelList?.value?.data?.let { for(recipe in it){
            plate?.items?.recipies?.add(recipe?.triggerRecipeItem?.value)
        } }
        mealPlateViewModel?.liveFoodViewModelList?.value?.data?.let { for(food in it){
            plate?.items?.foods?.add(food?.triggerFoodItem?.value)
        } }

        return plate!!
    }

}