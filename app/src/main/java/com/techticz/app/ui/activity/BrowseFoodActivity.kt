package com.techticz.app.ui.activity

import android.os.Bundle
import android.app.Activity
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.constants.FoodCategories
import com.techticz.app.model.BrowseFoodResponse
import com.techticz.app.model.food.Food
import com.techticz.app.ui.adapter.BrowseFoodsAdapter
import com.techticz.app.ui.adapter.CategoryFoodAdapter
import com.techticz.app.viewmodel.BrowseFoodViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.activity_browse_food.*
import kotlinx.android.synthetic.main.content_browse_food.*


class BrowseFoodActivity : BaseDIActivity(), BrowseFoodsAdapter.FoodViewCallBacks, CategoryFoodAdapter.FoodViewCallBacks {
    override fun onFeaturedFoodViewClicked(mealFood: Food) {
        onFoodViewClicked(mealFood)
    }

    override fun onFoodViewClicked(food: Food) {
        if(TextUtils.isEmpty(plateId) && TextUtils.isEmpty(recipeId) ){

        } else {
            var data = intent
            data.putExtra("foodId", food.id)
            data.putExtra("stdServing", food.standardServing.portion)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    private lateinit var browseFoodViewModel: BrowseFoodViewModel

    private var recipeId: String? = null

    private var plateId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_food)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout

        recipeId = intent.getStringExtra("recipeId")
        plateId = intent.getStringExtra("plateId")
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
                spin_kit_browse_foods.visibility = View.VISIBLE
                return false
            }

        })

        recycler_foods.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_foods.adapter = BrowseFoodsAdapter(ArrayList(),this)

        recycler_fruits.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_fruits.adapter = CategoryFoodAdapter(ArrayList(),this)

        recycler_vegitables.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_vegitables.adapter = CategoryFoodAdapter(ArrayList(),this)

        recycler_egg_or_meat.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_egg_or_meat.adapter = CategoryFoodAdapter(ArrayList(),this)

        browseFoodViewModel.liveBrowseFoodsResponse?.observe(this, Observer {
            res->
            onFoodsDataLoaded(res)
        })

        browseFoodViewModel.liveFruitsResponse?.observe(this, Observer {
            res->
            onFruitsDataLoaded(res)
        })
        browseFoodViewModel.liveVegitablesResponse?.observe(this, Observer {
            res->
            onVegitablesDataLoaded(res)
        })
        browseFoodViewModel.liveEggsOrMeatResponse?.observe(this, Observer {
            res->
            onEggOrMeatDataLoaded(res)
        })

        browseFoodViewModel.triggerFruits.value = FoodCategories.E.id
        browseFoodViewModel.triggerVegitables.value = FoodCategories.C.id
        browseFoodViewModel.triggerEggOrMeat.value = FoodCategories.M.id

    }

    private fun onFoodsDataLoaded(res: Resource<BrowseFoodResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                spin_kit_browse_foods.visibility = View.INVISIBLE
                (recycler_foods.adapter as BrowseFoodsAdapter).foods = res?.data?.foods!!
                (recycler_foods.adapter as BrowseFoodsAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onFruitsDataLoaded(res: Resource<BrowseFoodResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                spin_kit_browse_foods.visibility = View.INVISIBLE
                (recycler_fruits.adapter as CategoryFoodAdapter).foods = res?.data?.foods!!
                (recycler_fruits.adapter as CategoryFoodAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onVegitablesDataLoaded(res: Resource<BrowseFoodResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                spin_kit_browse_foods.visibility = View.INVISIBLE
                (recycler_vegitables.adapter as CategoryFoodAdapter).foods = res?.data?.foods!!
                (recycler_vegitables.adapter as CategoryFoodAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onEggOrMeatDataLoaded(res: Resource<BrowseFoodResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                spin_kit_browse_foods.visibility = View.INVISIBLE
                (recycler_egg_or_meat.adapter as CategoryFoodAdapter).foods = res?.data?.foods!!
                (recycler_egg_or_meat.adapter as CategoryFoodAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onCreateFoodClicked() {
       // navigator.startCreateFoodScreen()
    }

}
