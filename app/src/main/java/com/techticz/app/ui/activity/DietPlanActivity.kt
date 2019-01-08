package com.techticz.app.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CompoundButton
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.dietplan.AdminInfo
import com.techticz.app.model.dietplan.BasicInfo
import com.techticz.app.model.dietplan.Calendar
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.repo.DietPlanRepository
import com.techticz.app.repo.UserRepository
import com.techticz.app.util.Utils
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.activity_diet_plan.*
import kotlinx.android.synthetic.main.content_diet_plan.*
import org.parceler.Parcels
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DietPlanActivity : BaseDIActivity(), DietPlanRepository.DietPlanCallBack, UserRepository.UserProfileCallback {


    @Inject
    lateinit var repo:DietPlanRepository

    companion object{
        var MODE_CREATE_NEW = 0
        var MODE_COPY_FROM_PLAN = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_plan)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout
        fab.setOnClickListener { view ->
            onCreatePlanClicked()
        }
        initData()
        initUI()
    }

    private fun initUI() {
        if(mode != MODE_COPY_FROM_PLAN || sourceDietPlan == null){
            cb_source_plan_plates.visibility = GONE
            til_source_plan_name.visibility = GONE
            tv_or.visibility = GONE
        } else {
            tv_or.visibility = VISIBLE
            cb_source_plan_plates.visibility = VISIBLE
            til_source_plan_name.visibility = VISIBLE
            til_source_plan_name.editText?.setText(sourceDietPlan?.basicInfo?.name)
            cb_source_plan_plates.isChecked = true
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
        cb_source_plan_plates.setOnCheckedChangeListener(sourcPlanCheckBoxListner)
        cb_random_plates.setOnCheckedChangeListener(randomPlanCheckBoxListner)
    }

    private var mode: Int = 0
    private var sourceDietPlan: DietPlan? = null

    private fun initData() {
        mode = intent.getIntExtra("mode",0)
        if(mode == MODE_CREATE_NEW){

        } else if(mode == MODE_COPY_FROM_PLAN){
            sourceDietPlan = Parcels.unwrap<Any>(intent.getParcelableExtra("plan")) as DietPlan
        }

    }

    private fun onCreatePlanClicked() {
        var result:String? = validateData()
        if(TextUtils.isEmpty(result)){
            createPlan()
        } else {
            showError(result!!)
        }
    }

    private fun validateData(): String? {
        //validate name
        if(TextUtils.isEmpty(til_plan_name.editText?.text.toString())){
            return "Enter plan name"
        }
        if(TextUtils.isEmpty(til_plan_desc.editText?.text.toString())){
            return "Enter plan description"
        }
        return null
    }

    private fun createPlan() {
        var newPlan: DietPlan = prepareNewPlan()
        repo.createDietPlan(newPlan,this)
    }

    private fun prepareNewPlan(): DietPlan {
        var plan = DietPlan()
        plan.basicInfo = BasicInfo()
        plan.basicInfo.name = til_plan_name.editText?.text.toString()
        plan.basicInfo.desc = til_plan_desc.editText?.text.toString()
        plan.basicInfo.image = "https://res.cloudinary.com/techticz/image/upload/v1506274760/plans/fifth_2017.09.24.23.09.18.jpg"

        if(mode == MODE_COPY_FROM_PLAN && cb_source_plan_plates.isChecked){
            //copy plates from source plan
            plan.calendar = sourceDietPlan?.calendar
        } else if(cb_random_plates.isChecked){
            plan.calendar = Calendar()
            //TODO:random plates logic here
        } else {
            plan.calendar = Calendar()
        }

        // define admin info
        plan.adminInfo = AdminInfo()
        plan.adminInfo.createdBy = LoginUtils.getUserCredential()
        plan.adminInfo.createdOn  = Utils.timeStamp()

        plan.id = "PLN_"+LoginUtils.getUserName().substring(0,2)+"_"+plan.adminInfo.createdOn

        return plan
    }

    private lateinit var createdPlan: DietPlan

    override fun onPlanCreated(plan: DietPlan) {
        createdPlan = plan
        showSuccess("Plan created successfully")
        var user = baseuserViewModel?.liveUserResponse?.value?.data?.user
        if(user?.myPlans == null){
            user?.myPlans = ArrayList()
        }
        user?.myPlans?.add(plan.id)

        baseuserViewModel?.injectedRepo.updateUser(user!!,this)

    }

    override fun onCreatePlanFailure() {
        showError("Plan could not be created!")
    }

    override fun onRegistered(userId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRegistrationFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpdated(id: String) {
        navigator.startDietChartScreen(this,createdPlan)
        finish()
    }

    override fun onUpdateFailure() {
        showError("Plan could not be updated in user profile!")
    }
    override fun onPlanUpdated(plan: DietPlan) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlanUpdateFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
