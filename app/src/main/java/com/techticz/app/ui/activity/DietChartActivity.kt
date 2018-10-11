package com.techticz.app.ui.activity

import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.ui.adapter.DietChartPagerAdapter

import com.techticz.dietcalendar.R
import com.techticz.powerkit.base.BaseDIActivity
import kotlinx.android.synthetic.main.activity_diet_chart.*
import kotlinx.android.synthetic.main.fragment_diet_chart.view.*
import org.parceler.Parcels
import com.techticz.app.model.User
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.repo.FoodRepository
import com.techticz.app.repo.MealPlateRepository
import com.techticz.app.repo.RecipeRepository
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.dietcalendar.viewmodel.LauncherViewModel
import javax.inject.Inject


class DietChartActivity : BaseDIActivity() {
    @Inject
    lateinit var mealPlateRepo: MealPlateRepository
    @Inject
    lateinit var recipeRepo: RecipeRepository
    @Inject
    lateinit var foodRepo: FoodRepository


    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: DietChartPagerAdapter? = null
    lateinit var dietPlan: DietPlan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_chart)

        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout

        initData()
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = DietChartPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        container.setOffscreenPageLimit(6);
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }


    private fun initData() {
        dietPlan = Parcels.unwrap<Any>(intent.getParcelableExtra("plan")) as DietPlan
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_diet_chart, menu)
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


}
