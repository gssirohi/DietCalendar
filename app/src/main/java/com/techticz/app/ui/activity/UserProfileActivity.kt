package com.techticz.app.ui.activity

import android.arch.lifecycle.Observer
import android.support.design.widget.Snackbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.UserResponse
import com.techticz.app.model.user.BasicInfo
import com.techticz.app.model.user.HealthProfile
import com.techticz.app.model.user.MealPref
import com.techticz.app.model.user.User
import com.techticz.app.repo.UserRepository
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.auth.utils.LoginUtils

import com.techticz.dietcalendar.R
import com.techticz.dietcalendar.ui.DietCalendarApplication
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile_basic_details.*
import kotlinx.android.synthetic.main.fragment_user_profile_health_details.*
import kotlinx.android.synthetic.main.fragment_user_profile_meal_prefs.*
import timber.log.Timber

class UserProfileActivity : BaseDIActivity(), UserRepository.UserProfileCallback {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var isEditModeOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        initUI()
        baseuserViewModel.liveUserResponse.observe(this, Observer { res->onUserLoaded(res) })

    }

    private fun validateSectionData(currentItem: Int): String? {
        when(currentItem+1) {
            1->return ((container.adapter as SectionsPagerAdapter).basicDetailsFrag)?.validateData()
            2->return (container.adapter as SectionsPagerAdapter).healthDetailsFrag?.validateData()
            3->return (container.adapter as SectionsPagerAdapter).mealPrefsFrag?.validateData()
            else-> return null
        }
    }


    private fun proceedClicked(view: View) {
        Snackbar.make(view, "Proceed button clicked", Snackbar.LENGTH_LONG)
                .setAction("OK", null).show()
        if(isEditModeOn) {
            var newUser = User(LoginUtils.getCurrentUserId())
            newUser.basicInfo = (container.adapter as SectionsPagerAdapter).basicDetailsFrag?.getBasicDetails()
            newUser.healthProfile = (container.adapter as SectionsPagerAdapter).healthDetailsFrag?.getHealthProfile()
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
        when(res?.status){
            Status.SUCCESS->{
                if(isNewUser) {
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
            Status.EMPTY->{
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

        container?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                if(position > 0 ){
                    (fab_backward.parent as View).visibility = View.VISIBLE
                } else {
                    (fab_backward.parent as View).visibility = View.GONE
                }
                if(position == 2){
                    //last pos selected
                    fab_forward.setImageResource(R.drawable.ic_check)
                } else {
                    fab_forward.setImageResource(R.drawable.ic_arrow_forward)
                }
            }

        })
        fab_forward.setOnClickListener { view ->
            var validationResult = validateSectionData(container.currentItem)
            if(TextUtils.isEmpty(validationResult)) {
                if(container.currentItem < 2) {
                    container.setCurrentItem(container.currentItem + 1, true)
                } else if(container.currentItem == 2){
                    proceedClicked(view)
                }
            } else {
                showError(view,validationResult)
            }

        }
        fab_backward.setOnClickListener { view ->
            if(container.currentItem > 0) {
                container.setCurrentItem(container.currentItem -1, true)
            }

        }

        fab_edit.visibility = View.VISIBLE
        fab_edit.setOnClickListener({view->
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

    override fun onRegistered(userId:String) {
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
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var basicDetailsFrag: BasicDetailsFragment? = null
        var healthDetailsFrag: HealthDetailsFragment? = null
        var mealPrefsFrag: MealPrefFragment? = null

        override fun getItem(position: Int): Fragment {
            when(position+1){
                1-> {
                    if (basicDetailsFrag == null) {
                        basicDetailsFrag = BasicDetailsFragment.newInstance(1)
                    }
                    return basicDetailsFrag!!;
                }
                2-> {
                    if (healthDetailsFrag == null) {
                        healthDetailsFrag = HealthDetailsFragment.newInstance(2)
                    }
                    return healthDetailsFrag!!;
                }
                3-> {
                    if (mealPrefsFrag == null) {
                        mealPrefsFrag = MealPrefFragment.newInstance(3)
                    }
                    return mealPrefsFrag!!;
                }
                else->{return mealPrefsFrag!!;}
            }

        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    class BasicDetailsFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            var rootView:View?  = inflater.inflate(R.layout.fragment_user_profile_basic_details, container, false)
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
            when(user?.basicInfo?.gender){
                "male"->rb_male.setChecked(true)
                "female"->rb_female.setChecked(true)
            }
            (activity as BaseDIActivity).baseuserViewModel.liveImage?.observe(activity as UserProfileActivity, Observer { res-> onImageModelLoaded(res) })
            (activity as BaseDIActivity).baseuserViewModel.autoLoadChildren(activity as UserProfileActivity)
        }

        private fun onImageModelLoaded(res: Resource<ImageViewModel>?) {
            when(res?.status){
                Status.LOADING-> aiv_user.setImageViewModel(res?.data,activity as UserProfileActivity)
                Status.SUCCESS-> aiv_user.setImageViewModel(res?.data,activity as UserProfileActivity)
            }
        }

        fun validateData(): String? {
            if(TextUtils.isEmpty(til_name.editText?.text)){
                return "Please enter valid Name!"
            }
            if(TextUtils.isEmpty(til_dob.editText?.text)){
                return "Please enter valid Date Of Birth!"
            }
            if(!(rb_male.isChecked || rb_female.isChecked)){
                return "Please select Gender!"
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
            if(rb_male.isChecked){
                basicInfo.gender = "male"
            } else {
                basicInfo.gender = "female"
            }
            basicInfo.dob = til_dob.editText?.text.toString()
            return basicInfo
        }

        fun updateEditability() {
            var b = (activity as UserProfileActivity).isEditModeOn
            Timber.d("making basic profile editable:"+b)
           // (til_dob.editText as EditText).isFocusable = b
            (til_dob.editText as EditText).isEnabled = b
            (til_dob.editText as EditText).isClickable = b

            rb_male.isFocusable = b
            rb_male.isEnabled = b
            rb_male.isClickable = b

            rb_female.isFocusable = b
            rb_female.isEnabled = b
            rb_female.isClickable = b
        }
    }



    class HealthDetailsFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            var rootView:View?  = inflater.inflate(R.layout.fragment_user_profile_health_details, container, false)
            return rootView
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initUI();
            updateEditability()
        }
        fun initUI() {
            Timber.d("Updating health profile UI")
            var user =(activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user
            til_height.editText?.setText(""+user?.healthProfile?.height)
            til_weight.editText?.setText(""+user?.healthProfile?.weight)
            til_target_weight.editText?.setText(""+user?.healthProfile?.targetWeight)
            when(user?.healthProfile?.activityLevel){
                "low"->rb_low.setChecked(true)
                "moderate"->rb_moderate.setChecked(true)
                "high"->rb_high.setChecked(true)
                "extreme"->rb_extreme.setChecked(true)
            }
            if(user?.healthProfile?.drink != null)
            checkbox_drink.isChecked = user?.healthProfile?.drink!!

        }
        fun updateEditability() {
            var b = (activity as UserProfileActivity).isEditModeOn
            Timber.d("making health profile editable:"+b)
         //   (til_height.editText as EditText).isFocusable = b
            (til_height.editText as EditText).isEnabled = b
            (til_height.editText as EditText).isClickable = b

        //    (til_weight.editText as EditText).isFocusable = b
            (til_weight.editText as EditText).isEnabled = b
            (til_weight.editText as EditText).isClickable = b

          //  (til_target_weight.editText as EditText).isFocusable = b
            (til_target_weight.editText as EditText).isEnabled = b
            (til_target_weight.editText as EditText).isClickable = b

         //   rb_low.isFocusable = b
            rb_low.isEnabled = b
            rb_low.isClickable = b

          //  rb_moderate.isFocusable = b
            rb_moderate.isEnabled = b
            rb_moderate.isClickable = b

          //  rb_high.isFocusable = b
            rb_high.isEnabled = b
            rb_high.isClickable = b

          //  rb_extreme.isFocusable = b
            rb_extreme.isEnabled = b
            rb_extreme.isClickable = b

           // checkbox_drink.isFocusable = b
            checkbox_drink.isEnabled = b
            checkbox_drink.isClickable = b

        }

        fun validateData(): String? {
            if(TextUtils.isEmpty(til_height.editText?.text)){
                return "Please enter valid Height!"
            }
            if(TextUtils.isEmpty(til_weight.editText?.text)){
                return "Please enter valid Weight!"
            }
            if(TextUtils.isEmpty(til_target_weight.editText?.text)){
                return "Please enter valid Target Weight!"
            }
            if(!(rb_low.isChecked || rb_moderate.isChecked || rb_high.isChecked || rb_extreme.isChecked)){
                return "Please select Activity Level!"
            }
            return null;
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): HealthDetailsFragment {
                val fragment = HealthDetailsFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

        fun getHealthProfile(): HealthProfile? {
            var health = HealthProfile()
            health.height = til_height.editText?.text.toString().toFloat()
            health.weight = til_weight.editText?.text.toString().toFloat()
            health.targetWeight = til_target_weight.editText?.text.toString().toFloat()
            if(rb_low.isChecked){
                health.activityLevel = "low"
            } else if(rb_moderate.isChecked){
                health.activityLevel = "moderate"
            } else if(rb_high.isChecked){
                health.activityLevel = "high"
            } else if(rb_extreme.isChecked){
                health.activityLevel = "extreme"
            }
            health.drink = checkbox_drink.isChecked
            health.smoke = false

            return health

        }
    }

    class MealPrefFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            var rootView:View?  = inflater.inflate(R.layout.fragment_user_profile_meal_prefs, container, false)
            return rootView
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initUI();
            updateEditability()
        }
        fun initUI() {
            Timber.d("Updating meal pref UI")
            var user = (activity as BaseDIActivity).baseuserViewModel.liveUserResponse?.value?.data?.user
            if(user?.mealPref?.cheese != null)
            cb_cheese.isChecked = user?.mealPref?.cheese!!
            else cb_cheese.isChecked = true

            if(user?.mealPref?.milk != null)
                cb_milk.isChecked = user?.mealPref?.milk!!
            else cb_milk.isChecked = true

            if(user?.mealPref?.onion != null)
                cb_onion.isChecked = user?.mealPref?.onion!!
            else cb_onion.isChecked = true


            if(user?.mealPref?.garlic != null)
                cb_garlic.isChecked = user?.mealPref?.garlic!!
            else cb_garlic.isChecked = true

            if(user?.mealPref?.egg != null)
                cb_egg.isChecked = user?.mealPref?.egg!!
            else cb_egg.isChecked = true


            if(user?.mealPref?.chicken != null)
                cb_chicken.isChecked = user?.mealPref?.chicken!!
            else cb_chicken.isChecked = true

            if(user?.mealPref?.fish != null)
                cb_fish.isChecked = user?.mealPref?.fish!!
            else cb_fish.isChecked = true

            if(user?.mealPref?.mutton != null)
                cb_mutton.isChecked = user?.mealPref?.mutton!!
            else cb_mutton.isChecked = true

            if((activity as UserProfileActivity).isNewUser){
                (cb_terms_conditions.parent as View).visibility = View.VISIBLE
            } else {
                (cb_terms_conditions.parent as View).visibility = View.GONE
            }
        }

        fun updateEditability() {
            var b = (activity as UserProfileActivity).isEditModeOn
            Timber.d("making meal pref profile editable:"+b)
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
            if((activity as UserProfileActivity).isNewUser && !cb_terms_conditions.isChecked){
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
