package com.techticz.app.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.constants.RecipeServings
import com.techticz.app.model.food.Nutrition
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.model.recipe.RecipeResponse
import com.techticz.app.repo.RecipeRepository
import com.techticz.app.ui.adapter.RecipeFoodAdapter
import com.techticz.app.ui.adapter.RecipeStepsAdapter
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
import javax.inject.Inject

class RecipeDetailsActivity : BaseDIActivity(), RecipeStepsAdapter.StepItemCallBacks, RecipeRepository.RecipeRepositoryCallback,NutritionDialogFragment.Listener {



    @Inject
    lateinit var repo: RecipeRepository

    var mode: Int = 0
    private var recipeId: String? = null
    lateinit var recipeViewModel: RecipeViewModel

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

        til_serving_type.editText?.setOnClickListener({onServingTypeClicked()})

        if (mode == MODE_EXPLORE) {
            recipeViewModel?.triggerRecipeItem.value = RecipeItem(recipeId, 1)
            supportActionBar?.title = "Recipe Details"
            fab.visibility = View.GONE
            // if allowed
            configureUIinEditMode(false)
        } else if (mode == MODE_NEW) {
            supportActionBar?.title = "Create Recipe"
            prepareChildrenUI()
            fab.visibility = View.VISIBLE
            configureUIinEditMode(true)
        } else if (mode == MODE_COPY_FROM_RECIPE) {
            recipeViewModel?.triggerRecipeItem.value = RecipeItem(sourceRecipe?.id, 1)
            supportActionBar?.title = "Create Recipe Copy"
            fab.visibility = View.VISIBLE
            configureUIinEditMode(true)
        }



    }

    override fun getNutrition1(): Nutrition {
        var nutrition = Nutrition()
        nutrition.nutrients = recipeViewModel?.getNutrientsPerServe()
        return nutrition
    }

    override fun getNutrition2(): Nutrition {
        var nutrition = Nutrition()
        nutrition.nutrients = recipeViewModel?.getNutrientsPerServe()
        return nutrition
    }
    private fun onNutriInfoClicked() {
        NutritionDialogFragment.newInstance("Recipe Nutrients","Recipe","RDA %").show(supportFragmentManager, "dialog")
    }

    private fun onServingTypeClicked() {
        val servingTypes = listOf(RecipeServings.TABLESPOON.serving,RecipeServings.TEASPOON.serving,
                RecipeServings.BOWL.serving,RecipeServings.GLASS.serving,RecipeServings.PLATE.serving)
        MaterialDialog(this).show{
            listItemsSingleChoice(items = servingTypes){ dialog, index, text ->
                // Invoked when the user selects an item
                (dialog.windowContext as RecipeDetailsActivity).til_serving_type.editText?.setText(text)
                recipeViewModel?.liveRecipeResponse?.value?.data?.recipe?.standardServing?.servingType = text
            }
        }
    }

    private fun onAddFoodClicked() {
        navigator.startBrowseFoodScreen(this)
    }

    private fun onAddStepClicked(){
        steps?.let{
            if(it.size < 35) {
                it?.add("step")
                stepsRecyclerView.adapter?.notifyItemInserted(it.size-1)
            } else {
                Toast.makeText(this,"Maximum limit reached",Toast.LENGTH_SHORT).show()
            }
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
        repo.createRecipe(newRecipe,this)
    }

    private fun prepareNewRecipe(): Recipe {
        var recipe = Recipe()
        recipe?.basicInfo?.name?.english = til_recipe_name.editText?.text.toString()
        recipe?.basicInfo?.desc = til_recipe_desc.editText?.text.toString()
        recipe?.standardServing?.servingType = til_serving_type.editText?.text.toString()

        recipe?.formula?.ingredients = ArrayList()
        recipe?.formula?.steps = steps


        recipeViewModel?.liveFoodViewModelList?.value?.data?.let { for(food in it){
            recipe?.formula?.ingredients?.add(food?.triggerFoodItem?.value)
        } }


        recipe.adminInfo?.createdBy = LoginUtils.getUserCredential()
        recipe.adminInfo?.createdOn = Utils.timeStamp()
        recipe.id = "RCP_"+LoginUtils.getUserName().substring(0,3)+"_"+recipe.adminInfo?.createdOn

        return recipe!!
    }

    private fun saveRecipeChanges() {
        showProgress("Saving recipe changes..")
        var modifiedRecipe = getModifiedRecipe()
        repo.updateRecipe(modifiedRecipe,this)
    }

    fun getModifiedRecipe(): Recipe {
        var recipe = recipeViewModel?.liveRecipeResponse?.value?.data?.recipe
        recipe?.basicInfo?.name?.english = til_recipe_name.editText?.text.toString()
        recipe?.basicInfo?.desc = til_recipe_desc.editText?.text.toString()
        recipe?.standardServing?.servingType = til_serving_type.editText?.text.toString()

        recipe?.formula?.ingredients = ArrayList()
        recipe?.formula?.steps = steps


        recipeViewModel?.liveFoodViewModelList?.value?.data?.let { for(food in it){
            recipe?.formula?.ingredients?.add(food?.triggerFoodItem?.value)
        } }

        //steps are already updated in viewModel
        return recipe!!
    }

    fun validateData(): String? {
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
            til_recipe_name.visibility = View.VISIBLE
            til_recipe_desc.visibility = View.VISIBLE
            til_serving_type.editText?.apply {
                isEnabled = true
                isClickable = true
            }
            tv_recipe_name.visibility = View.GONE
            tv_recipe_desc.visibility = View.GONE

            b_add_food.visibility = View.VISIBLE
            b_add_steps.visibility = View.VISIBLE
        } else {
            fab.setImageResource(R.drawable.ic_mode_edit)
            til_recipe_name.visibility = View.GONE
            til_recipe_desc.visibility = View.GONE
            til_serving_type.editText?.apply {
                isEnabled = false
                isClickable = false
            }
            tv_recipe_name.visibility = View.VISIBLE
            tv_recipe_desc.visibility = View.VISIBLE

            b_add_food.visibility = View.GONE
            b_add_steps.visibility = View.GONE
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

                activityCollapsingToolbar?.title = resource?.data?.recipe?.basicInfo?.name?.english

                //UI binding
                tv_recipe_name.text = resource.data?.recipe?.basicInfo?.name?.english
                tv_recipe_desc.text = resource.data?.recipe?.basicInfo?.desc
                til_serving_type.editText?.setText(resource.data?.recipe?.standardServing?.servingType)

                til_recipe_name.editText?.setText(resource.data?.recipe?.basicInfo?.name?.english)
                til_recipe_desc.editText?.setText(resource.data?.recipe?.basicInfo?.desc)

                tv_recipe_serving.text = ""+resource.data?.recipe?.standardServing?.qty+" "+resource.data?.recipe?.standardServing?.servingType
                resource.data?.recipe?.formula?.steps?.let { steps = it }
                prepareChildrenUI()

                var creater = resource?.data?.recipe?.adminInfo?.createdBy
                var user = LoginUtils.getUserCredential()
                creater?.let {
                    if (creater!!.equals(user, true)) {
                        fab.visibility = View.VISIBLE
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

    private fun onChildViewModelsDataChanged(resource: Resource<List<FoodViewModel>>?) {
        resource?.isFresh = false
        Timber.d("this.recipeViewModel?.liveFoodViewModelList? Data Changed : Status=" + resource?.status + " : Source=" + resource?.dataSource)

        when (resource?.status) {
            Status.LOADING -> {
                spin_kit_recipe_foods.visibility = View.VISIBLE
            }
            Status.COMPLETE -> {
                spin_kit_recipe_foods.visibility = View.INVISIBLE
                spin_kit_content_analysis.visibility = View.INVISIBLE

                tv_content_calories.text = "" + recipeViewModel?.perServingCal() +"\uD83D\uDD25"+
                        " KCAL/" + recipeViewModel?.liveRecipeResponse?.value?.data?.recipe?.standardServing?.servingType
                tv_content_calories.visibility = View.VISIBLE
                when (recipeViewModel?.isVeg()) {
                    true -> tv_veg_nonveg.setTextColor(Color.parseColor("#ff669900"))
                    else -> tv_veg_nonveg.setTextColor(Color.parseColor("#ffcc0000"))
                }

            }
            Status.EMPTY -> {

            }
            Status.ERROR -> {
                spin_kit_recipe_foods.visibility = View.INVISIBLE
                tv_content_calories.text = resource?.message
                tv_content_calories.visibility = View.VISIBLE
            }
        }


    }
    private fun onCreateCopyClicked() {
        Toast.makeText(this, "Create Copy Clicked!", Toast.LENGTH_SHORT).show()
        navigator?.startCopyRecipeScreen(this, recipeViewModel?.liveRecipeResponse?.value?.data?.recipe)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            var foodId = data?.getStringExtra("foodId")
            var foodStdServing = data?.getIntExtra("stdServing",100)
            if(foodStdServing == null) foodStdServing = 100
            var foodItem = FoodItem(foodId,foodStdServing)

            //add recipe to this plate
            recipeViewModel?.addFoodViewModel(this,foodItem)
            //notify adapter
            foodRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    override fun onRemoveStepClicked(position: Int, step: String?) {
        steps?.removeAt(position)
        stepsRecyclerView.adapter?.notifyItemRemoved(position)
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


    var steps: MutableList<String> = ArrayList()
}
