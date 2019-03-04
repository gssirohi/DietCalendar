package com.techticz.app.base

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.techticz.app.ui.Navigator
import com.techticz.app.viewmodel.UserViewModel
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.activity_diet_chart.*
import kotlinx.android.synthetic.main.app_bar_dashboard.view.*
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.techticz.app.repo.PrefRepo
import com.techticz.dietcalendar.ui.DietCalendarApplication


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

open class BaseDIActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var  navigator: Navigator

    @Inject
    lateinit var  dispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var baseuserViewModel: UserViewModel
    lateinit var prefRepo: PrefRepo
    private var progressDialog: ProgressDialog? = null

    var activityToolbar: Toolbar? = null
        get() = field
        set(toolbar) {
            field = toolbar
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener { view -> onBackPressed() }
            supportActionBar!!.title = toolbar?.title
        }


    var activityCoordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout? = null
    var activityCollapsingToolbar: CollapsingToolbarLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = Navigator(this)
        prefRepo = PrefRepo(this)
        baseuserViewModel = DietCalendarApplication.getAppUserViewModel()
        if(baseuserViewModel.triggerUserId.value != null && baseuserViewModel.triggerUserId.value.equals(LoginUtils.getCurrentUserId())) {

        } else {
            //first activity (Launcher ACtivity) will execute this code after that above code will be executed
            baseuserViewModel.triggerUserId.value = LoginUtils.getCurrentUserId()
            baseuserViewModel.autoLoadChildren(this)
        }


    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }
    override fun onResume() {
        super.onResume()
        try {

            val typeface = ResourcesCompat.getFont(this, R.font.dancing_script_bold)
            activityCollapsingToolbar?.setCollapsedTitleTypeface(typeface)
            activityCollapsingToolbar?.setExpandedTitleTypeface(typeface)
          //  activityCoordinatorLayout = findViewById(R.id.coordinatorLayout)
          //  activityToolbar = activityCoordinatorLayout?.findViewById(R.id.toolbar)
        } catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun addFragment(id: Int, fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
                .add(id, fragment)
                .commit()
    }

    fun addFragmentAndHoldInBackStack(id: Int, fragment: androidx.fragment.app.Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .add(id, fragment)
                .addToBackStack(tag)
                .commit()
    }

    fun replaceFragment(id: Int, fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(id, fragment)
                .commit()
    }

    fun removeFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
    }


    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> {
        return dispatchingAndroidInjector
    }

    fun showProgress(title:String,message:String) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog?.setMessage(getString(R.string.loading))
            progressDialog?.setIndeterminate(true)

        }
        progressDialog?.show()
    }

    fun showProgress(message:String) {
        showProgress("Loading..",message)
    }

    fun showProgress() {
        showProgress("Loading..","Hold on for a moment..")
    }

    fun hideProgress() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    override fun onBackPressed() {

        val count = fragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            setResult(-2)
            finish()
            //additional code
        } else {
            fragmentManager.popBackStack()
        }

    }

    fun showToast(message: String) {
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show()
    }

    fun showSuccess(s: String) {
        if(activityCoordinatorLayout != null) {
            val snackbar = Snackbar
                    .make(activityCoordinatorLayout!!, s, Snackbar.LENGTH_LONG)
            snackbar.setAction("OK", View.OnClickListener { snackbar.dismiss() })
            snackbar.setActionTextColor(Color.GREEN)
            snackbar.show()
        }
    }

    fun showError(s: String) {
        if(activityCoordinatorLayout != null) {
            val snackbar = Snackbar
                    .make(activityCoordinatorLayout!!, s, Snackbar.LENGTH_LONG)
            snackbar.setAction("OK", View.OnClickListener { snackbar.dismiss() })
            snackbar.setActionTextColor(Color.RED)
            snackbar.show()
        }
    }
}
