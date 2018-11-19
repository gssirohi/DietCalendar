package com.techticz.app.viewmodel

import androidx.lifecycle.*
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.techticz.app.model.food.Nutrients
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.RecipeResponse
import com.techticz.app.repo.FoodRepository
import com.techticz.app.repo.RecipeRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseViewModel
import com.techticz.app.repo.UserRepository
import com.techticz.dietcalendar.ui.DietCalendarApplication
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class RecipeViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: RecipeRepository
    val triggerRecipeItem = MutableLiveData<RecipeItem>()
    val liveRecipeResponse: LiveData<Resource<RecipeResponse>>
    var liveFoodViewModelList: MediatorLiveData<Resource<List<FoodViewModel>>>? = MediatorLiveData<Resource<List<FoodViewModel>>>()
    var liveImage: MediatorLiveData<Resource<ImageViewModel>>? = MediatorLiveData<Resource<ImageViewModel>>()

    init {
        DietCalendarApplication.getAppComponent().inject(this)
        liveRecipeResponse = Transformations.switchMap(triggerRecipeItem) { triggerLaunch ->

            if (triggerLaunch == null || triggerLaunch.id == null) {
                return@switchMap AbsentLiveData.create<Resource<RecipeResponse>>()
            } else {
                Timber.d("Recipe Trigger detected for:"+triggerLaunch?.id)
                var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.EMPTY, null, "Empty Recipe foods..", DataSource.LOCAL)
                liveFoodViewModelList?.value = foodViewModelListResource

                var liveImageResource = Resource<ImageViewModel>(Status.EMPTY, null, "Empty image recipe..", DataSource.LOCAL)
                liveImage?.value = liveImageResource
                return@switchMap injectedRepo.fetchRecipeResponse(triggerRecipeItem.value)
            }
        }
    }

    fun getNutrients(): Nutrients? {
        var nutrients = Nutrients()

        var foodViewModelList = liveFoodViewModelList?.value?.data
        if(foodViewModelList != null) {
            for (foodViewModel in foodViewModelList!!) {
                if(foodViewModel.liveFoodResponse.value?.data != null) {
                    var foodNutrients: Nutrients? = foodViewModel.getNutrients()
                    var factoredNutrients = foodNutrients?.applyFactor(foodViewModel.triggerFoodItem?.value?.qty!!)
                    nutrients.addUpNutrients(factoredNutrients)
                }
            }
        }
        return nutrients
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
        return isVeg
    }

    fun autoLoadChildren(lifecycleOwner: LifecycleOwner) {
        liveRecipeResponse?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){
                Status.SUCCESS->{
                    //load children view model here
                    observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner)
                }
            }
        })
    }

    private fun observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner: LifecycleOwner) {
        liveFoodViewModelList?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){ Status.EMPTY->loadFoodViewModels(lifecycleOwner)}
        })
        liveImage?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){ Status.EMPTY->loadImageViewModel(lifecycleOwner)}
        })
    }


    private fun loadImageViewModel(lifecycleOwner: LifecycleOwner) {
        var imageViewModel = ImageViewModel(lifecycleOwner as Context)
        imageViewModel.triggerImageUrl.value = liveRecipeResponse?.value?.data?.recipe?.basicInfo?.image
        var imageRes = Resource<ImageViewModel>(Status.SUCCESS,imageViewModel,"Loading recipe image model success..",DataSource.REMOTE)
        liveImage?.value = imageRes
    }

    private fun loadFoodViewModels(lifecycleOwner: LifecycleOwner) {
        var foods = liveRecipeResponse?.value?.data?.recipe?.formula?.ingredients

        var foodViewModelList = ArrayList<FoodViewModel>()

        if (foods != null) {
            for (food in foods) {
                var foodViewModel = FoodViewModel()
                foodViewModel.triggerFoodItem.value = food
                foodViewModel.liveFoodResponse.observe(lifecycleOwner, Observer { resource->when(resource?.status){
                    Status.SUCCESS-> registerFoodChildCompletion();
                } })
                foodViewModelList.add(foodViewModel)
            }
        }
        var foodViewModelListResource = Resource<List<FoodViewModel>>(Status.LOADING, foodViewModelList, "Loading Recipe foods..", DataSource.LOCAL)
        this.liveFoodViewModelList?.value = foodViewModelListResource

    }

    private fun registerFoodChildCompletion() {
        var isAllCompleted = true
        for(child in this.liveFoodViewModelList?.value?.data!!){
            if(child.liveFoodResponse?.value?.status == Status.SUCCESS ||
                    child.liveFoodResponse?.value?.status == Status.ERROR){
                //do nothing
            } else {
                isAllCompleted = false
            }
        }
        if(isAllCompleted){
            var newRes = liveFoodViewModelList?.value?.createCopy(Status.COMPLETE)
            liveFoodViewModelList?.value = newRes
        }
    }


}