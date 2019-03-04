package com.techticz.app.viewmodel

import androidx.lifecycle.*
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.model.food.Nutrients
import com.techticz.app.model.meal.Meal
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.app.base.BaseViewModel
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.app.model.mealplate.RecipeItem
import timber.log.Timber
import javax.inject.Inject
import com.techticz.app.repo.MealPlateRepository
import com.techticz.app.util.Utils
import com.techticz.dietcalendar.ui.DietCalendarApplication
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Status

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class MealPlateViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: MealPlateRepository

    val triggerMealPlateID = MutableLiveData<Meal>()
    val liveStatus = MediatorLiveData<Status>()
    val liveMealPlateResponse: LiveData<Resource<MealPlateResponse>>
    var liveRecipeViewModelList:MediatorLiveData<Resource<List<RecipeViewModel>>>? = MediatorLiveData<Resource<List<RecipeViewModel>>>()
    var liveFoodViewModelList: MediatorLiveData<Resource<List<FoodViewModel>>>? = MediatorLiveData<Resource<List<FoodViewModel>>>()
    var liveImage: MediatorLiveData<Resource<ImageViewModel>>? = MediatorLiveData<Resource<ImageViewModel>>()
    init {
        DietCalendarApplication.getAppComponent().inject(this)
        liveMealPlateResponse = Transformations.switchMap(triggerMealPlateID) { triggerMeal ->

            if (triggerMeal == null ) {
                return@switchMap AbsentLiveData.create<Resource<MealPlateResponse>>()
            } else {
                liveStatus.value = Status.LOADING
                Timber.d("MealPlate Trigger detected for:"+triggerMeal?.mealPlateId)
                var recipeViewModelListResource = Resource<List<RecipeViewModel>>(Status.EMPTY, null, "Empty Meal recipes..", DataSource.LOCAL)
                liveRecipeViewModelList?.value = recipeViewModelListResource

                var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.EMPTY, null, "Empty Meal foods..", DataSource.LOCAL)
                liveFoodViewModelList?.value = foodViewModelListResource

                var liveImageResource = Resource<ImageViewModel>(Status.EMPTY, null, "Empty image meal..", DataSource.LOCAL)
                liveImage?.value = liveImageResource

                return@switchMap injectedRepo.fetchMealPlateResponse(triggerMealPlateID.value)
            }
        }
    }
    fun perPlateCal(): Float? {
        getNutrientsPerPlate()?.principlesAndDietaryFibers?.energy?.let {
            return Utils.calories(it)
        }
        return 0f
    }
    fun getNutrientsPerPlate(): Nutrients? {
        var nutrientsFood = Nutrients()

        var foodViewModelList = liveFoodViewModelList?.value?.data
        if(foodViewModelList != null) {
            for (foodViewModel in foodViewModelList!!) {
                if(foodViewModel.liveFoodResponse.value?.data != null) {
                    var foodNutrients: Nutrients? = foodViewModel.getNutrientPerPortion()
                    var factoredNutrients = foodNutrients?.applyFactor(foodViewModel.triggerFoodItem?.value?.qty!!.toFloat())
                    nutrientsFood.addUpNutrients(factoredNutrients)
                }
            }
        }

        var nutrientsRecipe = Nutrients()

        var recipeViewModelList = liveRecipeViewModelList?.value?.data
        if(recipeViewModelList != null) {
            for (recipeViewModel in recipeViewModelList!!) {
                if(recipeViewModel.liveRecipeResponse.value?.data != null) {
                    var recipeNutrients: Nutrients? = recipeViewModel.getNutrientsPerServe()
                    var factoredNutrients = recipeNutrients?.applyFactor(recipeViewModel.triggerRecipeItem?.value?.qty!!.toFloat())
                    nutrientsRecipe.addUpNutrients(factoredNutrients)
                }
            }
        }
        nutrientsRecipe.addUpNutrients(nutrientsFood)
        return nutrientsRecipe
    }

    fun isVeg(): Boolean {
        var isVeg = true

        var foodViewModelList = liveFoodViewModelList?.value?.data
        if(foodViewModelList != null) {
            for (foodViewModel in foodViewModelList!!) {
                if(foodViewModel.liveFoodResponse.value?.data != null) {
                    var isVeg = foodViewModel.isVeg()
                    if(!isVeg) return false;
                }
            }
        }

        var recipeViewModelList = liveRecipeViewModelList?.value?.data
        if(recipeViewModelList != null) {
            for (recipeViewModel in recipeViewModelList!!) {
                if(recipeViewModel.liveRecipeResponse.value?.data != null) {
                    var isVeg = recipeViewModel.isVeg()
                    if(!isVeg) return false;
                }
            }
        }
        return isVeg
    }

    fun autoLoadChildren(lifecycleOwner: LifecycleOwner){
        Timber.d("observing meal Plate....:"+triggerMealPlateID?.value?.mealPlateId )
        liveMealPlateResponse?.observe(lifecycleOwner, Observer { resource->
           // updateParent()
            when(resource?.status){
                Status.SUCCESS->{
                    //load children view model here
                   liveStatus.value = Status.SUCCESS
                   observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner)
                }
                Status.ERROR->{
                    liveStatus.value = Status.COMPLETE
                }
            }
        })

    }

    private fun observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner: LifecycleOwner) {
        liveRecipeViewModelList?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){
                Status.EMPTY->{
                    var recipes = liveMealPlateResponse?.value?.data?.mealPlate?.items?.recipies
                    if(recipes == null || recipes.isEmpty()){
                        registerRecipeChildCompletion()
                    } else {
                        loadRecipeViewModels(lifecycleOwner, recipes)
                    }
                }
                Status.COMPLETE->{
                    Log.d("STATUS","Plate All Recipes Completed")
                    registerAllChildCompletion()
                }
            }
        })
        liveFoodViewModelList?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){
                Status.EMPTY->{
                    var foods = liveMealPlateResponse?.value?.data?.mealPlate?.items?.foods
                    if(foods == null || foods.isEmpty()){
                        registerFoodChildCompletion()
                    } else {
                        loadFoodViewModels(lifecycleOwner,foods)
                    }
                }
                Status.COMPLETE->{
                    Log.d("STATUS","Plate All Foods Completed")
                    registerAllChildCompletion()
                }
            }
        })
        liveImage?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){ Status.EMPTY->loadImageViewModel(lifecycleOwner)}
        })
    }

    private fun loadImageViewModel(lifecycleOwner: LifecycleOwner) {
        var imageViewModel = ImageViewModel(lifecycleOwner as Context)
        imageViewModel.triggerImageUrl.value = liveMealPlateResponse?.value?.data?.mealPlate?.basicInfo?.image
        var imageRes = Resource<ImageViewModel>(Status.LOADING,imageViewModel,"Loading meal image model ..",DataSource.REMOTE)
        liveImage?.value = imageRes
    }

    private fun loadFoodViewModels(lifecycleOwner: LifecycleOwner,foods:List<FoodItem>) {

        var foodViewModelList = ArrayList<FoodViewModel>()

        if (foods != null) {
            for (food in foods) {
                var foodViewModel = FoodViewModel()
                foodViewModel.autoLoadChildren(lifecycleOwner)
                foodViewModel.triggerFoodItem.value = food
                foodViewModel.liveStatus.observe(lifecycleOwner, Observer { resource->when(resource){
                    Status.COMPLETE-> {
                        Log.d("STATUS","Meal Food completed:"+food.id)
                        registerFoodChildCompletion()
                    }
                } })
                foodViewModelList.add(foodViewModel)
            }
        }
        var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.LOADING, foodViewModelList, "Loading Meal foods..", DataSource.LOCAL)
        liveFoodViewModelList?.value = foodViewModelListResource

    }

    fun loadRecipeViewModels(lifecycleOwner: LifecycleOwner, recipes: MutableList<RecipeItem>) {

        var recipeViewModelList = ArrayList<RecipeViewModel>()

        if (recipes != null) {
            for (recipe in recipes) {
                var recipeViewModel = RecipeViewModel()
                recipeViewModel.triggerRecipeItem.value = recipe
                recipeViewModel.autoLoadChildren(lifecycleOwner)
                recipeViewModel.liveStatus?.observe(lifecycleOwner, Observer { resource->when(resource){
                    Status.COMPLETE-> {
                        Log.d("STATUS","Meal Recipe completed:"+recipe.id)
                        registerRecipeChildCompletion()
                    }
                } })
                recipeViewModelList.add(recipeViewModel)
            }
        }
        var recipeViewModelListResource = Resource<List<RecipeViewModel>>(Status.LOADING, recipeViewModelList, "Loading Meal recipes..", DataSource.LOCAL)
        liveRecipeViewModelList?.value = recipeViewModelListResource

    }

    fun removeRecipeViewModel(lifecycleOwner: LifecycleOwner,recipeItem:RecipeItem){
        var res = liveRecipeViewModelList?.value
        var newList = ArrayList<RecipeViewModel>()
        res?.data?.let {
            for(recipe in it){
                if(recipe.triggerRecipeItem?.value?.id!!.equals(recipeItem.id)){

                } else {
                    newList.add(recipe)
                }
            }
        }

        var recipeViewModelListResource = Resource<List<RecipeViewModel>>(Status.COMPLETE, newList, "Remove recipe..", DataSource.LOCAL)
        liveRecipeViewModelList?.value= recipeViewModelListResource
    }

    fun removeFoodViewModel(lifecycleOwner: LifecycleOwner,foodItem:FoodItem){
        var res = liveFoodViewModelList?.value
        var newList = ArrayList<FoodViewModel>()
        res?.data?.let {
            for(food in it){
                if(food.triggerFoodItem?.value?.id!!.equals(foodItem.id)){

                } else {
                    newList.add(food)
                }
            }
        }

        var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.COMPLETE, newList, "Remove food..", DataSource.LOCAL)
        liveFoodViewModelList?.value= foodViewModelListResource
    }

    fun addRecipeViewModel(lifecycleOwner: LifecycleOwner,recipeItem:RecipeItem){
        var res = liveRecipeViewModelList?.value
        var newList = ArrayList<RecipeViewModel>()
        res?.data?.let { newList.addAll(it) }
        var vm = RecipeViewModel()
        vm.autoLoadChildren(lifecycleOwner)
        vm.triggerRecipeItem.value = recipeItem
        vm.liveStatus?.observe(lifecycleOwner, Observer { resource->when(resource){
            Status.COMPLETE-> registerRecipeChildCompletion()
        } })
        newList.add(vm)

        var recipeViewModelListResource = Resource<List<RecipeViewModel>>(Status.LOADING, newList, "Added recipe..", DataSource.LOCAL)
        liveRecipeViewModelList?.value= recipeViewModelListResource
    }

    fun addFoodViewModel(lifecycleOwner: LifecycleOwner,foodItem: FoodItem){
        var res = liveFoodViewModelList?.value
        var newList = ArrayList<FoodViewModel>()
        res?.data?.let { newList.addAll(it) }
        var vm = FoodViewModel()
        vm.autoLoadChildren(lifecycleOwner)
        vm.triggerFoodItem.value = foodItem
        vm.liveStatus.observe(lifecycleOwner, Observer { resource->when(resource){
            Status.SUCCESS-> registerFoodChildCompletion();
            Status.COMPLETE->registerFoodChildCompletion();
        } })
        newList.add(vm)

        var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.LOADING, newList, "Added food..", DataSource.LOCAL)
        liveFoodViewModelList?.value= foodViewModelListResource
    }

    fun registerRecipeChildCompletion() {
        var isAllCompleted = true

        this.liveRecipeViewModelList?.value?.data?.let {
            for (child in it) {
                if (child.liveStatus.value == Status.COMPLETE) {
                    //do nothing
                } else {
                    isAllCompleted = false
                }
            }
        }

        if(isAllCompleted){
            var newRes = liveRecipeViewModelList?.value?.createCopy(Status.COMPLETE)
            liveRecipeViewModelList?.value = newRes
        }
    }
    fun registerFoodChildCompletion() {
        var isAllCompleted = true
        this.liveFoodViewModelList?.value?.data?.let {
            for (child in it) {
                if (child.liveStatus.value == Status.COMPLETE) {
                    //do nothing
                } else {
                    isAllCompleted = false
                }
            }
        }

        if(isAllCompleted){
            var newRes = liveFoodViewModelList?.value?.createCopy(Status.COMPLETE)
            liveFoodViewModelList?.value = newRes
        }

    }
    fun registerAllChildCompletion() {
        var isAllCompleted = true
        if(this.liveFoodViewModelList?.value?.status == Status.COMPLETE
        && this.liveRecipeViewModelList?.value?.status == Status.COMPLETE){

        } else {
            isAllCompleted = false
        }


        if(isAllCompleted){
           liveStatus.value = Status.COMPLETE
        }
    }

    fun hasItems(): Boolean {
        liveRecipeViewModelList?.value?.data?.let { if(it.size >= 1) return true }
        liveFoodViewModelList?.value?.data?.let { if(it.size >= 1) return true }
        return false
    }

    fun getFruitContentPerPlate(): Float {
        var perPlateFruit = 0f

        var foodViewModelList = liveFoodViewModelList?.value?.data
        if(foodViewModelList != null) {
            for (foodViewModel in foodViewModelList!!) {
                if(foodViewModel.liveFoodResponse.value?.data != null) {
                    var fruit: Float = foodViewModel.getFruitPerPortion()
                    var factoredFruit = fruit * foodViewModel.triggerFoodItem?.value?.qty!!.toFloat()
                    perPlateFruit = perPlateFruit + factoredFruit
                }
            }
        }


        var recipeViewModelList = liveRecipeViewModelList?.value?.data
        if(recipeViewModelList != null) {
            for (recipeViewModel in recipeViewModelList!!) {
                if(recipeViewModel.liveRecipeResponse.value?.data != null) {
                    var fruit: Float = recipeViewModel.getFruitPerServe()
                    var factoredFruits = fruit * recipeViewModel.triggerRecipeItem?.value?.qty!!.toFloat()
                    perPlateFruit = perPlateFruit + factoredFruits
                }
            }
        }

        return perPlateFruit
    }
    fun getVeggieContentPerPlate(): Float {
        var perPlateVeggie = 0f

        var foodViewModelList = liveFoodViewModelList?.value?.data
        if(foodViewModelList != null) {
            for (foodViewModel in foodViewModelList!!) {
                if(foodViewModel.liveFoodResponse.value?.data != null) {
                    var veggie: Float = foodViewModel.getVeggiesPerPortion()
                    var factoredVeggie = veggie * foodViewModel.triggerFoodItem?.value?.qty!!.toFloat()
                    perPlateVeggie = perPlateVeggie + factoredVeggie
                }
            }
        }


        var recipeViewModelList = liveRecipeViewModelList?.value?.data
        if(recipeViewModelList != null) {
            for (recipeViewModel in recipeViewModelList!!) {
                if(recipeViewModel.liveRecipeResponse.value?.data != null) {
                    var veggie: Float = recipeViewModel.getVeggiesPerServe()
                    var factoredVeggies = veggie * recipeViewModel.triggerRecipeItem?.value?.qty!!.toFloat()
                    perPlateVeggie = perPlateVeggie + factoredVeggies
                }
            }
        }

        return perPlateVeggie
    }
}