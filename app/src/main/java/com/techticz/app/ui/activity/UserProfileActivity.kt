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
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.widget.DatePicker
import com.google.android.material.chip.Chip
import com.google.gson.Gson
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

    private var mUser: User? = null

    private fun initData() {
        var json = Gson().toJson(baseuserViewModel.liveUserResponse?.value?.data?.user)
        mUser = Gson().fromJson(json,User::class.java)

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
            showProgress()
            if (isNewUser) {
                baseuserViewModel.registerUser(mUser!!, this)
            } else {
                mUser?.activePlan = baseuserViewModel.liveUserResponse?.value?.data?.user?.activePlan
                mUser?.access = baseuserViewModel.liveUserResponse?.value?.data?.user?.access
                baseuserViewModel.updateUser(mUser, this)
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
                    initData()
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
                mUser = User(LoginUtils.getCurrentUserId())
                mUser?.basicInfo?.name = LoginUtils.getUserName()
                mUser?.basicInfo?.credential = LoginUtils.getUserCredential()
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
            til_name.editText?.setText(user()?.basicInfo?.name)
            val watcher = object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    user()?.basicInfo?.name = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            }
            til_name.editText?.addTextChangedListener(watcher)
            til_credential.editText?.setText(user()?.basicInfo?.credential)
            til_dob.editText?.setText(user()?.basicInfo?.dob)
            til_dob.editText?.setOnClickListener { showDatePickerDialog()}
            radio_group_gender.setOnCheckedChangeListener{chipGroup, i -> {
                when(i){
                    R.id.rb_male->{user()?.basicInfo?.gender = "male"}
                    R.id.rb_female->{user()?.basicInfo?.gender = "female"}
                }
            } }
            when (user()?.basicInfo?.gender) {
                "male" -> rb_male.setChecked(true)
                "female" -> rb_female.setChecked(true)
            }

            Timber.d("Updating health profile UI")
            //var user =(activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user

            user()?.healthProfile?.height?.let {
                tv_height?.text = "" + it + " CMs"
                sb_height?.progress = it.toInt()
            }
            user()?.healthProfile?.weight?.let {
                tv_weight?.text = "" + it + " KGs"
                sb_weight?.progress = it.toInt()
            }

            radio_group_activity_level.setOnCheckedChangeListener{chipGroup, i -> {
                when(i){
                    R.id.rb_low->{user()?.healthProfile?.activityLevel = "low"}
                    R.id.rb_moderate->{user()?.healthProfile?.activityLevel = "moderate"}
                    R.id.rb_high->{user()?.healthProfile?.activityLevel = "high"}
                    R.id.rb_extreme->{user()?.healthProfile?.activityLevel = "extreme"}
                }
            } }
            when (user()?.healthProfile?.activityLevel) {
                "low" -> rb_low.setChecked(true)
                "moderate" -> rb_moderate.setChecked(true)
                "high" -> rb_high.setChecked(true)
                "extreme" -> rb_extreme.setChecked(true)
            }
            radio_group_drinking_habit.setOnCheckedChangeListener{chipGroup, i -> {
                when(i){
                    R.id.checkbox_no_drink->{user()?.healthProfile?.drink = "low"}
                    R.id.checkbox_moderate_drink->{user()?.healthProfile?.drink = "moderate"}
                    R.id.checkbox_high_drink->{user()?.healthProfile?.drink = "high"}
                }
            } }
            if (user()?.healthProfile?.drink != null)
                when (user()?.healthProfile?.drink) {
                    "low" -> checkbox_no_drink.isChecked = true
                    "moderate" -> checkbox_moderate_drink.isChecked = true
                    "high" -> checkbox_high_drink.isChecked = true
                }
            radio_group_smoking_habit.setOnCheckedChangeListener{chipGroup, i -> {
                when(i){
                    R.id.checkbox_smoke->{user()?.healthProfile?.smoke = true}
                    R.id.checkbox_no_smoke->{user()?.healthProfile?.smoke = false}
                }
            } }
            user()?.healthProfile?.smoke?.let {
                checkbox_smoke.isChecked = it
                checkbox_no_smoke.isChecked = !it
            }

            sb_height?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    user()?.healthProfile?.height = progress.toFloat()
                    tv_height?.text = "" + progress + " CMs"
                }
            })
            sb_weight?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    user()?.healthProfile?.weight = progress.toFloat()
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
                calendar.set(Calendar.YEAR,1990)
            }
            val datePickerDialog = DatePickerDialog(
                    context, object:DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    til_dob.editText?.setText(""+dayOfMonth+"-"+(month+1)+"-"+year)
                    user()?.basicInfo?.dob = ""+dayOfMonth+"-"+(month+1)+"-"+year
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

        fun user():User?{
            return (activity as UserProfileActivity).mUser
        }
        fun validateData(): String? {
            if (TextUtils.isEmpty(user()?.basicInfo?.name)) {
                return "Please enter valid Name!"
            }
            if (TextUtils.isEmpty(user()?.basicInfo?.dob)) {
                return "Please enter valid Date Of Birth!"
            }
            if (TextUtils.isEmpty(user()?.basicInfo?.gender)) {
                return "Please select Gender!"
            }

            if (user()?.healthProfile?.height == null && user()?.healthProfile?.height!! <= 0f) {
                return "Please enter valid Height!"
            }
            if (user()?.healthProfile?.weight == null && user()?.healthProfile?.weight!! <= 0f) {
                return "Please enter valid Weight!"
            }

            if (TextUtils.isEmpty(user()?.healthProfile?.activityLevel)) {
                return "Please select Activity Level!"
            }

            if (TextUtils.isEmpty(user()?.healthProfile?.drink)) {
                return "Please mention drinking habit!"
            }

            if (user()?.healthProfile?.smoke == null) {
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
        fun user():User?{
            return (activity as UserProfileActivity).mUser
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


        fun initUI() {

            Timber.d("Updating health profile UI")
            if(user()?.goal?.goalType == null){
                user()?.goal?.goalType = "lose weight"
            }
            if(user()?.goal?.targetWeight == null){
                user()?.goal?.targetWeight = user()?.healthProfile?.weight
            }
            if(user()?.goal?.durationInWeek == null){
                user()?.goal?.durationInWeek = 24
            }
            updateBMICard()
            user()?.goal?.targetWeight?.let {
                tv_target_weight?.text = "" + it
                sb_target_weight?.progress = it.toInt()
            }
            user()?.goal?.durationInWeek?.let {
                tv_duration?.text = "" + it
                sb_duration?.progress = it.toInt()
            }
            when(user()?.goal?.goalType?.toLowerCase()){
                "gain weight"->{radio_group_goal.check(R.id.rb_gain_weight)}
                "lose weight"->{radio_group_goal.check(R.id.rb_lose_weight)}
                "stay energetic"->{radio_group_goal.check(R.id.rb_energetic)}
            }
            radio_group_goal.setOnCheckedChangeListener(object:ChipGroup.OnCheckedChangeListener{
                override fun onCheckedChanged(chipGroup: ChipGroup?, p1: Int) {
                    when(chipGroup?.checkedChipId) {
                        null-> ll_target_container.visibility = View.VISIBLE
                        R.id.rb_gain_weight ->{
                            ll_target_container.visibility = View.VISIBLE
                            user()?.goal?.goalType = "gain weight"
                        }
                        R.id.rb_lose_weight ->{
                            ll_target_container.visibility = View.VISIBLE
                            user()?.goal?.goalType = "lose weight"
                        }
                        R.id.rb_energetic ->{
                            ll_target_container.visibility = View.VISIBLE
                            user()?.goal?.goalType = "stay energetic"
                        }
                    }

                }

            })
            sb_target_weight?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    tv_target_weight?.text = "" + progress + " KGs"
                    user()?.goal?.targetWeight = progress.toFloat()
                    tv_daily_required_calories.text = ""+user()?.dailyRequiredCaloriesAsPerGoal+" KCAL"
                }
            })
            sb_duration?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    tv_duration?.text = "" + progress + " Weeks"
                    user()?.goal?.durationInWeek = progress
                    tv_daily_required_calories.text = ""+user()?.dailyRequiredCaloriesAsPerGoal+" KCAL"

                }
            })

        }

        private fun updateBMICard() {
            var bmi = user()?.bmi
            var idealWeight = user()?.idealWeight
            tv_bmi.text = ""+bmi
            tv_weight_comment.text = getBMIComment(bmi)
            tv_weight_recommendation.text = "Recommondation: your ideal weight should be "+idealWeight+" KGs"

            user()?.goal?.targetWeight?.let {
                tv_target_weight.text = "" + it + " KGs"
                sb_target_weight.progress = it.toInt()
            }

            user()?.goal?.durationInWeek?.let {
                tv_duration.text = ""+it+" Weeks"
                sb_duration.progress = it.toInt()
            }


                tv_daily_required_calories.text = ""+user()?.dailyRequiredCaloriesAsPerGoal+" KCAL"
                tv_daily_maintain_calories.text =""+user()?.dailyCaloriesToMaintainWeight +" KCAL"
            }

         fun getBMIComment(bmi: Float?): String {
            bmi?.let {
                if(it < 18.5f){
                    return "You are Underweight"
                } else if(it >= 18.5 && it < 24.9) {
                    return "Your BMI is Normal"
                } else if(it >= 25 && it <=29.9) {
                    return "You are Overweight"
                } else if(it >=  30){
                    return "You are Obese"
                }
            }
            return "";

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

    }

    class MealPrefFragment : androidx.fragment.app.Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            var rootView: View? = inflater.inflate(R.layout.fragment_user_profile_meal_prefs, container, false)
            return rootView
        }
        fun user():User?{
            return (activity as UserProfileActivity).mUser
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

            var dailyRequiredCalory = user()?.dailyRequiredCaloriesAsPerGoal
            dailyRequiredCalory?.let {
                tv_calory_dist_total.text = "" + (it).toInt() +" KCAL"
                tv_calory_dist_early_morning.text = "Early Morning - " + (it * 0.10f).toInt() + " KCAL"
                tv_calory_dist_breakfast.text = "Breakfast - " + (it * 0.30f).toInt() + " KCAL"
                tv_calory_dist_lunch.text = "Lunch - " + (it * 0.20f).toInt() + " KCAL"
                tv_calory_dist_snacks.text = "Evening Snacks - " + (it * 0.10f).toInt()+ " KCAL"
                tv_calory_dist_dinner.text = "Dinner - " + (it * 0.20f).toInt() + " KCAL"
                tv_calory_dist_bed_time.text = "Bed Time - " + (it * 0.10f).toInt()+ " KCAL"

                var nutritionRDA = Nutrition()
                nutritionRDA.nutrients = Nutrients()
                nutritionRDA.nutrients.principlesAndDietaryFibers?.energy = it * 4.18f
                card_nutri_details.removeAllViews()
                card_nutri_details.addView(NutritionDetailsView(view as ViewGroup, "Recommended Dietry Allowance", "Amount", "RDA", nutritionRDA, nutritionRDA, NutritionDetailsView.MODE_RDA))
            }

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
