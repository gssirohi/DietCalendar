package com.techticz.app.ui.activity

import android.os.Bundle
import android.app.Activity
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.BrowsePlateResponse
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.ui.adapter.BrowsePlatesAdapter
import com.techticz.app.viewmodel.BrowsePlateViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status

import kotlinx.android.synthetic.main.activity_browse_plate.*
import kotlinx.android.synthetic.main.content_browse_plate.*

class BrowsePlateActivity : BaseDIActivity(), BrowsePlatesAdapter.PlateViewCallBacks {
    override fun onPlateViewClicked(mealPlate: MealPlate) {
        var data = intent
        data.putExtra("plateId",mealPlate.id)
        setResult(Activity.RESULT_OK,data)
        finish()
    }

    private lateinit var browsePlatesViewModel: BrowsePlateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_plate)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        fab.setOnClickListener { view ->
            onCreatePlateClicked()
        }
        browsePlatesViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(BrowsePlateViewModel::class.java)
        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                //Task HERE
                browsePlatesViewModel?.triggerPlateText.value  = query
                return false
            }

        })

        recycler_plates.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_plates.adapter = BrowsePlatesAdapter(ArrayList(),this)

        browsePlatesViewModel.liveBrowsePlatesResponse?.observe(this, Observer {
            res->
            onPlatesDataLoaded(res)
        })

    }

    private fun onPlatesDataLoaded(res: Resource<BrowsePlateResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                (recycler_plates.adapter as BrowsePlatesAdapter).dayMeals = res?.data?.plates!!
                (recycler_plates.adapter as BrowsePlatesAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onCreatePlateClicked() {
        navigator.startCreatePlateScreen()
    }

}
