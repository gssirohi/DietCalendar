package com.techticz.app.base

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import android.support.design.widget.Snackbar
import android.view.View
import com.techticz.app.ui.Navigator
import com.techticz.app.viewmodel.UserViewModel
import com.techticz.auth.utils.LoginUtils
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.app_bar_dashboard.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

open class BaseDIActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var  navigator: Navigator

    @Inject
    lateinit var  dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var baseuserViewModel: UserViewModel
    private var progressDialog: MaterialDialog? = null

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


    var activityCoordinatorLayout: CoordinatorLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseuserViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(UserViewModel::class.java)
        baseuserViewModel.triggerUserId.value = LoginUtils.getCurrentUserId()
        baseuserViewModel.autoLoadChildren(this)
    }
    override fun onResume() {
        super.onResume()
        try {
          //  activityCoordinatorLayout = findViewById(R.id.coordinatorLayout)
          //  activityToolbar = activityCoordinatorLayout?.findViewById(R.id.toolbar)
        } catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun addFragment(id: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(id, fragment)
                .commit()
    }

    fun addFragmentAndHoldInBackStack(id: Int, fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .add(id, fragment)
                .addToBackStack(tag)
                .commit()
    }

    fun replaceFragment(id: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(id, fragment)
                .commit()
    }

    fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    fun showProgress() {
        if (progressDialog == null) {
            progressDialog = MaterialDialog.Builder(this)
                    .title("Loading..")
                    .content("Hold on for a moment")
                    .progress(true, 0).build()
        }
        progressDialog!!.show()
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