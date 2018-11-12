package com.techticz.app.viewmodel

import android.arch.lifecycle.*
import android.content.Context
import com.techticz.app.constants.FoodCategories
import com.techticz.app.model.FoodResponse
import com.techticz.app.model.food.Nutrients
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.app.repo.FoodRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseViewModel
import com.techticz.app.repo.MealPlateRepository
import com.techticz.dietcalendar.ui.DietCalendarApplication
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class FoodViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: FoodRepository

    val triggerFoodItem = MutableLiveData<FoodItem>()
    val liveFoodResponse: LiveData<Resource<FoodResponse>>
    var liveImage: MediatorLiveData<Resource<ImageViewModel>>? = MediatorLiveData<Resource<ImageViewModel>>()
    init {
        DietCalendarApplication.getAppComponent().inject(this)
        liveFoodResponse = Transformations.switchMap(triggerFoodItem) { triggerLaunch ->
            if (triggerLaunch == null) {
                return@switchMap AbsentLiveData.create<Resource<FoodResponse>>()
            } else {
                Timber.d("Food Trigger detected for:"+triggerLaunch?.id)
                var liveImageResource = Resource<ImageViewModel>(Status.EMPTY, null, "Empty image food..", DataSource.LOCAL)
                liveImage?.value = liveImageResource
                return@switchMap injectedRepo.fetchFoodResponse(triggerFoodItem.value)
            }
        }
    }

    fun getNutrients(): Nutrients? {
        var nutrients = liveFoodResponse?.value?.data?.food?.nutrition?.nutrients
        return nutrients
    }

    fun isVeg(): Boolean {
        if(liveFoodResponse?.value?.status == Status.SUCCESS) {
            var cat = liveFoodResponse?.value?.data?.food?.basicInfo?.category
            if (cat.equals(FoodCategories.EGG.id, true)
                    || cat.equals(FoodCategories.MEAT.id, true)) {
                return false;
            }
            return true;
        }
        return true;
    }

    fun autoLoadChildren(lifecycleOwner: LifecycleOwner) {
        liveFoodResponse?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){
                Status.SUCCESS->{
                    //load children view model here
                    observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner)
                }
            }
        })
    }

    private fun observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner: LifecycleOwner) {
        liveImage?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){ Status.EMPTY->loadImageViewModel(lifecycleOwner)}
        })
    }

    private fun loadImageViewModel(lifecycleOwner: LifecycleOwner) {
        var imageViewModel = ImageViewModel(lifecycleOwner as Context)
        imageViewModel.triggerImageUrl.value = liveFoodResponse?.value?.data?.food?.basicInfo?.image
        var imageRes = Resource<ImageViewModel>(Status.SUCCESS,imageViewModel,"Loading food image model success..",DataSource.REMOTE)
        liveImage?.value = imageRes
    }


}