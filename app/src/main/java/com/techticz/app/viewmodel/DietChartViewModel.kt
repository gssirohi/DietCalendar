package com.techticz.app.viewmodel

import androidx.lifecycle.*
import android.content.Context
import com.techticz.app.constants.Meals
import com.techticz.app.model.DietPlanResponse
import com.techticz.app.model.dietplan.DayPlan
import com.techticz.app.model.food.Nutrients
import com.techticz.app.model.meal.Meal
import com.techticz.app.repo.DietPlanRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseViewModel
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.ui.DietCalendarApplication
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class DietChartViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: DietPlanRepository

    val triggerFetchDietPlan = MutableLiveData<String>()
    val liveDietPlanResponse: LiveData<Resource<DietPlanResponse>>

    var liveImage: MediatorLiveData<Resource<ImageViewModel>>? = MediatorLiveData<Resource<ImageViewModel>>()

    var liveMondayViewModelList: MediatorLiveData<Resource<List<MealPlateViewModel>>>? = MediatorLiveData<Resource<List<MealPlateViewModel>>>()
    var liveTuesdayViewModelList: MediatorLiveData<Resource<List<MealPlateViewModel>>>? = MediatorLiveData<Resource<List<MealPlateViewModel>>>()
    var liveWednesdayViewModelList: MediatorLiveData<Resource<List<MealPlateViewModel>>>? = MediatorLiveData<Resource<List<MealPlateViewModel>>>()
    var liveThursdayViewModelList: MediatorLiveData<Resource<List<MealPlateViewModel>>>? = MediatorLiveData<Resource<List<MealPlateViewModel>>>()
    var liveFridayViewModelList: MediatorLiveData<Resource<List<MealPlateViewModel>>>? = MediatorLiveData<Resource<List<MealPlateViewModel>>>()
    var liveSaturdayViewModelList: MediatorLiveData<Resource<List<MealPlateViewModel>>>? = MediatorLiveData<Resource<List<MealPlateViewModel>>>()
    var liveSundayViewModelList: MediatorLiveData<Resource<List<MealPlateViewModel>>>? = MediatorLiveData<Resource<List<MealPlateViewModel>>>()
    /*private var mondayMealViewModels:List<MealPlateViewModel>? = null
    private var tuesdayMealViewModels:List<MealPlateViewModel>? = null
    private var wednesdayMealViewModels:List<MealPlateViewModel>? = null
    private var thursdayMealViewModels:List<MealPlateViewModel>? = null
    private var fridayMealViewModels:List<MealPlateViewModel>? = null
    private var saturdayMealViewModels:List<MealPlateViewModel>? = null
    private var sundayMealViewModels:List<MealPlateViewModel>? = null
*/
    init {
        DietCalendarApplication.getAppComponent().inject(this)
        liveDietPlanResponse = Transformations.switchMap(triggerFetchDietPlan) { dietPlanTrigger ->
            Timber.d("DietChart Trigger received for:"+dietPlanTrigger)
            if (dietPlanTrigger == null) {
                return@switchMap AbsentLiveData.create<Resource<DietPlanResponse>>()
            } else {
                liveImage?.value = Resource<ImageViewModel>(Status.EMPTY, null, "Empty image diet plan..", DataSource.LOCAL)

                liveMondayViewModelList?.value = Resource<List<MealPlateViewModel>>(Status.EMPTY, null, "Empty Day meals..", DataSource.LOCAL)
                liveTuesdayViewModelList?.value = Resource<List<MealPlateViewModel>>(Status.EMPTY, null, "Empty Day meals..", DataSource.LOCAL)
                liveWednesdayViewModelList?.value = Resource<List<MealPlateViewModel>>(Status.EMPTY, null, "Empty Day meals..", DataSource.LOCAL)
                liveThursdayViewModelList?.value = Resource<List<MealPlateViewModel>>(Status.EMPTY, null, "Empty Day meals..", DataSource.LOCAL)
                liveFridayViewModelList?.value = Resource<List<MealPlateViewModel>>(Status.EMPTY, null, "Empty Day meals..", DataSource.LOCAL)
                liveSaturdayViewModelList?.value = Resource<List<MealPlateViewModel>>(Status.EMPTY, null, "Empty Day meals..", DataSource.LOCAL)
                liveSundayViewModelList?.value = Resource<List<MealPlateViewModel>>(Status.EMPTY, null, "Empty Day meals..", DataSource.LOCAL)
                return@switchMap injectedRepo.fetchDietPlan(dietPlanTrigger)
            }
        }

    }

    fun autoLoadChildren(lifecycleOwner: LifecycleOwner,days:List<Int>){
        liveImage?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){ Status.EMPTY->loadImageViewModel(lifecycleOwner)}
        })
        liveDietPlanResponse?.observe(lifecycleOwner, Observer { resource->
            // updateParent()
            when(resource?.status){
                Status.LOADING->{
                    Timber.d("loading diet plan..")
                }
                Status.SUCCESS->{
                    Timber.d("Diet Plan Loaded:"+resource?.data?.dietPlan?.id)
                    //load children view model here
                    for(day in days) {
                        observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner, day)
                    }
                }
            }
        })

    }
    private fun loadImageViewModel(lifecycleOwner: LifecycleOwner) {
        var imageViewModel = ImageViewModel(lifecycleOwner as Context)
        imageViewModel.triggerImageUrl.value = liveDietPlanResponse?.value?.data?.dietPlan?.basicInfo?.image
        var imageRes = Resource<ImageViewModel>(Status.LOADING,imageViewModel,"Loading diet plan image model ..",DataSource.REMOTE)
        liveImage?.value = imageRes
    }
    private fun observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner: LifecycleOwner,day:Int) {
        Timber.d("Observing dayMealsModels.... :"+day)
        getDayMealViewModels(day)?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){ Status.EMPTY->{
                Timber.d(" dayMealsModels are EMPTY .... :"+day)
                loadDayViewModels(lifecycleOwner,day)
            }}
        })
    }

    private fun loadDayViewModels(lifecycleOwner: LifecycleOwner, day: Int) {
        Timber.d(" loading dayMealModels .... :"+day)
        var list = createDayMealViewModels(lifecycleOwner,day)
        var liveDayModel = getDayMealViewModels(day)
        var recipeViewModelListResource = Resource<List<MealPlateViewModel>>(Status.LOADING, list, "Loading Day meals..", DataSource.LOCAL)
        liveDayModel?.value = recipeViewModelListResource
    }

    public fun getDayMealViewModels(sectionNumber:Int): MediatorLiveData<Resource<List<MealPlateViewModel>>>? {
        when(sectionNumber){
            1->{
                return liveMondayViewModelList;
            }
            2->{
                return liveTuesdayViewModelList
            }
            3->{
                return liveWednesdayViewModelList
            }
            4->{
                return liveThursdayViewModelList
            }
            5->{
                return liveFridayViewModelList
            }
            6->{
               return liveSaturdayViewModelList
            }
            7->{
               return liveSundayViewModelList
            }
        }
        return null
    }

    private fun createDayMealViewModels(lifecycleOwner: LifecycleOwner,sectionNumber:Int): List<MealPlateViewModel> {
        var dayPlan: DayPlan? = DayPlan()
        when(sectionNumber){
            1->{
                dayPlan = liveDietPlanResponse?.value?.data?.dietPlan?.calendar?.monday
            }
            2->{
                dayPlan = liveDietPlanResponse?.value?.data?.dietPlan?.calendar?.tuesday
            }
            3->{
                dayPlan = liveDietPlanResponse?.value?.data?.dietPlan?.calendar?.wednesday
            }
            4->{
                dayPlan = liveDietPlanResponse?.value?.data?.dietPlan?.calendar?.thursday
            }
            5->{
                dayPlan = liveDietPlanResponse?.value?.data?.dietPlan?.calendar?.friday
            }
            6->{
                dayPlan = liveDietPlanResponse?.value?.data?.dietPlan?.calendar?.saturday
            }
            7->{
                dayPlan = liveDietPlanResponse?.value?.data?.dietPlan?.calendar?.sunday
            }
        }
        var meals :ArrayList<Meal> = ArrayList<Meal>()
        meals.add(Meal(Meals.EARLY_MORNING,dayPlan?.earlyMorning))
        meals.add(Meal(Meals.BREAKFAST,dayPlan?.breakfast))
        meals.add(Meal(Meals.LUNCH,dayPlan?.lunch))
        meals.add(Meal(Meals.BRUNCH,dayPlan?.brunch))
        meals.add(Meal(Meals.DINNER,dayPlan?.dinner))
        meals.add(Meal(Meals.BED_TIME,dayPlan?.bedTime))

        var viewModelList = ArrayList<MealPlateViewModel>()
        for(meal in meals){
            var mealPlateViewModel = MealPlateViewModel()
            mealPlateViewModel.triggerMealPlateID.value = meal
            mealPlateViewModel.autoLoadChildren(lifecycleOwner)
            mealPlateViewModel.liveFoodViewModelList?.observe(lifecycleOwner, Observer { resource->when(resource?.status){
                Status.COMPLETE-> registerChildCompletion(sectionNumber);
            } })
            mealPlateViewModel.liveRecipeViewModelList?.observe(lifecycleOwner, Observer { resource->when(resource?.status){
                Status.COMPLETE-> registerChildCompletion(sectionNumber);
            } })
            viewModelList.add(mealPlateViewModel)
        }

        return viewModelList
    }

    fun getDayMealNutrients(day:Int): Nutrients? {
        var nutrientsDayMeals = Nutrients()

        var mealModelList = getDayMealViewModels(day)
        if(mealModelList != null) {
            for (mealModel in mealModelList?.value?.data!!) {
                if(mealModel.liveMealPlateResponse.value?.data != null) {
                    var mealNutrients: Nutrients? = mealModel.getNutrientsPerPlate()
                    var factoredNutrients = mealNutrients?.applyFactor(1f)//as meal is one always
                    nutrientsDayMeals.addUpNutrients(factoredNutrients)
                }
            }
        }
        return nutrientsDayMeals
    }

    private fun registerChildCompletion(section: Int) {
        var isAllCompleted = true
        var liveModelList = getDayMealViewModels(section)
        for(child in liveModelList?.value?.data!!){
            if(child.liveFoodViewModelList?.value?.status == Status.COMPLETE){
                //do nothing
            } else {
                isAllCompleted = false
            }
        }

/*
        if(isAllCompleted){
            var newRes = liveModelList?.value?.createCopy(Status.COMPLETE)
            liveModelList?.value = newRes
        }

        isAllCompleted = true

*/

        for(child in liveModelList?.value?.data!!){
            if(child.liveRecipeViewModelList?.value?.status == Status.COMPLETE){
                //do nothing
            } else {
                isAllCompleted = false
            }
        }

        if(isAllCompleted){
            var newRes = liveModelList?.value?.createCopy(Status.COMPLETE)
            liveModelList?.value = newRes
        }
    }

    fun isMyPlan(): Boolean {
        var creator = liveDietPlanResponse?.value?.data?.dietPlan?.adminInfo?.createdBy
        return LoginUtils.getUserCredential().equals(creator,true)
    }

    fun addMealInDietPlan(daySection: Int?, mealType: String?, plateId: String?, listner: DietPlanRepository.DietPlanCallBack) {
        var dietPlan = liveDietPlanResponse?.value?.data?.dietPlan

        var dayPlan = dietPlan?.calendar?.monday
        when(daySection){
            1-> dayPlan = dietPlan?.calendar?.monday
            2->dayPlan = dietPlan?.calendar?.tuesday
            3->dayPlan = dietPlan?.calendar?.wednesday
            4->dayPlan = dietPlan?.calendar?.thursday
            5->dayPlan = dietPlan?.calendar?.friday
            6->dayPlan = dietPlan?.calendar?.saturday
            7->dayPlan = dietPlan?.calendar?.sunday
        }

        when(mealType){
            Meals.EARLY_MORNING.id-> dayPlan?.earlyMorning = plateId
            Meals.BREAKFAST.id-> dayPlan?.breakfast = plateId
            Meals.LUNCH.id-> dayPlan?.lunch = plateId
            Meals.BRUNCH.id-> dayPlan?.brunch = plateId
            Meals.DINNER.id-> dayPlan?.dinner = plateId
            Meals.BED_TIME.id-> dayPlan?.bedTime = plateId
        }

        injectedRepo.updatePlan(dietPlan,listner)

    }
}