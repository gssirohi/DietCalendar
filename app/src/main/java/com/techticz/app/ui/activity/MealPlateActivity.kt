package com.techticz.app.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.constants.Meals
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.model.food.Nutrition
import com.techticz.app.model.meal.Meal
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.repo.MealPlateRepository
import com.techticz.app.ui.customView.PlateView
import com.techticz.app.ui.frag.ImagePickerFragment
import com.techticz.app.ui.frag.NutritionDialogFragment
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status

import kotlinx.android.synthetic.main.activity_meal_plate.*
import kotlinx.android.synthetic.main.create_plate_copy_layout.view.*
import kotlinx.android.synthetic.main.meal_plate_content_layout.view.*
import kotlinx.android.synthetic.main.plate_desc_layout.view.*
import org.parceler.Parcels
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MealPlateActivity : BaseDIActivity(), MealPlateRepository.PlateRepositoryCallback,NutritionDialogFragment.Listener,ImagePickerFragment.Listener {


    @Inject
    lateinit var repo:MealPlateRepository

    private var mode: Int = 0
    private var plateId: String? = null
    private lateinit var plateViewModel: MealPlateViewModel

    companion object{
        var MODE_NEW = 0
        var MODE_EXPLORE = 1
        var MODE_COPY_FROM_PLATE = 2
        private var MODE_EDIT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plate)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout

        initData()
        initUI()
    }

    private var sourcePlate: MealPlate? = null

    private fun initData() {
        plateViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(MealPlateViewModel::class.java)

        mode = intent.getIntExtra("mode",0)
        if(mode == MODE_NEW){
            plateViewModel?.thePlate = MealPlate()
        } else if(mode == MODE_EXPLORE){
            plateId = intent.getStringExtra("plateId")
        } else if(mode == MODE_COPY_FROM_PLATE){
            sourcePlate = Parcels.unwrap<Any>(intent.getParcelableExtra("plate")) as MealPlate
        }

    }
    lateinit var plateView:PlateView

    private fun initUI() {

        if(mode == MODE_EXPLORE) {
            plateViewModel?.triggerMealPlateID.value = Meal(Meals.BREAKFAST, plateId)
            supportActionBar?.title = "Plate Details"
            plateView = PlateView(1,this,PlateView.MODE_EXPLORE,fl_plate_container)

            (fab as View).visibility = View.GONE
            // if allowed
            configureUIinEditMode(false)
        } else if(mode == MODE_NEW){
            supportActionBar?.title = "Create Plate"
            plateView = PlateView(1,this,PlateView.MODE_NEW,fl_plate_container)

            (fab as View).visibility = View.VISIBLE
            configureUIinEditMode(true)
        } else if(mode == MODE_COPY_FROM_PLATE){
            plateViewModel?.triggerMealPlateID.value = Meal(Meals.BREAKFAST, sourcePlate?.id)
            supportActionBar?.title = "Create Plate Copy"
            plateView = PlateView(1,this,PlateView.MODE_COPY_FROM_PLATE,fl_plate_container)

            (fab as View).visibility = View.VISIBLE
            configureUIinEditMode(true)
        }
        plateViewModel?.autoLoadChildren(this)

        plateView.fillDetails(plateViewModel)
        plateView.aiv_meal_plate.visibility = View.GONE
        plateView.ll_meal_content.visibility = View.VISIBLE
        fl_plate_container.addView(plateView)

        plateViewModel?.liveImage?.observe(this, Observer {
            resource->
            onImageModelLoaded(resource)
        })
        plateViewModel?.liveMealPlateResponse?.observe(this, Observer { resource->onPlateLoaded(resource) })

        fab.setOnClickListener { view ->
            onFabClicked()
        }
        fab_nutri.setOnClickListener({
            onNutriInfoClicked()
        })
        view_image_placeholder.setOnClickListener{
            ImagePickerFragment.newInstance().show(supportFragmentManager, "ImagePickerDialog")
        }
    }
    override fun getNutrition1(): Nutrition {
        var nutrition = Nutrition()
        nutrition.nutrients = plateViewModel?.getNutrientsPerPlate()
        return nutrition
    }

    override fun getNutrition2(): Nutrition {
        var nutrition = Nutrition()
        nutrition.nutrients = plateViewModel?.getNutrientsPerPlate()
        return nutrition
    }
    private fun onNutriInfoClicked() {
        NutritionDialogFragment.newInstance("Plate Nutrients","Plate","RDA %").show(supportFragmentManager, "dialog")
    }

    private fun onFabClicked() {
        if(mode == MODE_EXPLORE){
            //switch on EDIT mode
            mode = MODE_EDIT
            plateView?.mode = MODE_EDIT
            configureUIinEditMode(true)

        } else if(mode == MODE_EDIT){
            //save changes
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                savePlateChanges()
            } else {
                showError(result!!)
            }
        } else if(mode == MODE_NEW){
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                createNewPlate()
            } else {
                showError(result!!)
            }
        } else if(mode == MODE_COPY_FROM_PLATE){
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                createNewPlate()
            } else {
                showError(result!!)
            }
        }
    }

    private fun createNewPlate(){
        showProgress("Creating plate..")
        var plate = plateView?.prepareNewPlate()
        aiv_meal_plate_app_bar.viewModel?.pickedBitmap?.let{
            // save picked bitmap image
            var localUri = aiv_meal_plate_app_bar.viewModel.savePickedImage(plate.basicInfo?.name?.english+ Calendar.getInstance().timeInMillis.toString())
            plate.basicInfo?.image = localUri
        }
        repo.createPlate(plate,this)
    }
    private fun savePlateChanges() {
        showProgress("Saving changes..")
        var plate = plateView?.getModifiedPlate()
        aiv_meal_plate_app_bar.viewModel?.pickedBitmap?.let{
            // save picked bitmap image
            var localUri = aiv_meal_plate_app_bar.viewModel.savePickedImage(plate.basicInfo?.name?.english+ Calendar.getInstance().timeInMillis.toString())
            plate.basicInfo?.image = localUri
        }
        repo.updatePlate(plate,this)
    }

    override fun onPlateCreated(plate: MealPlate) {
        hideProgress()
        mode = MODE_EXPLORE
        plateView?.mode = PlateView.MODE_EXPLORE
        plateView?.mealPlateViewModel?.triggerMealPlateID?.value = Meal(Meals.BREAKFAST,plate?.id)
        configureUIinEditMode(false)
        showSuccess("Plate created successfully")
    }

    override fun onCreatePlateFailure(message: String) {
        hideProgress()
        showError(message)
    }
    override fun onPlateUpdated(plate: MealPlate) {
        hideProgress()
        mode = MODE_EXPLORE
        plateView?.mode = PlateView.MODE_EXPLORE
        plateView?.mealPlateViewModel?.triggerMealPlateID?.value = Meal(Meals.BREAKFAST,plate?.id)
        configureUIinEditMode(false)
        showSuccess("Plate updated successfully")
    }

    override fun onPlateUpdateFailure(s:String) {
        hideProgress()
        showError(s)
    }

    private fun validateData(): String? {
        return plateView?.validateData()
    }

    private fun configureUIinEditMode(yes:Boolean) {
        if(yes) {
            fab.setImageResource(R.drawable.ic_check)
            fab.invalidate()
            view_image_placeholder.visibility = View.VISIBLE
            plateView?.configureUIinEditMode(true)
        } else {
            fab.setImageResource(R.drawable.ic_mode_edit)
            fab.invalidate()
            view_image_placeholder.visibility = View.GONE
            plateView?.configureUIinEditMode(false)
        }
    }

    private fun onPlateLoaded(resource: Resource<MealPlateResponse>?) {
        when(resource?.status) {
            Status.SUCCESS -> {
                supportActionBar?.title = resource?.data?.mealPlate?.basicInfo?.name?.english
                activityCollapsingToolbar?.title = resource?.data?.mealPlate?.basicInfo?.name?.english
                var creater = resource?.data?.mealPlate?.adminInfo?.createdBy
                var user = LoginUtils.getUserCredential()
                creater?.let { if(creater!!.equals(user,true)){
                    (fab as View).visibility = View.VISIBLE
                } else{
                    if(mode != MODE_COPY_FROM_PLATE) {
                        var createCopyView = View.inflate(this, R.layout.create_plate_copy_layout, null)
                        fl_plate_container.addView(createCopyView)
                        createCopyView.b_create_copy.setOnClickListener({
                            onCreateCopyClicked()
                        })
                    }
                }
                }
            }

        }
    }

    private fun onCreateCopyClicked() {
        Toast.makeText(this,"Create Copy Clicked!",Toast.LENGTH_SHORT).show()
        navigator?.startCopyPlateScreen(this,plateViewModel?.liveMealPlateResponse?.value?.data?.mealPlate)
    }

    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d("this.mealPlateViewModel?.liveImage? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
                aiv_meal_plate_app_bar.setImageViewModel(resource?.data,this)
            }
            Status.SUCCESS -> {
                aiv_meal_plate_app_bar.setImageViewModel(resource?.data,this)
            }

        }
    }

    fun startBrowsingRecipe() {
        if(mode == MODE_NEW || mode == MODE_COPY_FROM_PLATE) {
            navigator.startBrowseRecipeScreen(this,"new")
        } else {
            navigator.startBrowseRecipeScreen(this,plateId)
        }
    }
    fun startBrowsingFood() {
        if(mode == MODE_NEW || mode == MODE_COPY_FROM_PLATE){
            navigator.startBrowseFoodScreen(this,"new",null)
        } else {
            navigator.startBrowseFoodScreen(this,plateId,null)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            var recipeId = data?.getStringExtra("recipeId")
            var recipeItem = RecipeItem(recipeId,1)

            //add recipe to this plate
            plateView?.mealPlateViewModel?.addRecipeViewModel(this,recipeItem)
             //notify adapter
            plateView?.recipeRecyclerView?.adapter?.notifyDataSetChanged()
        } else if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            var foodId = data?.getStringExtra("foodId")
            var foodPopularServingQty = data?.getIntExtra("popularServingQty",100)
            var foodPopularServingType = data?.getStringExtra("popularServingType")
            if(foodPopularServingQty == null) foodPopularServingQty = 100
            var foodItem = FoodItem(foodId,foodPopularServingQty,foodPopularServingType)

            //add recipe to this plate
            plateView?.mealPlateViewModel?.addFoodViewModel(this,foodItem)
            //notify adapter
            plateView?.foodRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    override fun onBitmapPicked(bitmap: Bitmap) {
        // this bitmap is still not saved: Save it while saving recipe
        if(aiv_meal_plate_app_bar.viewModel == null){
            aiv_meal_plate_app_bar.viewModel = ImageViewModel(this)
        }
        aiv_meal_plate_app_bar.viewModel.pickedBitmap = bitmap
        aiv_meal_plate_app_bar.setImageBitmap(bitmap)
    }
}
