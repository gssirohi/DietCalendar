package com.techticz.app.ui.activity

import android.os.Bundle
import android.app.Activity
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.BrowseFoodResponse
import com.techticz.app.model.food.Food
import com.techticz.app.ui.adapter.BrowseFoodsAdapter
import com.techticz.app.viewmodel.BrowseFoodViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.activity_browse_food.*
import kotlinx.android.synthetic.main.content_browse_food.*


class BrowseFoodActivity : BaseDIActivity(), BrowseFoodsAdapter.FoodViewCallBacks {
    override fun onFoodViewClicked(food: Food) {
        var data = intent
        data.putExtra("foodId",food.id)
        setResult(Activity.RESULT_OK,data)
        finish()
    }

    private lateinit var browseFoodViewModel: BrowseFoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_food)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        fab.setOnClickListener { view ->
            onCreateFoodClicked()
        }
        browseFoodViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(BrowseFoodViewModel::class.java)
        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                //Task HERE
                browseFoodViewModel?.triggerFoodText.value  = query
                return false
            }

        })

        recycler_foods.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_foods.adapter = BrowseFoodsAdapter(ArrayList(),this)

        browseFoodViewModel.liveBrowseFoodsResponse?.observe(this, Observer {
            res->
            onFoodsDataLoaded(res)
        })

    }

    private fun onFoodsDataLoaded(res: Resource<BrowseFoodResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                (recycler_foods.adapter as BrowseFoodsAdapter).dayMeals = res?.data?.foods!!
                (recycler_foods.adapter as BrowseFoodsAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onCreateFoodClicked() {
       // navigator.startCreateFoodScreen()
    }

}
