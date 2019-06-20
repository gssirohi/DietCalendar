package com.techticz.app.ui.customView

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.chip.Chip
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.ui.adapter.PlateFoodAdapter
import com.techticz.app.ui.adapter.PlateRecipesAdapter
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.constants.Meals
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.app.model.mealplate.Items
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.ui.activity.MealPlateActivity
import com.techticz.app.util.Utils
import com.techticz.auth.utils.LoginUtils
import kotlinx.android.synthetic.main.content_desc_layout.view.*

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
        var meals = Meals.values()
        meals?.forEach {
            var chip = Chip(context)
            chip.text = it.mealName
            chip.tag = it
            chip.isCheckable = true
            chip.isClickable = true
            chip_group_plate_pref_meal.addView(chip)
        }

        til_plate_approx_cal.editText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onApproxCaloryUpdated(s)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    private fun onApproxCaloryUpdated(s: Editable?) {
        if(!TextUtils.isEmpty(s)) {
            try {
                var approxCal = s.toString().toInt()
                this.mealPlateViewModel?.thePlate?.basicInfo?.calories = approxCal.toFloat()
            } catch (e:Exception){
                this.mealPlateViewModel?.thePlate?.basicInfo?.calories = 0f
            }
        }
        updateCalculation()
    }
    private fun updateCalculation() {
        var perServeCalory = mealPlateViewModel?.perPlateCal()
        var approxCalories = mealPlateViewModel?.thePlate?.basicInfo?.calories


        var servingUnit:String = "Plate"

        til_plate_approx_cal.hint = "Approx. Cal/"+servingUnit
        tv_content_serving_qty.text = ""+1
        tv_content_serving_unit.text = servingUnit
        tv_content_calories.text = ""+perServeCalory
        tv_content_calories_serving_text.text = "Cal/"+servingUnit

        if(approxCalories != null && perServeCalory != null && approxCalories>0 && perServeCalory>0) {
            var percentDeviation = (perServeCalory!! / approxCalories!!)*100
            if(percentDeviation < 90){
                tv_calory_deviation.text = "Plate contains only "+percentDeviation.toInt()+"% of declared approx. calories. You should add more items or increase quantity."
                tv_calory_deviation.visibility = View.VISIBLE
            } else if(percentDeviation > 110){
                tv_calory_deviation.text = "Plate contains more than "+percentDeviation.toInt()+"% of declared approx. calories.  You should remove items or reduce quantity."
                tv_calory_deviation.visibility = View.VISIBLE
            } else {
                tv_calory_deviation.visibility = View.GONE
            }
        }
        when(mealPlateViewModel?.isVeg()){
            true->{
                tv_veg_nonveg.setTextColor(Color.parseColor("#ff669900"))
                tv_content_type_text.setText("Veg. Plate")
            }
            else->{
                tv_veg_nonveg.setTextColor(Color.parseColor("#ffcc0000"))
                tv_content_type_text.setText("NonVeg. Plate")
            }
        }
    }
    private fun onAddRecipeClicked() {
        if(context is MealPlateActivity){
            (context as MealPlateActivity).startBrowsingRecipe()
        }
    }
    private fun onAddFoodClicked() {
        if(context is MealPlateActivity){
            (context as MealPlateActivity).startBrowsingFood()
        }
    }


    fun fillDetails(mealViewModel: MealPlateViewModel) {
        this.mealPlateViewModel = mealViewModel
        if(context is MealPlateActivity){
            fab_explore_plate.visibility = View.GONE
            ll_plate_approx_cal.visibility = View.VISIBLE
            tv_pref_meal_header.visibility = View.VISIBLE
            chip_group_plate_pref_meal.visibility = View.VISIBLE
        } else {
            fab_explore_plate.setOnClickListener({ explorePlate() })
            ll_plate_approx_cal.visibility = View.GONE
            tv_pref_meal_header.visibility = View.GONE
            chip_group_plate_pref_meal.visibility = View.GONE
        }
        if(TextUtils.isEmpty(mealViewModel.triggerMealPlateID.value?.mealPlateId)){
            // meal plate is empty
            if(mode == MODE_NEW || mode == MODE_COPY_FROM_PLATE || mode == MODE_EDIT){
                configureUIinEditMode(true)

                tv_recipes_header.visibility = View.VISIBLE
                tv_foods_header.visibility = View.VISIBLE
                prepareChildrenUI()
            } else {
                configureUIinEditMode(false)
                Timber.e("ERROR","Unexpected State Detected")
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

        }
        mealPlateViewModel?.liveMealPlateResponse?.observe(context as BaseDIActivity, Observer { resource ->
            onMealPlateLoaded(resource)

        })

        b_add_recipe.setOnClickListener {
            onAddRecipeClicked()
        }
        b_add_food.setOnClickListener {
            onAddFoodClicked()
        }


    }

    private fun explorePlate() {
        if(context is BaseDIActivity){
            (context as BaseDIActivity).navigator.startExplorePlateScreen(mealPlateViewModel?.triggerMealPlateID?.value?.mealPlateId)
        }
    }

    private fun onMealPlateLoaded(resource: Resource<MealPlateResponse>?) {
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit_plate_desc.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                Timber.d("PLate","MealPlate Loaded")
                spin_kit_plate_desc.visibility = View.INVISIBLE
                mealPlateViewModel?.thePlate = resource.data?.mealPlate!!
                if(mode == MODE_COPY_FROM_PLATE){
                    til_plate_name.editText?.setText(mealPlateViewModel?.thePlate?.basicInfo?.name?.english+"_Copy"+"_"+LoginUtils.getUserName().substring(0,2))
                    til_plate_desc.editText?.setText(mealPlateViewModel?.thePlate?.basicInfo?.desc)

                } else {
                    til_plate_name.editText?.setText(mealPlateViewModel?.thePlate?.basicInfo?.name?.english)
                    til_plate_desc.editText?.setText(mealPlateViewModel?.thePlate?.basicInfo?.desc)

                    tv_plate_name.setText(mealPlateViewModel?.thePlate?.basicInfo?.name?.english)
                    tv_plate_desc.setText(mealPlateViewModel?.thePlate?.basicInfo?.desc)
                }
                til_plate_approx_cal.editText?.setText(""+mealPlateViewModel?.thePlate?.basicInfo?.calories)
                var prefMeals = mealPlateViewModel?.thePlate?.basicProperty?.prefMeals
                var meals = Meals.values()
                chip_group_plate_pref_meal.removeAllViews()
                meals?.forEach {meal->
                    var chip = Chip(context)
                    chip.text = meal.mealName
                    chip.tag = meal
                    chip.isCheckable = true
                    chip.isEnabled = false
                    chip.isClickable = false
                    prefMeals?.let{
                        if(prefMeals!!.contains(meal.id)){
                            chip.isChecked = true
                        }
                    }

                    chip_group_plate_pref_meal.addView(chip)
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
        recipeRecyclerView.adapter = PlateRecipesAdapter(this, null)

        foodRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        foodRecyclerView.adapter = PlateFoodAdapter(this, null)
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
                updateCalculation()
            }

            Status.EMPTY-> {

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
                spin_kit_content_analysis.visibility = View.GONE
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
                spin_kit_content_analysis.visibility = View.GONE
                tv_calory_deviation.text = resource?.message
                tv_calory_deviation.visibility = View.VISIBLE
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
                updateCalculation()

            }
            Status.EMPTY->{

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

            til_plate_approx_cal.editText?.apply {
                isEnabled = true
                isFocusable = true
            }

            chip_group_plate_pref_meal.apply {
                isEnabled = true
            }
            var count = chip_group_plate_pref_meal.childCount
            for(i in 0..count-1){
                var chip = chip_group_plate_pref_meal.getChildAt(i) as Chip
                chip.isClickable = true
                chip.isEnabled = true
            }

            if(context is MealPlateActivity){
                ll_plate_approx_cal.visibility = View.VISIBLE
                tv_pref_meal_header.visibility = View.VISIBLE
                chip_group_plate_pref_meal.visibility = View.VISIBLE
            }

        } else {
            til_plate_name.visibility = View.GONE
            til_plate_desc.visibility = View.GONE

            tv_plate_name.visibility = View.VISIBLE
            tv_plate_desc.visibility = View.VISIBLE

            b_add_recipe.visibility = View.GONE
            b_add_food.visibility = View.GONE
            til_plate_approx_cal.editText?.apply {
                isEnabled = false
                isFocusable = false
            }
            chip_group_plate_pref_meal.apply {
                isEnabled = false
            }
            var count = chip_group_plate_pref_meal.childCount
            for(i in 0..count-1){
                var chip = chip_group_plate_pref_meal.getChildAt(i) as Chip
                chip.isClickable = false
                chip.isEnabled = false
            }
            ll_plate_approx_cal.visibility = View.GONE


                ll_plate_approx_cal.visibility = View.GONE
                tv_pref_meal_header.visibility = View.GONE
                chip_group_plate_pref_meal.visibility = View.GONE

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

    fun removeFood(lifecycleOwner: LifecycleOwner,food: FoodItem?) {
        //add recipe to this plate
        mealPlateViewModel?.removeFoodViewModel(lifecycleOwner,food!!)
        //notify adapter
        foodRecyclerView?.adapter?.notifyDataSetChanged()
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
        var plate = mealPlateViewModel?.thePlate
        plate?.basicInfo?.name?.english = til_plate_name.editText?.text.toString()
        plate?.basicInfo?.desc = til_plate_desc.editText?.text.toString()

        var count = chip_group_plate_pref_meal?.childCount
        var prefMeals = ArrayList<String>()
        for(i in 0..(count!!-1)){
            var chip = chip_group_plate_pref_meal?.getChildAt(i) as Chip
            if(chip.isChecked){
                prefMeals.add((chip.tag as Meals).id)
            }
        }
        plate?.basicProperty?.prefMeals = prefMeals

        plate?.items = Items()
        plate?.items?.recipies = ArrayList()
        plate?.items?.foods = ArrayList()

        mealPlateViewModel?.liveRecipeViewModelList?.value?.data?.let { for(recipe in it){
            plate?.items?.recipies?.add(recipe?.triggerRecipeItem?.value)
        } }
        mealPlateViewModel?.liveFoodViewModelList?.value?.data?.let { for(food in it){
            plate?.items?.foods?.add(food?.triggerFoodItem?.value)
        } }

        plate?.adminInfo?.createdBy = LoginUtils.getUserCredential()
        plate?.adminInfo?.createdOn = Utils.timeStamp()

        plate?.id = "PLT_"+LoginUtils.getUserName().substring(0,3)+"_"+plate?.adminInfo?.createdOn

        return plate!!
    }

    fun getModifiedPlate(): MealPlate {
        var plate = mealPlateViewModel?.thePlate
        plate?.basicInfo?.name?.english = til_plate_name.editText?.text.toString()
        plate?.basicInfo?.desc = til_plate_desc.editText?.text.toString()

        var count = chip_group_plate_pref_meal?.childCount
        var prefMeals = ArrayList<String>()
        for(i in 0..(count!!-1)){
            var chip = chip_group_plate_pref_meal?.getChildAt(i) as Chip
            if(chip.isChecked){
                prefMeals.add((chip.tag as Meals).id)
            }
        }
        plate?.basicProperty?.prefMeals = prefMeals

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