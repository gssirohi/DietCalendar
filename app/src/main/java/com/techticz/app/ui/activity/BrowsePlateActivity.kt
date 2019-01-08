package com.techticz.app.ui.activity

import android.os.Bundle
import android.app.Activity
import android.view.View
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.constants.Meals
import com.techticz.app.model.BrowsePlateResponse
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.ui.adapter.BrowsePlatesAdapter
import com.techticz.app.ui.adapter.FeaturedPlatesAdapter
import com.techticz.app.util.Utils
import com.techticz.app.viewmodel.BrowsePlateViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status

import kotlinx.android.synthetic.main.activity_browse_plate.*
import kotlinx.android.synthetic.main.content_browse_plate.*

class BrowsePlateActivity : BaseDIActivity(), BrowsePlatesAdapter.PlateViewCallBacks,FeaturedPlatesAdapter.PlateViewCallBacks {
    override fun onPlateViewClicked(mealPlate: MealPlate) {
        var data = intent
        data.putExtra("plateId",mealPlate.id)
        setResult(Activity.RESULT_OK,data)
        finish()
    }
    override fun onFeaturedPlateViewClicked(mealPlate: MealPlate) {
        var data = intent
        data.putExtra("plateId",mealPlate.id)
        setResult(Activity.RESULT_OK,data)
        finish()
    }

    private lateinit var browsePlatesViewModel: BrowsePlateViewModel

    private var mealType: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_plate)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout
        fab.setOnClickListener { view ->
            onCreatePlateClicked()
        }
        mealType = intent.getStringExtra("mealType")
        var meal : Meals = Utils.getMealType(mealType)
        tv_plate_meal_type.text = meal.mealName
        browsePlatesViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(BrowsePlateViewModel::class.java)
        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                //Task HERE
                browsePlatesViewModel?.triggerSearchPlateText.value  = query
                spin_kit_browse_plates.visibility = View.VISIBLE
                tv_search_error.visibility = View.GONE
                return false
            }

        })

        recycler_plates.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_plates.adapter = BrowsePlatesAdapter(ArrayList(),this)

        recycler_featured_plates.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_featured_plates.adapter = FeaturedPlatesAdapter(ArrayList(),this)

        recycler_my_plates.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_my_plates.adapter = FeaturedPlatesAdapter(ArrayList(),this)

        browsePlatesViewModel.liveBrowsePlatesResponse?.observe(this, Observer {
            res->
            onPlatesDataLoaded(res)
        })

        browsePlatesViewModel.liveFeaturedPlatesResponse?.observe(this, Observer {
            res->
            onFeaturedPlatesDataLoaded(res)
        })
        browsePlatesViewModel.liveMyPlatesResponse?.observe(this, Observer {
            res->
            onMyPlatesDataLoaded(res)
        })
        browsePlatesViewModel?.triggerFeaturedPlateMealType.value  = mealType
        browsePlatesViewModel?.triggerMyPlates.value = ""
    }

    private fun onPlatesDataLoaded(res: Resource<BrowsePlateResponse>?) {
        when(res?.status){
            Status.LOADING->{spin_kit_browse_plates.visibility = View.VISIBLE}
            Status.SUCCESS->{
                spin_kit_browse_plates.visibility = View.INVISIBLE
                (recycler_plates.adapter as BrowsePlatesAdapter).dayMeals = res?.data?.plates!!
                (recycler_plates.adapter as BrowsePlatesAdapter).notifyDataSetChanged()
                if(res?.data?.plates.isNullOrEmpty()){
                    tv_search_error.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onFeaturedPlatesDataLoaded(res: Resource<BrowsePlateResponse>?) {
        when(res?.status){
            Status.LOADING->{spin_kit_featured_plates.visibility = View.VISIBLE}
            Status.SUCCESS->{
                spin_kit_featured_plates.visibility = View.INVISIBLE
                (recycler_featured_plates.adapter as FeaturedPlatesAdapter).dayMeals = res?.data?.plates!!
                (recycler_featured_plates.adapter as FeaturedPlatesAdapter).notifyDataSetChanged()
                if(res?.data?.plates.isNullOrEmpty()){
                    tv_search_error.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onMyPlatesDataLoaded(res: Resource<BrowsePlateResponse>?) {
        when(res?.status){
            Status.LOADING->{spin_kit_my_plates.visibility = View.VISIBLE}
            Status.SUCCESS->{
                spin_kit_my_plates.visibility = View.INVISIBLE
                (recycler_my_plates.adapter as FeaturedPlatesAdapter).dayMeals = res?.data?.plates!!
                (recycler_my_plates.adapter as FeaturedPlatesAdapter).notifyDataSetChanged()
                if(res?.data?.plates.isNullOrEmpty()){
                    tv_search_error.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onCreatePlateClicked() {
        navigator.startCreatePlateScreen()
    }

}
