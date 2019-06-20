package com.techticz.app.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.material.chip.Chip
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.constants.Meals
import com.techticz.app.constants.RecipeCategories
import com.techticz.app.constants.RecipeCourses
import com.techticz.app.constants.RecipeServings
import com.techticz.app.model.food.Nutrition
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.model.recipe.RecipeResponse
import com.techticz.app.repo.RecipeRepository
import com.techticz.app.ui.adapter.RecipeFoodAdapter
import com.techticz.app.ui.adapter.RecipeStepsAdapter
import com.techticz.app.ui.frag.ImagePickerFragment
import com.techticz.app.ui.frag.NutritionDialogFragment
import com.techticz.app.util.Utils
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.RecipeViewModel
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status

import kotlinx.android.synthetic.main.activity_recipe_details.*
import kotlinx.android.synthetic.main.content_desc_layout.*
import kotlinx.android.synthetic.main.content_recipe_details.*
import kotlinx.android.synthetic.main.create_recipe_copy_layout.view.*
import org.parceler.Parcels
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RecipeDetailsActivity : BaseDIActivity(), RecipeStepsAdapter.StepItemCallBacks, RecipeRepository.RecipeRepositoryCallback,NutritionDialogFragment.Listener,ImagePickerFragment.Listener {



    @Inject
    lateinit var repo: RecipeRepository

    var mode: Int = 0
    private var recipeId: String? = null
    lateinit var recipeViewModel: RecipeViewModel
    lateinit var theRecipe:Recipe

    companion object {
        var MODE_NEW = 0
        var MODE_EXPLORE = 1
        var MODE_COPY_FROM_RECIPE = 2
        private var MODE_EDIT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout

        initData()
        initUI()
    }

    private var sourceRecipe: Recipe? = null
    private fun initData() {
        recipeViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(RecipeViewModel::class.java)

        mode = intent.getIntExtra("mode", 0)
        if (mode == MODE_NEW) {
            theRecipe = Recipe()
        } else if (mode == MODE_EXPLORE) {
            recipeId = intent.getStringExtra("recipeId")
        } else if (mode == MODE_COPY_FROM_RECIPE) {
            sourceRecipe = Parcels.unwrap<Any>(intent.getParcelableExtra("recipe")) as Recipe
        }

    }

    fun removeFood(lifecycleOwner: LifecycleOwner,food: FoodItem?) {
        //add recipe to this plate
        recipeViewModel?.removeFoodViewModel(lifecycleOwner,food!!)
        //notify adapter
        foodRecyclerView?.adapter?.notifyDataSetChanged()
    }
    private fun initUI() {
        recipeViewModel?.autoLoadChildren(this)

        recipeViewModel?.liveImage?.observe(this, Observer { resource ->
            onImageModelLoaded(resource)
        })
        recipeViewModel?.liveRecipeResponse?.observe(this, Observer { resource -> onRecipeLoaded(resource) })

        fab.setOnClickListener { view ->
            onFabClicked()
        }
        fab_nutri.setOnClickListener({
            onNutriInfoClicked()
        })
        b_add_food.setOnClickListener {
            onAddFoodClicked()
        }
        b_add_steps.setOnClickListener {
            onAddStepClicked()
        }
        view_image_placeholder.setOnClickListener{
            ImagePickerFragment.newInstance().show(supportFragmentManager, "ImagePickerDialog")
        }

        var meals = Meals.values()
        meals?.forEach {
            var chip = Chip(this)
            chip.text = it.mealName
            chip.tag = it
            chip.isCheckable = true
            chip.isClickable = true
            chip_group_pref_meal.addView(chip)
        }

        til_serving_type.editText?.setOnClickListener({onServingTypeClicked()})
        til_recipe_category.editText?.setOnClickListener({onRecipeCategoryClicked()})
        til_course.editText?.setOnClickListener({onRecipeCourseClicked()})
        til_recipe_serving_qty.editText?.setOnClickListener({onServingQuantityClicked()})
        til_approx_cal.editText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(!TextUtils.isEmpty(s)) {
                    try {
                        var approxCal = s.toString().toInt()
                        theRecipe.basicInfo?.perServingCalories = approxCal
                    } catch (e:Exception){
                        theRecipe.basicInfo?.perServingCalories = 0
                    }
                }
                updateCalculation()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        if (mode == MODE_EXPLORE) {
            recipeViewModel?.triggerRecipeItem.value = RecipeItem(recipeId, 1)
            supportActionBar?.title = "Recipe Details"
            (fab as View).visibility = View.GONE
            // if allowed
            configureUIinEditMode(false)
        } else if (mode == MODE_NEW) {
            supportActionBar?.title = "Create Recipe"
            prepareChildrenUI()
            (fab as View).visibility = View.VISIBLE
            configureUIinEditMode(true)
        } else if (mode == MODE_COPY_FROM_RECIPE) {
            recipeViewModel?.triggerRecipeItem.value = RecipeItem(sourceRecipe?.id, 1)
            supportActionBar?.title = "Create Recipe Copy"
            (fab as View).visibility = View.VISIBLE
            configureUIinEditMode(true)
        }



    }

    override fun getNutrition1(): Nutrition {
        var nutrition = Nutrition()
        nutrition.nutrients = recipeViewModel?.getNutrientsPerServe(theRecipe.standardServing?.qty!!)
        return nutrition
    }

    override fun getNutrition2(): Nutrition {
        var nutrition = Nutrition()
        nutrition.nutrients = recipeViewModel?.getNutrientsPerServe(theRecipe.standardServing?.qty!!)
        return nutrition
    }
    private fun onNutriInfoClicked() {
        NutritionDialogFragment.newInstance("Recipe Nutrients","Recipe","RDA %").show(supportFragmentManager, "dialog")
    }

    private fun onServingTypeClicked() {

        val recipeServings = RecipeServings.values()
        var servingTypes = ArrayList<String>()
        recipeServings?.forEach {
            servingTypes.add(it.serving)
        }

        MaterialDialog(this).show{
            listItemsSingleChoice(items = servingTypes){ dialog, index, text ->
                // Invoked when the user selects an item
                theRecipe.standardServing?.servingType = text
                updateCalculation()

            }
        }
    }

    private fun updateCalculation() {
        til_serving_type.editText?.setText(theRecipe.standardServing?.servingType)
        til_recipe_serving_qty.editText?.setText(""+theRecipe.standardServing?.qty!!)

        var perServeCalory = recipeViewModel?.perServingCal(theRecipe.standardServing?.qty!!)
        var approxCalories = theRecipe.basicInfo?.perServingCalories
        var servingUnit:String = ""
        if(TextUtils.isEmpty(theRecipe.standardServing?.servingType)){
            servingUnit = "Serving"
        } else {
            servingUnit = theRecipe.standardServing?.servingType!!
        }

        til_approx_cal.hint = "Approx. Cal/"+servingUnit
        tv_content_serving_qty.text = ""+theRecipe.standardServing?.qty
        tv_content_serving_unit.text = servingUnit
        tv_content_calories.text = ""+perServeCalory
        tv_content_calories_serving_text.text = "Cal/"+servingUnit

        when (recipeViewModel?.isVeg()) {
            true -> {
                tv_veg_nonveg.setTextColor(Color.parseColor("#ff669900"))
                tv_content_type_text.setText("Veg. Recipe")
            }
            else -> {
                tv_veg_nonveg.setTextColor(Color.parseColor("#ffcc0000"))
                tv_content_type_text.setText("NonVeg. Recipe")
            }
        }

        if(approxCalories != null && perServeCalory != null && approxCalories>0 && perServeCalory>0) {
            var percentDeviation = (perServeCalory!! / approxCalories!!)*100
            if(percentDeviation < 90){
                tv_calory_deviation.text = "Recipe contains only "+percentDeviation.toInt()+"% of declared approx. calories. You should add more items or increase quantity."
                tv_calory_deviation.visibility = View.VISIBLE
            } else if(percentDeviation > 110){
                tv_calory_deviation.text = "Recipe contains more than "+percentDeviation.toInt()+"% of declared approx. calories.  You should remove items or reduce quantity."
                tv_calory_deviation.visibility = View.VISIBLE
            } else {
                tv_calory_deviation.visibility = View.GONE
            }
        }
    }

    private fun onRecipeCategoryClicked() {

        val recipeCategories = RecipeCategories.values()
        var categories = ArrayList<String>()
        recipeCategories?.forEach {
            categories.add(it.category)
        }

        MaterialDialog(this).show{
            listItemsSingleChoice(items = categories){ dialog, index, text ->
                // Invoked when the user selects an item
                (dialog.windowContext as RecipeDetailsActivity).til_recipe_category.editText?.setText(text)
                theRecipe.basicInfo?.category = text
            }
        }
    }
    private fun onRecipeCourseClicked() {

        val recipeCourses = RecipeCourses.values()
        var courses = ArrayList<String>()
        recipeCourses?.forEach {
            courses.add(it.course)
        }

        MaterialDialog(this).show{
            listItemsSingleChoice(items = courses){ dialog, index, text ->
                // Invoked when the user selects an item
                (dialog.windowContext as RecipeDetailsActivity).til_course.editText?.setText(text)
                theRecipe.basicProperty?.course = text
            }
        }
    }
    private fun onServingQuantityClicked() {
        val servingTypes = listOf("1","2","3","4","5","6","7","8","9","10")
        MaterialDialog(this).show{
            listItemsSingleChoice(items = servingTypes){ dialog, index, text ->
                // Invoked when the user selects an item
                theRecipe.standardServing?.qty = text.toInt()
                updateCalculation()

            }
        }
    }

    private fun onAddFoodClicked() {
        if(mode == MODE_NEW || mode == MODE_COPY_FROM_RECIPE) {
            navigator.startBrowseFoodScreen(this,null,"new")
        } else {
            navigator.startBrowseFoodScreen(this,null,recipeId)
        }
    }

    private fun onAddStepClicked(){

            if(theRecipe?.formula?.steps!!.size < 35) {
                theRecipe?.formula?.steps?.add("step")
                stepsRecyclerView.adapter?.notifyDataSetChanged(/*theRecipe?.formula?.steps!!.size-1*/)
            } else {
                Toast.makeText(this,"Maximum limit reached",Toast.LENGTH_SHORT).show()
            }

    }
    private fun onFabClicked() {
        if(mode == MODE_EXPLORE){
            //switch on EDIT mode
            mode = MODE_EDIT
            configureUIinEditMode(true)

        } else if(mode == MODE_EDIT){
            //save changes
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                saveRecipeChanges()
            } else {
                showError(result!!)
            }
        } else if(mode == MODE_NEW){
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                createNewRecipe()
            } else {
                showError(result!!)
            }
        } else if(mode == MODE_COPY_FROM_RECIPE){
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                createNewRecipe()
            } else {
                showError(result!!)
            }
        }
    }
    private fun prepareChildrenUI() {
        observeChildViewModels()
        foodRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        foodRecyclerView.adapter = RecipeFoodAdapter(this, null)

        stepsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        stepsRecyclerView.adapter = RecipeStepsAdapter(this,this)
    }
    private fun createNewRecipe(){
        showProgress("Creating recipe..")
        var newRecipe = prepareNewRecipe()
        aiv_recipe_app_bar.viewModel?.pickedBitmap?.let{
            // save picked bitmap image
            var localUri = aiv_recipe_app_bar.viewModel.savePickedImage(newRecipe.basicInfo?.name?.english+Calendar.getInstance().timeInMillis.toString())
            newRecipe.basicInfo?.image = localUri
        }
        repo.createRecipe(newRecipe,this)
    }

    private fun prepareNewRecipe(): Recipe {

        theRecipe?.basicInfo?.name?.english = til_recipe_name.editText?.text.toString()
        theRecipe?.basicInfo?.desc = til_recipe_desc.editText?.text.toString()
        theRecipe?.standardServing?.servingType = til_serving_type.editText?.text.toString()

        var count = chip_group_pref_meal?.childCount
        var prefMeals = ArrayList<String>()
        for(i in 0..(count!!-1)){
            var chip = chip_group_pref_meal?.getChildAt(i) as Chip
            if(chip.isChecked){
                prefMeals.add((chip.tag as Meals).id)
            }
        }
        theRecipe.basicProperty?.prefMeals = prefMeals


        theRecipe?.formula?.ingredients = ArrayList()

        recipeViewModel?.liveFoodViewModelList?.value?.data?.let { for(food in it){
            theRecipe?.formula?.ingredients?.add(food?.triggerFoodItem?.value)
        } }


        theRecipe.adminInfo?.createdBy = LoginUtils.getUserCredential()
        theRecipe.adminInfo?.createdOn = Utils.timeStamp()
        theRecipe.id = "RCP_"+LoginUtils.getUserName().substring(0,3)+"_"+theRecipe.adminInfo?.createdOn

        return theRecipe!!
    }

    private fun saveRecipeChanges() {
        showProgress("Saving recipe changes..")
        var modifiedRecipe = getModifiedRecipe()
        aiv_recipe_app_bar.viewModel?.pickedBitmap?.let{
            // save picked bitmap image
            var localUri = aiv_recipe_app_bar.viewModel.savePickedImage(modifiedRecipe.basicInfo?.name?.english+Calendar.getInstance().timeInMillis.toString())
            modifiedRecipe.basicInfo?.image = localUri
        }

        repo.updateRecipe(modifiedRecipe,this)
    }

    fun getModifiedRecipe(): Recipe {
        theRecipe?.basicInfo?.name?.english = til_recipe_name.editText?.text.toString()
        theRecipe?.basicInfo?.desc = til_recipe_desc.editText?.text.toString()
        theRecipe?.standardServing?.servingType = til_serving_type.editText?.text.toString()

        var count = chip_group_pref_meal?.childCount
        var prefMeals = ArrayList<String>()
        for(i in 0..(count!!-1)){
            var chip = chip_group_pref_meal?.getChildAt(i) as Chip
            if(chip.isChecked){
                prefMeals.add((chip.tag as Meals).id)
            }
        }
        theRecipe.basicProperty?.prefMeals = prefMeals

        theRecipe?.formula?.ingredients = ArrayList()

        recipeViewModel?.liveFoodViewModelList?.value?.data?.let { for(food in it){
            theRecipe?.formula?.ingredients?.add(food?.triggerFoodItem?.value)
        } }

        //steps are already updated in viewModel
        return theRecipe!!
    }

    fun validateData(): String? {
        if(TextUtils.isEmpty(aiv_recipe_app_bar.viewModel.triggerImageUrl?.value)
            && aiv_recipe_app_bar.viewModel?.getBitmap() == null){
            return "please choose image for recipe"
        }
        if(TextUtils.isEmpty(til_recipe_name.editText?.text.toString())){
            return "please enter recipe name"
        }
        if(TextUtils.isEmpty(til_recipe_desc.editText?.text.toString())){
            return "please enter recipe description"
        }
        if(TextUtils.isEmpty(til_serving_type.editText?.text.toString())){
            return "Please choose serving type for recipe"
        }
        if(!recipeViewModel?.hasItems()!!){
            return "Please add ingredients to your recipe"
        }
        if((stepsRecyclerView?.adapter as RecipeStepsAdapter).itemCount < 1){
            return "Please add steps to prepare recipe"
        }
        return null;
    }
    private fun configureUIinEditMode(yes:Boolean) {
        if(yes) {
            fab.setImageResource(R.drawable.ic_check)
            fab.invalidate()
            til_recipe_name.visibility = View.VISIBLE
            til_recipe_desc.visibility = View.VISIBLE
            til_serving_type.editText?.apply {
                isEnabled = true
                isClickable = true
            }
            til_recipe_serving_qty.editText?.apply {
                isEnabled = true
                isClickable = true
            }
            til_recipe_category.editText?.apply {
                isEnabled = true
                isClickable = true
            }
            til_course.editText?.apply {
                isEnabled = true
                isClickable = true
            }
            til_approx_cal.editText?.apply {
                isEnabled = true
            }
            chip_group_pref_meal.apply {
                isEnabled = true
            }
            var count = chip_group_pref_meal.childCount
            for(i in 0..count-1){
                var chip = chip_group_pref_meal.getChildAt(i) as Chip
                chip.isClickable = true
                chip.isEnabled = true
            }

            tv_recipe_name.visibility = View.GONE
            tv_recipe_desc.visibility = View.GONE

            b_add_food.visibility = View.VISIBLE
            b_add_steps.visibility = View.VISIBLE
            view_image_placeholder.visibility = View.VISIBLE

            ll_recipe_category_approx_cal.visibility = View.VISIBLE
        } else {
            fab.setImageResource(R.drawable.ic_mode_edit)
            til_recipe_name.visibility = View.GONE
            til_recipe_desc.visibility = View.GONE

            til_serving_type.editText?.apply {
                isEnabled = false
                isClickable = false
            }
            til_recipe_serving_qty.editText?.apply {
                isEnabled = false
                isClickable = false
            }
            til_recipe_category.editText?.apply {
                isEnabled = false
                isClickable = false
            }
            til_course.editText?.apply {
                isEnabled = false
                isClickable = false
            }
            til_approx_cal.editText?.apply {
                isEnabled = false
            }
            chip_group_pref_meal.apply {
                isEnabled = false
            }
            var count = chip_group_pref_meal.childCount
            for(i in 0..count-1){
                var chip = chip_group_pref_meal.getChildAt(i) as Chip
                chip.isClickable = false
                chip.isEnabled = false
            }

            tv_recipe_name.visibility = View.VISIBLE
            tv_recipe_desc.visibility = View.VISIBLE

            b_add_food.visibility = View.GONE
            b_add_steps.visibility = View.GONE
            view_image_placeholder.visibility = View.GONE
            ll_recipe_category_approx_cal.visibility = View.GONE
        }
        foodRecyclerView.adapter?.notifyDataSetChanged()
        stepsRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun onRecipeLoaded(resource: Resource<RecipeResponse>?) {
        when (resource?.status) {
            Status.LOADING -> {
                spin_kit_recipe_desc.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                spin_kit_recipe_desc.visibility = View.INVISIBLE
                theRecipe = resource?.data?.recipe!!
                activityCollapsingToolbar?.title =theRecipe.basicInfo?.name?.english

                //UI binding
                tv_recipe_name.text = theRecipe.basicInfo?.name?.english
                tv_recipe_desc.text = theRecipe.basicInfo?.desc

                til_recipe_name.editText?.setText(theRecipe.basicInfo?.name?.english)
                til_recipe_desc.editText?.setText(theRecipe.basicInfo?.desc)


                til_approx_cal.editText?.setText(""+theRecipe.basicInfo?.perServingCalories)
                updateCalculation()
                til_recipe_category.editText?.setText(""+theRecipe.basicInfo?.category)
                til_course.editText?.setText(""+theRecipe.basicProperty?.course)

                var prefMeals = theRecipe.basicProperty?.prefMeals
                var meals = Meals.values()
                chip_group_pref_meal.removeAllViews()
                meals?.forEach {meal->
                    var chip = Chip(this)
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

                    chip_group_pref_meal.addView(chip)
                }

                if(theRecipe?.formula?.steps == null){
                    theRecipe?.formula?.steps = ArrayList<String>()
                }
                prepareChildrenUI()

                var creater =theRecipe.adminInfo?.createdBy
                var user = LoginUtils.getUserCredential()
                creater?.let {
                    if (creater!!.equals(user, true)) {
                        (fab as View).visibility = View.VISIBLE
                    } else {
                        if (mode != MODE_COPY_FROM_RECIPE) {
                            var createCopyView = View.inflate(this, R.layout.create_recipe_copy_layout, null)
                            ll_recipe_content.addView(createCopyView)
                            createCopyView.b_create_copy.setOnClickListener({
                                onCreateCopyClicked()
                            })
                        }
                    }
                }
                if(creater == null){
                    if (mode != MODE_COPY_FROM_RECIPE) {
                        var createCopyView = View.inflate(this, R.layout.create_recipe_copy_layout, null)
                        ll_recipe_content.addView(createCopyView)
                        createCopyView.b_create_copy.setOnClickListener({
                            onCreateCopyClicked()
                        })
                    }
                }
            }

        }
    }

    private fun observeChildViewModels() {
        this.recipeViewModel?.liveFoodViewModelList?.observe(this as BaseDIActivity, Observer { resource ->

            onChildViewModelsDataChanged(resource)
        })

        this.recipeViewModel?.liveImage?.observe(this as BaseDIActivity, Observer { resource ->
            onImageModelLoaded(resource)
        })
    }

    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d("this.recipeViewModel?.liveImage? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
               // spin_kit.visibility = View.INVISIBLE
                aiv_recipe_app_bar.setImageViewModel(resource?.data, this as LifecycleOwner)

            }
            Status.SUCCESS -> {
               // spin_kit.visibility = View.INVISIBLE
                aiv_recipe_app_bar.setImageViewModel(resource?.data, this as LifecycleOwner)
            }
            Status.EMPTY -> {
                /* var imageViewModel = ImageViewModel(context)
                 imageViewModel.triggerImageUrl.value = recipeViewModel?.liveRecipeResponse?.value?.data?.recipe?.basicInfo?.image
                 var imageRes = Resource<ImageViewModel>(Status.SUCCESS,imageViewModel,"Loading recipe image model success..",DataSource.REMOTE)
                 this.recipeViewModel?.liveImage?.value = imageRes*/
            }
            Status.ERROR -> {
              //  spin_kit.visibility = View.INVISIBLE
                tv_content_calories.text = resource?.message
                tv_content_calories.visibility = View.VISIBLE
            }
        }
    }

    override fun onBitmapPicked(bitmap: Bitmap) {
        // this bitmap is still not saved: Save it while saving recipe
        if(aiv_recipe_app_bar.viewModel == null){
            aiv_recipe_app_bar.viewModel = ImageViewModel(this)
        }
        aiv_recipe_app_bar.viewModel.pickedBitmap = bitmap
        aiv_recipe_app_bar.setImageBitmap(bitmap)
    }

    private fun onChildViewModelsDataChanged(resource: Resource<List<FoodViewModel>>?) {
        resource?.isFresh = false
        Timber.d("this.recipeViewModel?.liveFoodViewModelList? Data Changed : Status=" + resource?.status + " : Source=" + resource?.dataSource)

        when (resource?.status) {
            Status.LOADING -> {
                spin_kit_recipe_foods.visibility = View.VISIBLE
            }
            Status.COMPLETE -> {
                spin_kit_recipe_foods.visibility = View.INVISIBLE
                spin_kit_content_analysis.visibility = View.GONE

                updateCalculation()

            }
            Status.EMPTY -> {

            }
            Status.ERROR -> {
                spin_kit_recipe_foods.visibility = View.INVISIBLE
                tv_calory_deviation.text = resource?.message
                tv_calory_deviation.visibility = View.VISIBLE
            }
        }


    }
    private fun onCreateCopyClicked() {
        Toast.makeText(this, "Create Copy Clicked!", Toast.LENGTH_SHORT).show()
        navigator?.startCopyRecipeScreen(this, theRecipe)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            var foodId = data?.getStringExtra("foodId")
            var foodPopularServingQty = data?.getIntExtra("popularServingQty",100)
            var foodPopularServingType = data?.getStringExtra("popularServingType")
            if(foodPopularServingQty == null) foodPopularServingQty = 100
            var foodItem = FoodItem(foodId,foodPopularServingQty,foodPopularServingType)

            //add recipe to this plate
            recipeViewModel?.addFoodViewModel(this,foodItem)
            //notify adapter
            foodRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    override fun onRemoveStepClicked(position: Int, step: String?) {
        theRecipe?.formula?.steps?.removeAt(position)
        stepsRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onRecipeUpdated(recipe: Recipe) {
        hideProgress()
        mode = MODE_EXPLORE
        recipeViewModel?.triggerRecipeItem?.value = RecipeItem(recipeId,1)
        configureUIinEditMode(false)
        showSuccess("Recipe updated successfully")
    }

    override fun onRecipeUpdateFailure(message: String) {
        hideProgress()
        showError(message)
    }

    override fun onRecipeCreated(recipe: Recipe) {
        hideProgress()
        mode = MODE_EXPLORE
        recipeViewModel?.triggerRecipeItem?.value = RecipeItem(recipeId,1)
        configureUIinEditMode(false)
        showSuccess("Recipe created successfully")
    }

    override fun onCreateRecipeFailure(message: String) {
        hideProgress()
        showError(message)
    }


}
