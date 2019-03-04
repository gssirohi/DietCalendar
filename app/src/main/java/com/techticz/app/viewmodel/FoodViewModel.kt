package com.techticz.app.viewmodel

import androidx.lifecycle.*
import android.content.Context
import android.text.Html
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
import com.techticz.app.util.Utils
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
    val liveStatus = MediatorLiveData<Status>()
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
                liveStatus.value = Status.LOADING
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
            if (cat.equals(FoodCategories.M.id, true)
                    || cat.equals(FoodCategories.N.id, true)
                    || cat.equals(FoodCategories.O.id, true)
                    || cat.equals(FoodCategories.P.id, true)
                    || cat.equals(FoodCategories.Q.id, true)
                    || cat.equals(FoodCategories.R.id, true)
                    || cat.equals(FoodCategories.S.id, true)) {
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
                    liveStatus.value = Status.COMPLETE
                    observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner)
                }
                Status.ERROR->{
                    liveStatus.value = Status.COMPLETE
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

    fun getCaloriesPerStdServing(): Float? {
        return Utils.calories(getNutrientPerStdServing()?.principlesAndDietaryFibers?.energy!!)
    }

    fun getNutrientPerStdServing(): Nutrients? {
        var stdPortion = liveFoodResponse?.value?.data?.food?.standardServing?.portion
        return getNutrientPerPortion()?.applyFactor(stdPortion!!.toFloat())
    }

    fun getNutrientPerPortion(): Nutrients? {
        var nutriFactPortion = liveFoodResponse?.value?.data?.food?.nutrition?.portion
        if(nutriFactPortion == null) nutriFactPortion = 100
        var perPortionFactor = (1/nutriFactPortion!!.toFloat())
        return getNutrients()?.applyFactor(perPortionFactor)
    }


    fun perServingCalText(): CharSequence {
        return ""+getCaloriesPerStdServing()+"\uD83D\uDD25"+" KCAL"
    }
    fun perServingCalPerUnitText(): CharSequence {
        return "per "+liveFoodResponse?.value?.data?.food?.standardServing?.portion+" "+liveFoodResponse?.value?.data?.food?.standardServing?.servingUnit
    }

    fun getFruitPerPortion(): Float {
        var nutriFactPortion = liveFoodResponse?.value?.data?.food?.nutrition?.portion
        if(nutriFactPortion == null) nutriFactPortion = 100
        var perPortionFactor = (1/nutriFactPortion!!.toFloat())
        liveFoodResponse?.value?.data?.food?.basicInfo?.category?.let {
            if(it.equals("E")){
                return 1f
            }
        }
        return 0f
    }

    fun getVeggiesPerPortion(): Float {
        var nutriFactPortion = liveFoodResponse?.value?.data?.food?.nutrition?.portion
        if(nutriFactPortion == null) nutriFactPortion = 100
        var perPortionFactor = (1/nutriFactPortion!!.toFloat())
        liveFoodResponse?.value?.data?.food?.basicInfo?.category?.let {
            if(it.equals("D") || it.equals("C")){
                return 1f // 1 portion = 1 gram
            }
        }
        return 0f
    }

}