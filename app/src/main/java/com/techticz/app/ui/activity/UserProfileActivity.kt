package com.techticz.app.ui.activity

import android.os.Build
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

import androidx.fragment.app.FragmentPagerAdapter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.chip.ChipGroup
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.UserResponse
import com.techticz.app.model.food.Nutrition
import com.techticz.app.model.user.*
import com.techticz.app.repo.UserRepository
import com.techticz.app.ui.customView.NutritionDetailsView
import com.techticz.app.util.Utils
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.auth.utils.LoginUtils

import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile_basic_details.*
import kotlinx.android.synthetic.main.fragment_user_profile_goal_details.*
import kotlinx.android.synthetic.main.fragment_user_profile_meal_prefs.*
import kotlinx.android.synthetic.main.meal_plan_list_item_view.*
import timber.log.Timber
import android.R.attr.startYear
import android.app.DatePickerDialog
import android.text.format.DateUtils
import android.widget.DatePicker
import com.google.android.material.chip.Chip
import com.techticz.app.model.food.Nutrients
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class UserProfileActivity : BaseDIActivity(), UserRepository.UserProfileCallback {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var isEditModeOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        initUI()
        baseuserViewModel.liveUserResponse.observe(this, Observer { res -> onUserLoaded(res) })

    }

    private fun validateSectionData(currentItem: Int): String? {
        when (currentItem + 1) {
            1 -> return ((container.adapter as SectionsPagerAdapter).basicDetailsFrag)?.validateData()
            2 -> return (container.adapter as SectionsPagerAdapter).healthDetailsFrag?.validateData()
            3 -> return (container.adapter as SectionsPagerAdapter).mealPrefsFrag?.validateData()
            else -> return null
        }
    }


    private fun proceedClicked(view: View) {
        Snackbar.make(view, "Proceed button clicked", Snackbar.LENGTH_LONG)
                .setAction("OK", null).show()
        if (isEditModeOn) {
            var newUser = User(LoginUtils.getCurrentUserId())
            newUser.basicInfo = (container.adapter as SectionsPagerAdapter).basicDetailsFrag?.getBasicDetails()
            newUser.healthProfile = (container.adapter as SectionsPagerAdapter).basicDetailsFrag?.getHealthProfile()
            newUser.goal = (container.adapter as SectionsPagerAdapter).healthDetailsFrag?.getGoal()
            newUser.mealPref = (container.adapter as SectionsPagerAdapter).mealPrefsFrag?.getMealPrefs()

            showProgress()
            if (isNewUser) {
                baseuserViewModel.registerUser(newUser, this)
            } else {
                newUser.activePlan = baseuserViewModel.liveUserResponse?.value?.data?.user?.activePlan
                newUser.access = baseuserViewModel.liveUserResponse?.value?.data?.user?.access
                baseuserViewModel.updateUser(newUser, this)
            }
        } else {
            finish()
        }
    }

    var isNewUser = false

    private fun onUserLoaded(res: Resource<UserResponse>?) {
        when (res?.status) {
            Status.SUCCESS -> {
                if (isNewUser) {
                    hideProgress()
                    showSuccess("Hi " + res?.data?.user?.basicInfo?.name + ", Profile successfully created")
                    navigator.startBrowsePlanScreen()
                    finish()
                } else {
                    isNewUser = false
                    isEditModeOn = false
                    fab_edit.visibility = View.VISIBLE
                    Timber.d("Profile Loaded")
                    showSuccess("Profile Loaded..")
                    updateEditability()
                }
                updateUI()
            }
            Status.EMPTY -> {
                Timber.d("User is EMPTY: NOT Registered!!")
                isNewUser = true
                isEditModeOn = true
                fab_edit.visibility = View.GONE
                updateEditability()

                (fab_forward.parent as View).visibility = View.VISIBLE
                var newUser = User(LoginUtils.getCurrentUserId())
                newUser.basicInfo.name = LoginUtils.getUserName()
                newUser.basicInfo.credential = LoginUtils.getUserCredential()
                res?.data?.user = newUser
                updateUI()
            }
        }
    }

    private fun updateUI() {
        Timber.d("Updating fragments UI")
        mSectionsPagerAdapter?.basicDetailsFrag?.initUI()
        mSectionsPagerAdapter?.healthDetailsFrag?.initUI()
        mSectionsPagerAdapter?.mealPrefsFrag?.initUI()
    }

    private fun initUI() {
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        (fab_backward.parent as View).visibility = View.GONE
        container.offscreenPageLimit = 3

        container?.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position > 0) {
                    (fab_backward.parent as View).visibility = View.VISIBLE
                } else {
                    (fab_backward.parent as View).visibility = View.GONE
                }
                if (position == 2) {
                    //last pos selected
                    fab_forward.setImageResource(R.drawable.ic_check)
                } else {
                    fab_forward.setImageResource(R.drawable.ic_arrow_forward)
                }
            }

        })
        fab_forward.setOnClickListener { view ->
            var validationResult = validateSectionData(container.currentItem)
            if (TextUtils.isEmpty(validationResult)) {
                if (container.currentItem < 2) {
                    container.setCurrentItem(container.currentItem + 1, true)
                } else if (container.currentItem == 2) {
                    proceedClicked(view)
                }
            } else {
                showError(view, validationResult)
            }

        }
        fab_backward.setOnClickListener { view ->
            if (container.currentItem > 0) {
                container.setCurrentItem(container.currentItem - 1, true)
            }

        }

        fab_edit.visibility = View.VISIBLE
        fab_edit.setOnClickListener({ view ->
            isEditModeOn = true
            view.visibility = View.GONE
            updateEditability()
        })
    }

    private fun updateEditability() {
        Timber.d("updating Editability:")
        mSectionsPagerAdapter?.basicDetailsFrag?.updateEditability()
        mSectionsPagerAdapter?.healthDetailsFrag?.updateEditability()
        mSectionsPagerAdapter?.mealPrefsFrag?.updateEditability()
    }

    private fun showError(view: View, validationResult: String?) {
        Snackbar.make(view, validationResult.toString(), Snackbar.LENGTH_LONG)
                .setAction("OK", null).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_user_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRegistered(userId: String) {
        hideProgress()
        isEditModeOn = false
        baseuserViewModel.triggerUserId.value = userId
    }

    override fun onRegistrationFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpdated(id: String) {
        hideProgress()
        isEditModeOn = false
        fab_edit.visibility = View.VISIBLE
        updateEditability()
        baseuserViewModel.triggerUserId.value = id
    }

    override fun onUpdateFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {

        var basicDetailsFrag: BasicDetailsFragment? = null
        var healthDetailsFrag: GoalDetailsFragment? = null
        var mealPrefsFrag: MealPrefFragment? = null

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            when (position + 1) {
                1 -> {
                    if (basicDetailsFrag == null) {
                        basicDetailsFrag = BasicDetailsFragment.newInstance(1)
                    }
                    return basicDetailsFrag!!;
                }
                2 -> {
                    if (healthDetailsFrag == null) {
                        healthDetailsFrag = GoalDetailsFragment.newInstance(2)
                    }
                    return healthDetailsFrag!!;
                }
                3 -> {
                    if (mealPrefsFrag == null) {
                        mealPrefsFrag = MealPrefFragment.newInstance(3)
                    }
                    return mealPrefsFrag!!;
                }
                else -> {
                    return mealPrefsFrag!!;
                }
            }

        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    class BasicDetailsFragment : androidx.fragment.app.Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            var rootView: View? = inflater.inflate(R.layout.fragment_user_profile_basic_details, container, false)
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initUI();
            updateEditability()
        }

        fun initUI() {
            Timber.d("Updating basic profile UI")
            var user = (activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user
            til_name.editText?.setText(user?.basicInfo?.name)
            til_credential.editText?.setText(user?.basicInfo?.credential)
            til_dob.editText?.setText(user?.basicInfo?.dob)
            til_dob.editText?.setOnClickListener({ showDatePickerDialog()})
            when (user?.basicInfo?.gender) {
                "male" -> rb_male.setChecked(true)
                "female" -> rb_female.setChecked(true)
            }

            Timber.d("Updating health profile UI")
            //var user =(activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user

            user?.healthProfile?.height?.let {
                tv_height?.text = "" + it + " CMs"
                sb_height?.progress = it.toInt()
            }
            user?.healthProfile?.weight?.let {
                tv_weight?.text = "" + it + " KGs"
                sb_weight?.progress = it.toInt()
            }

            when (user?.healthProfile?.activityLevel) {
                "low" -> rb_low.setChecked(true)
                "moderate" -> rb_moderate.setChecked(true)
                "high" -> rb_high.setChecked(true)
                "extreme" -> rb_extreme.setChecked(true)
            }
            if (user?.healthProfile?.drink != null)
                when (user?.healthProfile?.drink) {
                    "low" -> checkbox_no_drink.isChecked = true
                    "moderate" -> checkbox_moderate_drink.isChecked = true
                    "high" -> checkbox_high_drink.isChecked = true
                }

            user?.healthProfile?.smoke?.let {
                checkbox_smoke.isChecked = it
                checkbox_no_smoke.isChecked = !it
            }

            sb_height?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    tv_height?.text = "" + progress + " CMs"
                }
            })
            sb_weight?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    tv_weight?.text = "" + progress + " KGs"
                }
            })
            (activity as BaseDIActivity).baseuserViewModel.liveImage?.observe(activity as UserProfileActivity, Observer { res -> onImageModelLoaded(res) })
            (activity as BaseDIActivity).baseuserViewModel.autoLoadChildren(activity as UserProfileActivity)
        }

        private fun showDatePickerDialog() {
            var stringDate = til_dob.editText?.text.toString()
            var calendar:Calendar
            try{
            val formatter = SimpleDateFormat("dd-MM-yyyy")
            val date = formatter.parse(stringDate)
                calendar = Calendar.getInstance()
                calendar.time = date

            } catch (e:Exception){
                calendar = Calendar.getInstance()
            }
            val datePickerDialog = DatePickerDialog(
                    context, object:DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    til_dob.editText?.setText(""+dayOfMonth+"-"+(month+1)+"-"+year)
                }
            }, calendar?.get(Calendar.DAY_OF_MONTH), calendar?.get(Calendar.MONTH), calendar?.get(Calendar.YEAR))

            datePickerDialog.show()
        }

        private fun onImageModelLoaded(res: Resource<ImageViewModel>?) {
            when (res?.status) {
                Status.LOADING -> aiv_user.setImageViewModel(res?.data, activity as UserProfileActivity)
                Status.SUCCESS -> aiv_user.setImageViewModel(res?.data, activity as UserProfileActivity)
            }
        }

        fun validateData(): String? {
            if (TextUtils.isEmpty(til_name.editText?.text)) {
                return "Please enter valid Name!"
            }
            if (TextUtils.isEmpty(til_dob.editText?.text)) {
                return "Please enter valid Date Of Birth!"
            }
            if (!(rb_male.isChecked || rb_female.isChecked)) {
                return "Please select Gender!"
            }

            if (TextUtils.isEmpty(tv_height?.text)) {
                return "Please enter valid Height!"
            }
            if (TextUtils.isEmpty(tv_weight?.text)) {
                return "Please enter valid Weight!"
            }

            if (!(rb_low.isChecked || rb_moderate.isChecked || rb_high.isChecked || rb_extreme.isChecked)) {
                return "Please select Activity Level!"
            }

            if (!(checkbox_no_drink.isChecked || checkbox_moderate_drink.isChecked || checkbox_high_drink.isChecked)) {
                return "Please mention drinking habit!"
            }

            if (!(checkbox_no_smoke.isChecked || checkbox_smoke.isChecked)) {
                return "Please mention smoking habit"
            }
            return null;
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): BasicDetailsFragment {
                val fragment = BasicDetailsFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

        fun getBasicDetails(): BasicInfo? {
            var basicInfo = BasicInfo()
            basicInfo.image = LoginUtils.getUserImageUrl()
            basicInfo.name = LoginUtils.getUserName()
            basicInfo.credential = LoginUtils.getUserCredential()
            if (rb_male.isChecked) {
                basicInfo.gender = "male"
            } else {
                basicInfo.gender = "female"
            }
            basicInfo.dob = til_dob.editText?.text.toString()
            return basicInfo
        }

        fun getHealthProfile(): HealthProfile? {
            var health = HealthProfile()
            health.height = sb_height?.progress?.toFloat()
            health.weight = sb_weight?.progress?.toFloat()
            if (rb_low.isChecked) {
                health.activityLevel = "low"
            } else if (rb_moderate.isChecked) {
                health.activityLevel = "moderate"
            } else if (rb_high.isChecked) {
                health.activityLevel = "high"
            } else if (rb_extreme.isChecked) {
                health.activityLevel = "extreme"
            }
            if (checkbox_no_drink.isChecked) {
                health.drink = "low"
            } else if (checkbox_moderate_drink.isChecked) {
                health.drink = "moderate"
            } else if (checkbox_high_drink.isChecked) {
                health.drink = "high"
            }

            health.smoke = !checkbox_no_smoke.isChecked

            return health

        }

        fun updateEditability() {
            var b = (activity as UserProfileActivity).isEditModeOn
            Timber.d("making basic profile editable:" + b)
            // (til_dob.editText as EditText).isFocusable = b
            (til_dob.editText as EditText).isEnabled = b
            (til_dob.editText as EditText).isClickable = b

            rb_male.isFocusable = b
            rb_male.isEnabled = b
            rb_male.isClickable = b

            rb_female.isFocusable = b
            rb_female.isEnabled = b
            rb_female.isClickable = b

            Timber.d("making health profile editable:" + b)

            sb_height.isEnabled = b
            sb_height.isClickable = b

            sb_weight.isEnabled = b
            sb_weight.isClickable = b

            rb_low.isEnabled = b
            rb_low.isClickable = b

            rb_moderate.isEnabled = b
            rb_moderate.isClickable = b

            rb_high.isEnabled = b
            rb_high.isClickable = b

            rb_extreme.isEnabled = b
            rb_extreme.isClickable = b

            checkbox_no_drink.isEnabled = b
            checkbox_no_drink.isClickable = b

            checkbox_moderate_drink.isEnabled = b
            checkbox_moderate_drink.isClickable = b

            checkbox_high_drink.isEnabled = b
            checkbox_high_drink.isClickable = b

            checkbox_smoke.isEnabled = b
            checkbox_smoke.isClickable = b

            checkbox_no_smoke.isEnabled = b
            checkbox_no_smoke.isClickable = b
        }
    }


    class GoalDetailsFragment : androidx.fragment.app.Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            var rootView: View? = inflater.inflate(R.layout.fragment_user_profile_goal_details, container, false)
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initUI();
            updateEditability()
        }

        override fun setUserVisibleHint(isVisibleToUser: Boolean) {
            super.setUserVisibleHint(isVisibleToUser)
            if(isVisibleToUser){
                //becoming visible
                updateBMICard()
            } else {

            }
        }

        private var dailyRequiredCalory: Int? = 0

        fun initUI() {

            Timber.d("Updating health profile UI")
            var user = (activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user
            // til_target_weight.editText?.setText(""+user?.goal?.targetWeight)

            updateBMICard()
            user?.goal?.targetWeight?.let {
                tv_target_weight?.text = "" + it
                sb_target_weight?.progress = it.toInt()
            }
            user?.goal?.durationInWeek?.let {
                tv_duration?.text = "" + it
                sb_duration?.progress = it.toInt()
            }
            when(user?.goal?.goalType?.toLowerCase()){
                "gain weight"->{radio_group_goal.check(R.id.rb_gain_weight)}
                "lose weight"->{radio_group_goal.check(R.id.rb_lose_weight)}
                "stay energetic"->{radio_group_goal.check(R.id.rb_energetic)}
            }
            radio_group_goal.setOnCheckedChangeListener(object:ChipGroup.OnCheckedChangeListener{
                override fun onCheckedChanged(chipGroup: ChipGroup?, p1: Int) {
                    var weightInKg = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getHealthProfile()?.weight
                    when(chipGroup?.checkedChipId) {
                        null-> ll_target_container.visibility = View.VISIBLE
                        R.id.rb_gain_weight ->{
                            ll_target_container.visibility = View.VISIBLE
                        }
                        R.id.rb_lose_weight ->{
                            ll_target_container.visibility = View.VISIBLE
                        }
                        R.id.rb_energetic ->{
                            ll_target_container.visibility = View.VISIBLE
                        }
                    }

                }

            })
            sb_target_weight?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    tv_target_weight?.text = "" + progress + " KGs"
                    dailyRequiredCalory = getDailyRequiredCalories(progress.toFloat(),sb_duration.progress)?.toInt()
                    tv_daily_required_calories.text = ""+dailyRequiredCalory+"\uD83D\uDD25"+" KCAL"
                }
            })
            sb_duration?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    tv_duration?.text = "" + progress + " Weeks"
                    dailyRequiredCalory = getDailyRequiredCalories(sb_target_weight.progress.toFloat(),progress)?.toInt()
                    tv_daily_required_calories.text = ""+dailyRequiredCalory+"\uD83D\uDD25"+" KCAL"

                }
            })

        }

        private fun updateBMICard() {
            var bmi = getBMI()
            var idealWeight = getIdealWeight()
            tv_bmi.text = ""+bmi
            tv_weight_comment.text = getWeightComment(bmi)
            tv_weight_recommendation.text = "Recommondation: your ideal weight should be "+idealWeight+" KGs"

            var user = (activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user

            var targgetW:Float? = null
            if(user?.goal == null || user?.goal?.targetWeight == null){
                tv_target_weight.text = ""+idealWeight+" KGs"
                sb_target_weight.progress = idealWeight!!.toInt()
                targgetW = idealWeight
            } else {
                tv_target_weight.text = ""+user?.goal?.targetWeight+" KGs"
                sb_target_weight.progress = user?.goal?.targetWeight!!.toInt()
                targgetW = user?.goal?.targetWeight
            }

            var duration:Int? = null
            if(user?.goal == null || user?.goal?.durationInWeek == null){
                tv_duration.text = ""+24+" Weeks"
                sb_duration.progress = 24
                duration = 24
            } else {
                tv_duration.text = ""+user?.goal?.durationInWeek+" Weeks"
                sb_duration.progress = user?.goal?.durationInWeek!!.toInt()
                duration = user?.goal?.durationInWeek
            }
            dailyRequiredCalory = getDailyRequiredCalories(targgetW,duration)?.toInt()
            tv_daily_required_calories.text = ""+dailyRequiredCalory+"\uD83D\uDD25"+" KCAL"
        }

        fun getDailyRequiredCalories(targgetW: Float?, duration: Int?): Float? {
            var weightInKg = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getHealthProfile()?.weight
            var bmr = getBMR()
            var activityFactor:Float? = null
            var activityType = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getHealthProfile()?.activityLevel
            when(activityType?.toLowerCase()){
                "low"->{activityFactor = 1.2f}
                "moderate"->{ activityFactor = 1.55f}
                "high"->{activityFactor = 1.725f}
                "extreme"->{activityFactor = 1.9f}
            }

            var maintainCalory = bmr!! * activityFactor!!

            tv_daily_maintain_calories.text = ""+maintainCalory.toInt()+"\uD83D\uDD25"+" KCAL"

            var weightDiff:Float?
            if(targgetW!! < weightInKg!!){
                weightDiff = weightInKg - targgetW
            } else {
                weightDiff = targgetW- weightInKg
            }


            var perWeekWeightDiff = weightDiff/duration!!
            // for loosing/gaining 1 kg weight in 1 week calory deficit 3500 * 2.2 Calories with no activity
            var deficitCaloryPerKgPerWeek = (3500 * 2.2)/7

            var extraRequiredCalories = perWeekWeightDiff * deficitCaloryPerKgPerWeek

            var totalRequiredCalory:Float? = null

            if(targgetW!! < weightInKg!!){
                totalRequiredCalory = maintainCalory.toFloat() - extraRequiredCalories.toFloat()
            } else {
                totalRequiredCalory = maintainCalory.toFloat()+ extraRequiredCalories.toFloat()
            }

            var user = (activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user
            if(user?.goal == null){
                user?.goal = Goal()
            }
            user?.goal?.dailyRequiredCalories = totalRequiredCalory
            return totalRequiredCalory

        }

        private fun getBMR(): Float? {
            var gender = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getBasicDetails()?.gender
            var weightInKg = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getHealthProfile()?.weight
            var heightInCm = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getHealthProfile()?.height

            var ageInYears = 30
            var bmr:Double? = null
            when(gender?.toLowerCase()){
                "male"->{
                   bmr =  66+(6.3 * 2.2* weightInKg!!) + (12.9 * 0.39 * heightInCm!!) - (6.8 * ageInYears)
                }
                "female"->{
                    bmr =  655+(4.3 * 2.2* weightInKg!!) + (4.7 * 0.39 * heightInCm!!) - (4.7 * ageInYears)
                }
            }
            return bmr?.toFloat()
        }

        private fun getIdealWeight(): Float? {
            var heightInCm = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getHealthProfile()?.height

            heightInCm?.let {
                var bmi = 21.5f
                var weightInKg = (bmi * (heightInCm * heightInCm))/10000
                return Utils.roundUpFloatToOneDecimal(weightInKg)
            }
            return 0f
        }

        private fun getWeightComment(bmi: Float?): String {
            bmi?.let {
                if(it < 18.5f){
                    return "You are Underweight"
                } else if(it >= 18.5 && it < 24.9) {
                    return "You are Normal"
                } else if(it >= 25 && it <=29.9) {
                    return "You are Overweight"
                } else if(it >=  30){
                    return "You are Obese"
                }
            }
            return "";

        }

        private fun getBMI(): Float? {
            var weightInKg = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getHealthProfile()?.weight
            var heightInCm = (activity as UserProfileActivity).mSectionsPagerAdapter?.basicDetailsFrag?.getHealthProfile()?.height

            weightInKg?.let {
                var bmi = (weightInKg / (heightInCm!! * heightInCm!!))*10000
                return Utils.roundUpFloatToOneDecimal(bmi)
            }
            return 0f
        }

        fun updateEditability() {
            var b = (activity as UserProfileActivity).isEditModeOn
            radio_group_goal.isEnabled = b
            rb_energetic.isEnabled = b
            rb_gain_weight.isEnabled = b
            rb_lose_weight.isEnabled = b

            sb_target_weight.isEnabled = b
            sb_duration.isEnabled = b

        }

        fun validateData(): String? {
            if(radio_group_goal.checkedChipId == null){
                return "Please select goal"
            }

            return null;
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): GoalDetailsFragment {
                val fragment = GoalDetailsFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

        fun getGoal(): Goal? {
            var goal = Goal()

            var id = radio_group_goal.checkedChipId
            goal.goalType = radio_group_goal.findViewById<Chip>(id).text.toString()
            goal.targetWeight = sb_target_weight.progress.toFloat()
            goal.durationInWeek = sb_duration.progress
            goal.dailyRequiredCalories = dailyRequiredCalory?.toFloat()
            return goal

        }
    }

    class MealPrefFragment : androidx.fragment.app.Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            var rootView: View? = inflater.inflate(R.layout.fragment_user_profile_meal_prefs, container, false)
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initUI();
            updateEditability()
        }
        override fun setUserVisibleHint(isVisibleToUser: Boolean) {
            super.setUserVisibleHint(isVisibleToUser)
            if(isVisibleToUser){
                //becoming visible
                updateCaloryDistribution()
            } else {

            }
        }

        private fun updateCaloryDistribution() {
            var user = (activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user

            var dailyRequiredCalory = user?.goal?.dailyRequiredCalories
            tv_calory_dist_total.text = ""+(dailyRequiredCalory!!).toInt()+"\uD83D\uDD25"+" KCAL"
            tv_calory_dist_early_morning.text = "Early Morning - "+(dailyRequiredCalory!! *0.10f).toInt()+"\uD83D\uDD25"+" KCAL"
            tv_calory_dist_breakfast.text = "Breakfast - "+(dailyRequiredCalory!! *0.30f).toInt()+"\uD83D\uDD25"+" KCAL"
            tv_calory_dist_lunch.text = "Lunch - "+(dailyRequiredCalory!! *0.20f).toInt()+"\uD83D\uDD25"+" KCAL"
            tv_calory_dist_snacks.text = "Evening Snacks - "+(dailyRequiredCalory!! *0.10f).toInt()+"\uD83D\uDD25"+" KCAL"
            tv_calory_dist_dinner.text = "Dinner - "+(dailyRequiredCalory!! *0.20f).toInt()+"\uD83D\uDD25"+" KCAL"
            tv_calory_dist_bed_time.text = "Bed Time - "+(dailyRequiredCalory!! *0.10f).toInt()+"\uD83D\uDD25"+" KCAL"

            var nutritionRDA = Nutrition()
            nutritionRDA.nutrients = Nutrients()
            nutritionRDA.nutrients.principlesAndDietaryFibers?.energy = user?.goal?.dailyRequiredCalories!! * 4.18f
            card_nutri_details.removeAllViews()
            card_nutri_details.addView(NutritionDetailsView(view as ViewGroup, "Recommended Dietry Allowance", "Amount", "RDA", nutritionRDA, nutritionRDA, NutritionDetailsView.MODE_RDA))

        }

        fun initUI() {
            Timber.d("Updating meal pref UI")
            var user = (activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user

            updateCaloryDistribution()

            if (user?.mealPref?.cheese != null)
                cb_cheese.isChecked = user?.mealPref?.cheese!!
            else cb_cheese.isChecked = true

            if (user?.mealPref?.milk != null)
                cb_milk.isChecked = user?.mealPref?.milk!!
            else cb_milk.isChecked = true

            if (user?.mealPref?.onion != null)
                cb_onion.isChecked = user?.mealPref?.onion!!
            else cb_onion.isChecked = true


            if (user?.mealPref?.garlic != null)
                cb_garlic.isChecked = user?.mealPref?.garlic!!
            else cb_garlic.isChecked = true

            if (user?.mealPref?.egg != null)
                cb_egg.isChecked = user?.mealPref?.egg!!
            else cb_egg.isChecked = true


            if (user?.mealPref?.chicken != null)
                cb_chicken.isChecked = user?.mealPref?.chicken!!
            else cb_chicken.isChecked = true

            if (user?.mealPref?.fish != null)
                cb_fish.isChecked = user?.mealPref?.fish!!
            else cb_fish.isChecked = true

            if (user?.mealPref?.mutton != null)
                cb_mutton.isChecked = user?.mealPref?.mutton!!
            else cb_mutton.isChecked = true

            if ((activity as UserProfileActivity).isNewUser) {
                (cb_terms_conditions.parent as View).visibility = View.VISIBLE
            } else {
                (cb_terms_conditions.parent as View).visibility = View.GONE
            }
        }

        fun updateEditability() {
            var b = (activity as UserProfileActivity).isEditModeOn
            Timber.d("making meal pref profile editable:" + b)
            //   cb_milk.isFocusable = b
            //   cb_milk.isEnabled = b
            cb_milk.isClickable = b
            cb_milk.jumpDrawablesToCurrentState()
            //    cb_cheese.isFocusable = b
            //    cb_cheese.isEnabled = b
            cb_cheese.isClickable = b
            cb_cheese.jumpDrawablesToCurrentState()
            //   cb_onion.isFocusable = b
            //   cb_onion.isEnabled = b
            cb_onion.isClickable = b
            cb_onion.jumpDrawablesToCurrentState()
            //   cb_garlic.isFocusable = b
            //   cb_garlic.isEnabled = b
            cb_garlic.isClickable = b
            cb_garlic.jumpDrawablesToCurrentState()
            //   cb_egg.isFocusable = b
            //   cb_egg.isEnabled = b
            cb_egg.isClickable = b
            cb_egg.jumpDrawablesToCurrentState()
            //   cb_chicken.isFocusable = b
            //   cb_chicken.isEnabled = b
            cb_chicken.isClickable = b
            cb_chicken.jumpDrawablesToCurrentState()
            //    cb_mutton.isFocusable = b
            //   cb_mutton.isEnabled = b
            cb_mutton.isClickable = b
            cb_mutton.jumpDrawablesToCurrentState()
            //    cb_fish.isFocusable = b
            //   cb_fish.isEnabled = b
            cb_fish.isClickable = b
            cb_fish.jumpDrawablesToCurrentState()
        }

        fun validateData(): String? {
            if ((activity as UserProfileActivity).isNewUser && !cb_terms_conditions.isChecked) {
                return "Read terms and conditions and Accept to continue!"
            }
            return null;
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): MealPrefFragment {
                val fragment = MealPrefFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

        fun getMealPrefs(): MealPref? {
            var pref = MealPref()
            pref.milk = cb_milk.isChecked
            pref.cheese = cb_cheese.isChecked
            pref.onion = cb_onion.isChecked
            pref.garlic = cb_garlic.isChecked
            pref.egg = cb_egg.isChecked
            pref.chicken = cb_chicken.isChecked
            pref.mutton = cb_mutton.isChecked
            pref.fish = cb_fish.isChecked

            return pref
        }
    }


}
