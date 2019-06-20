package com.techticz.app.ui.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CompoundButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.DietPlanResponse
import com.techticz.app.model.dietplan.Calendar
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.repo.DietPlanRepository
import com.techticz.app.repo.UserRepository
import com.techticz.app.ui.adapter.TypicalPlatesAdapter
import com.techticz.app.ui.customView.AppImageView
import com.techticz.app.ui.customView.CaloryDistributionView
import com.techticz.app.ui.event.FreshLoadDashboard
import com.techticz.app.ui.event.FreshLoadPlans
import com.techticz.app.ui.frag.ImagePickerFragment
import com.techticz.app.ui.frag.ShareWordsFragment
import com.techticz.app.util.Utils
import com.techticz.app.viewmodel.DietChartViewModel
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.activity_diet_plan.*
import kotlinx.android.synthetic.main.content_diet_plan.*
import org.greenrobot.eventbus.EventBus
import org.parceler.Parcels
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DietPlanActivity : BaseDIActivity(), DietPlanRepository.DietPlanCallBack, UserRepository.UserProfileCallback,ImagePickerFragment.Listener,ShareWordsFragment.Listener {


    @Inject
    lateinit var repo:DietPlanRepository

    lateinit var dietPlanViewModel: DietChartViewModel
    lateinit var thePlan:DietPlan
    private var mode: Int = 0
    private var sourceDietPlan: DietPlan? = null
    lateinit var planId:String
    companion object{
        var MODE_CREATE_NEW = 0
        var MODE_COPY_FROM_PLAN = 1
        var MODE_EXPLORE = 2
        var MODE_EDIT = 3
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_plan)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout
        fab.setOnClickListener { view ->
            onFabClicked()
        }
        initData()
        initUI()
    }

    private fun initData() {
        dietPlanViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(DietChartViewModel::class.java)
        mode = intent.getIntExtra("mode",0)
        if(mode == MODE_CREATE_NEW){
            thePlan = DietPlan()
        } else if(mode == MODE_COPY_FROM_PLAN){
            sourceDietPlan = Parcels.unwrap<Any>(intent.getParcelableExtra("plan")) as DietPlan
        } else if(mode == MODE_EXPLORE){
            thePlan = Parcels.unwrap<Any>(intent.getParcelableExtra("plan")) as DietPlan
            dietPlanViewModel?.triggerFetchDietPlan.value = thePlan?.id
        }

    }
    private fun initUI() {
        dietPlanViewModel?.liveImage?.observe(this,androidx.lifecycle.Observer { res->onImageModelLoaded(res) })
        dietPlanViewModel?.liveDietPlanResponse?.observe(this,androidx.lifecycle.Observer { res->onDietPlanLoaded(res) })

        if(mode == MODE_CREATE_NEW){
            cb_source_plan_plates.visibility = GONE
            til_source_plan_name.visibility = GONE
            tv_or.visibility = GONE
            card_locked_plan.visibility = View.GONE
            configureUIInEditMode(true)
        } else if(mode == MODE_EDIT){
            card_locked_plan.visibility = View.GONE
            cb_source_plan_plates.visibility = GONE
            til_source_plan_name.visibility = GONE
            tv_or.visibility = GONE
            configureUIInEditMode(true)
        } else if(mode == MODE_COPY_FROM_PLAN){
            card_locked_plan.visibility = View.GONE
            tv_or.visibility = VISIBLE
            cb_source_plan_plates.visibility = VISIBLE
            til_source_plan_name.visibility = VISIBLE
            til_source_plan_name.editText?.setText(sourceDietPlan?.basicInfo?.name)
            cb_source_plan_plates.isChecked = true
            configureUIInEditMode(true)
        } else if(mode == MODE_EXPLORE){//explore
            card_locked_plan.visibility = View.VISIBLE
            cb_source_plan_plates.visibility = GONE
            til_source_plan_name.visibility = GONE
            tv_or.visibility = GONE
            configureUIInEditMode(false)
        }
        view_image_placeholder.setOnClickListener{
            ImagePickerFragment.newInstance().show(supportFragmentManager, "ImagePickerDialog")
        }
        val sourcPlanCheckBoxListner = CompoundButton.OnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if(b){
                cb_random_plates.isChecked = false
            }
        }
        val randomPlanCheckBoxListner = CompoundButton.OnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if(b){
                cb_source_plan_plates.isChecked = false
            }
        }
        cdv.setCalDistribution(thePlan.caloryDistribution)
        cb_source_plan_plates.setOnCheckedChangeListener(sourcPlanCheckBoxListner)
        cb_random_plates.setOnCheckedChangeListener(randomPlanCheckBoxListner)
        b_activate_plan.setOnClickListener { activatePlan() }
        b_unlock.setOnClickListener { onUnlockClicked() }
    }

    private var isActivatingPlan = false

    private fun activatePlan() {
        showProgress()
        var user  = baseuserViewModel.liveUserResponse.value?.data?.user
        user?.activePlan  = thePlan?.id
        isActivatingPlan = true
        baseuserViewModel.updateUser(user,this)
    }

    private fun onUnlockClicked() {
        ShareWordsFragment.newInstance().show(supportFragmentManager, "share-word")
    }

    override fun onRewardClaimed() {
        showProgress("Granting reward..")
        var user = baseuserViewModel?.liveUserResponse?.value?.data?.user
        user?.saveKeyForPlanLock(thePlan?.id)
        baseuserViewModel?.updateUser(user,this)
    }

    override fun rewardProductView(): View? {
        var itemView = View.inflate(this,R.layout.meal_plan_list_item_view, null)
        var planImage = itemView.findViewById(R.id.aiv_plan_image) as AppImageView
        var planName = itemView.findViewById(R.id.plan_name) as TextView
        var planDesc = itemView.findViewById(R.id.plan_desc) as TextView
        var planType = itemView.findViewById(R.id.plan_type) as TextView
        var planCalory = itemView.findViewById(R.id.tv_daily_cal) as TextView
        var planAuthor = itemView.findViewById(R.id.tv_plan_author) as TextView
        var bExplore = itemView.findViewById(R.id.b_explore) as FloatingActionButton

        planImage.setImageViewModel(dietPlanViewModel?.liveImage?.value?.data,this)
        planName.setText(thePlan?.basicInfo?.name)
        planDesc.setText(thePlan?.basicInfo?.desc)
        if (thePlan.getBasicInfo().getType().equals("veg", ignoreCase = true)) {
            planType.setTextColor(Color.parseColor("#ff669900"))
        } else if (thePlan.getBasicInfo().getType().equals("non-veg", ignoreCase = true)) {
            planType.setTextColor(Color.parseColor("#ffcc0000"))
        }
        if (thePlan.getBasicInfo().getDailyCalories() > 0) {
            planCalory.setText(""+thePlan.getBasicInfo().getDailyCalories()!! + " Daily Calories")
        } else {
            planCalory.setVisibility(View.GONE)
        }
        planAuthor.visibility = View.GONE
        (bExplore as View).visibility = View.GONE
        return itemView
    }

    override fun couldNotSpreadWord() {
        showError("Could not share words!!")
    }

    private fun updatePlanAccessebility() {
        var isLocked = true
        var creater = thePlan.adminInfo?.createdBy
        var user = LoginUtils.getUserCredential()
        creater?.let {
            if (it.equals(user, true)) {
                (fab as View).visibility = View.VISIBLE
                planIsMyPlan()
                isLocked = false
            } else {
                (fab as View).visibility = View.GONE
                if (thePlan?.adminInfo?.hasLock != null && thePlan?.adminInfo?.hasLock!!) {
                    if (baseuserViewModel?.liveUserResponse?.value?.data?.user?.hasKeyForPlanLock(thePlan?.id)!!) {
                        unlockPlan()
                        isLocked = false
                    } else {
                        lockPlan()
                    }
                } else {
                    planWithoutLock()
                    isLocked = false
                }
                //check if it is unlocked

            }
        }
        var activePlan = baseuserViewModel?.liveUserResponse?.value?.data?.user?.activePlan
        if(activePlan.equals(thePlan?.id)){
            b_activate_plan.visibility = View.GONE
            b_diet_chart.visibility = View.VISIBLE
            toolbar?.subtitle = "Activated"
            shape_plan_active_lable_header.visibility = View.VISIBLE
            shape_plan_active_lable.visibility = View.VISIBLE
        } else {
            shape_plan_active_lable_header.visibility = View.GONE
            shape_plan_active_lable.visibility = View.GONE
            if(isLocked){
                b_activate_plan.visibility = View.GONE
            } else {
                b_activate_plan.visibility = View.VISIBLE
            }
            b_diet_chart.visibility = View.GONE
            toolbar?.subtitle = ""
        }
    }

    private fun planWithoutLock() {
        iv_locked_plan.setImageResource(R.drawable.free)
        tv_locked_plan_title.text = "Complimentry Plan!"
        tv_locked_plan_desc.text = "This plan is complimentry from Dietist Team."
        b_unlock.visibility = View.GONE
    }

    private fun planIsMyPlan() {
        iv_locked_plan.setImageResource(R.drawable.your_plan)
        tv_locked_plan_title.text = "Your Plan"
        tv_locked_plan_desc.text = "This plan is created by you."
        b_unlock.visibility = View.GONE
    }

    private fun unlockPlan() {
        iv_locked_plan.setImageResource(R.drawable.reward)
        tv_locked_plan_title.text = "Plan Unlocked!"
        tv_locked_plan_desc.text = "This plan is unlocked and can be activated now."
        b_unlock.visibility = View.GONE
    }

    private fun lockPlan(){
        iv_locked_plan.setImageResource(R.drawable.lock)
        tv_locked_plan_title.text = "Plan Locked!"
        tv_locked_plan_desc.text = "This plan is locked. You can unlock it by clicking on below button."
        b_unlock.visibility = View.VISIBLE
    }

    private fun onDietPlanLoaded(resource: Resource<DietPlanResponse>?) {
        when (resource?.status) {
            Status.LOADING -> {
                showProgress("Loading Plan..")
            }
            Status.SUCCESS -> {
                hideProgress()
                thePlan = resource?.data?.dietPlan!!
//                dietPlanViewModel?.loadImageViewModel(this)
                activityCollapsingToolbar?.title =thePlan.basicInfo?.name

                til_plan_name.editText?.setText(thePlan.basicInfo?.name)
                til_plan_desc.editText?.setText(thePlan.basicInfo?.desc)

                thePlan.basicInfo?.dailyCalories?.let{
                    til_diet_plan_daily_approx_cal.editText?.setText(""+it)
                }

                thePlan.basicInfo?.type?.let{
                    if(it.equals("veg")){
                        chip_vegiterion.apply { isChecked = true }
                    } else if(it.equals("non-veg")){
                     chip_non_vegiterion.apply{isChecked = true}
                    } else{

                    }
                }
                updatePlanAccessebility()

                dietPlanViewModel.autoLoadChildren(this, listOf(1))
                dietPlanViewModel.getDayMealViewModels(1)?.observe(this,androidx.lifecycle.Observer {
                    res->
                    onDayMealsViewModelLoaded(res)
                })
            }
            Status.ERROR->{
                hideProgress()
                showError(resource?.message!!)
            }
        }
    }


private fun onDayMealsViewModelLoaded(res: Resource<List<MealPlateViewModel>>?) {
    when(res?.status){
        Status.COMPLETE->{
            if(res?.data != null && !res?.data!!.isEmpty()) {
                ll_typical_paltes.visibility = View.VISIBLE
                recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = TypicalPlatesAdapter(this, res?.data!!)
            } else {
                ll_typical_paltes.visibility = View.GONE
            }
        }
    }
}
    private fun onImageModelLoaded(resource: Resource<ImageViewModel>?) {
        Timber.d("this.dietPlan?.liveImage? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        resource?.isFresh = false
        when(resource?.status) {
            Status.LOADING -> {
                //showProgress("Loading image..")
                aiv_plan.setImageViewModel(resource?.data, this as LifecycleOwner)
            }
            Status.SUCCESS -> {
                //hideProgress()
                aiv_plan.setImageViewModel(resource?.data, this as LifecycleOwner)
            }
            Status.EMPTY -> {

            }
            Status.ERROR -> {
                hideProgress()
                showError(resource?.message!!)
            }
        }
    }


    private fun onFabClicked() {
        if(mode == MODE_EXPLORE){
            //switch on EDIT mode
            mode = MODE_EDIT
            configureUIInEditMode(true)

        } else if(mode == MODE_EDIT){
            //save changes
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                savePlanChanges()
            } else {
                showError(result!!)
            }
        } else if(mode == MODE_CREATE_NEW){
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                createPlan()
            } else {
                showError(result!!)
            }
        } else if(mode == MODE_COPY_FROM_PLAN){
            var result = validateData()
            if(TextUtils.isEmpty(result)){
                createPlan()
            } else {
                showError(result!!)
            }
        }
    }

    private fun configureUIInEditMode(b: Boolean) {
        if(b){
            fab.setImageResource(R.drawable.ic_check)
            fab.refreshDrawableState()
            til_plan_name.editText?.apply {
                isEnabled = true
                isClickable = true
                isFocusable = true
            }
            til_plan_desc.editText?.apply {
                isEnabled = true
                isClickable = true
                isFocusable = true
            }
            til_diet_plan_daily_approx_cal.editText?.apply {
                isEnabled = true
                isClickable = true
                isFocusable = true
            }
            var count = chip_group_plan_type.childCount
            for(i in 0..count-1){
                var chip = chip_group_plan_type.getChildAt(i) as Chip
                chip.isClickable = true
                chip.isEnabled = true
            }
            view_image_placeholder.visibility = View.VISIBLE
            b_activate_plan.visibility = View.GONE
            if(mode == MODE_CREATE_NEW || mode == MODE_COPY_FROM_PLAN){
                card_include_meal_plates.visibility = View.VISIBLE
            } else {
                card_include_meal_plates.visibility = View.GONE
            }
            cdv.setMode(CaloryDistributionView.MODE_EDIT);
            ll_typical_paltes.visibility = View.GONE
        } else {
            fab.setImageResource(R.drawable.ic_mode_edit)
            fab.refreshDrawableState()
            til_plan_name.editText?.apply {
                isEnabled = false
                isClickable = false
                isFocusable = false
            }
            til_plan_desc.editText?.apply {
                isEnabled = false
                isClickable = false
                isFocusable = false
            }
            til_diet_plan_daily_approx_cal.editText?.apply {
                isEnabled = false
                isClickable = false
                isFocusable = false
            }
            var count = chip_group_plan_type.childCount
            for(i in 0..count-1){
                var chip = chip_group_plan_type.getChildAt(i) as Chip
                chip.isClickable = false
                chip.isEnabled = false
            }
            view_image_placeholder.visibility = View.GONE
            b_activate_plan.visibility = View.VISIBLE
            card_include_meal_plates.visibility = View.GONE
            cdv.setMode(CaloryDistributionView.MODE_EXPLORE);
            ll_typical_paltes.visibility = View.VISIBLE
        }
    }



   /* private fun onCreatePlanClicked() {
        var result:String? = validateData()
        if(TextUtils.isEmpty(result)){
            createPlan()
        } else {
            showError(result!!)
        }
    }*/

    private fun validateData(): String? {
        //validate name
        if(TextUtils.isEmpty(til_plan_name.editText?.text.toString())){
            return "Enter plan name"
        }
        if(TextUtils.isEmpty(til_plan_desc.editText?.text.toString())){
            return "Enter plan description"
        }
        if(TextUtils.isEmpty(til_diet_plan_daily_approx_cal.editText?.text.toString())){
            return "Enter Daily Approx. Calory"
        }
        if(!(chip_vegiterion.isChecked || chip_non_vegiterion.isChecked)){
            return "Select Plan food type"
        }
        return null
    }

    private fun savePlanChanges() {
        showProgress("Saving plan changes..")
        var modifiedPlan: DietPlan = prepareModifiedPlan()
        aiv_plan.viewModel?.pickedBitmap?.let{
            // save picked bitmap image
            var localUri = aiv_plan.viewModel.savePickedImage(modifiedPlan.basicInfo?.name+Utils.timeStamp())
            modifiedPlan.basicInfo?.image = localUri
        }
        repo.updatePlan(modifiedPlan,this)
    }
    private fun createPlan() {
        showProgress("Creating new Diet Plan..")
        var newPlan: DietPlan = prepareNewPlan()
        aiv_plan.viewModel?.pickedBitmap?.let{
            // save picked bitmap image
            var localUri = aiv_plan.viewModel.savePickedImage(newPlan.basicInfo?.name+Utils.timeStamp())
            newPlan.basicInfo?.image = localUri
        }
        repo.createDietPlan(newPlan,this)
    }

    private fun prepareModifiedPlan(): DietPlan {

        thePlan.basicInfo.name = til_plan_name.editText?.text.toString()
        thePlan.basicInfo.desc = til_plan_desc.editText?.text.toString()
        thePlan.caloryDistribution = cdv.getCaloryDistribution()
        if(chip_vegiterion.isChecked){
            thePlan.basicInfo.type = "veg"
        } else if(chip_non_vegiterion.isChecked){
            thePlan.basicInfo.type = "non-veg"
        }
        thePlan.basicInfo.dailyCalories = til_diet_plan_daily_approx_cal.editText?.text.toString().toInt()
        //calendar will be present as earlier

        thePlan.adminInfo.lastModifiedOn  = Utils.timeStamp()


        return thePlan
    }

    private fun prepareNewPlan(): DietPlan {
        thePlan.basicInfo.name = til_plan_name.editText?.text.toString()
        thePlan.basicInfo.desc = til_plan_desc.editText?.text.toString()
        thePlan.caloryDistribution = cdv.getCaloryDistribution()
       // plan.basicInfo.image = "https://res.cloudinary.com/techticz/image/upload/v1506274760/plans/fifth_2017.09.24.23.09.18.jpg"

        if(mode == MODE_COPY_FROM_PLAN && cb_source_plan_plates.isChecked){
            //copy plates from source plan
            thePlan.calendar = sourceDietPlan?.calendar
        } else if(cb_random_plates.isChecked){
            thePlan.calendar = Calendar()
            //TODO:random plates logic here
        } else {
            thePlan.calendar = Calendar()
        }

        // define admin info
        thePlan.adminInfo.createdBy = LoginUtils.getUserCredential()
        thePlan.adminInfo.createdOn  = Utils.timeStamp()

        thePlan.id = "PLN_"+LoginUtils.getUserName().substring(0,2)+"_"+thePlan.adminInfo.createdOn

        return thePlan
    }

    override fun onPlanCreated(plan: DietPlan) {
        //createdPlan = plan
        showSuccess("Plan created successfully")
        mode = MODE_EXPLORE
        var user = baseuserViewModel?.liveUserResponse?.value?.data?.user
        if(user?.myPlans == null){
            user?.myPlans = ArrayList()
        }
        user?.myPlans?.add(plan.id)
        baseuserViewModel?.injectedRepo.updateUser(user!!,this)
        configureUIInEditMode(false)

    }
    override fun onBitmapPicked(bitmap: Bitmap) {
        // this bitmap is still not saved: Save it while saving recipe
        if(aiv_plan.viewModel == null){
            aiv_plan.viewModel = ImageViewModel(this)
        }
        aiv_plan.viewModel.pickedBitmap = bitmap
        aiv_plan.setImageBitmap(bitmap)
    }
    override fun onCreatePlanFailure() {
        showError("Plan could not be created!")
    }

    override fun onUserRegistered(userId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserRegistrationFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserUpdated(id: String) {
        hideProgress()
        updatePlanAccessebility()
        if(isActivatingPlan){
            isActivatingPlan = false
            EventBus.getDefault().postSticky(FreshLoadPlans());
            EventBus.getDefault().postSticky(FreshLoadDashboard());
            navigator.startDashBoard()
            finish()
        }
    }


    override fun onUserUpdateFailure() {
        hideProgress()
        showError("Profile could not be updated!")
    }
    override fun onPlanUpdated(plan: DietPlan) {
        hideProgress()
        mode = MODE_EXPLORE
        configureUIInEditMode(false)
        showSuccess("Plan updated successfully!")
    }

    override fun onPlanUpdateFailure() {
        hideProgress()
        showError("Plan could not be updated!!")
    }

}
