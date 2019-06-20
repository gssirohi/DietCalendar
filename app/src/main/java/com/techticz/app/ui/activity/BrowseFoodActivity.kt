package com.techticz.app.ui.activity

import android.os.Bundle
import android.app.Activity
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
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

    override fun getFoodNutrient(food: Food): Float {
        return when(tv_select_nutrient.text.toString().toUpperCase()){
            "ENERGY"-> food.nutrition.nutrients.principlesAndDietaryFibers.energy
            "PROTIEN"->food.nutrition.nutrients.principlesAndDietaryFibers.protien
            "FAT"->food.nutrition.nutrients.principlesAndDietaryFibers.fat
            "CARBOHYDRATE"->food.nutrition.nutrients.principlesAndDietaryFibers.carbohydrate
            "TOTALB6"->food.nutrition.nutrients.waterSolubleVitamins.totalB6
            "BIOINB7"->food.nutrition.nutrients.waterSolubleVitamins.bioinB7
            "TOTALFOLATESB9"->food.nutrition.nutrients.waterSolubleVitamins.totalFolatesB9
            "IRON"->food.nutrition.nutrients.mineralsAndTraceElements.iron
            "CALCIUM"->food.nutrition.nutrients.mineralsAndTraceElements.calcium
            "COPPER"->food.nutrition.nutrients.mineralsAndTraceElements.copper
            "ZINC"->food.nutrition.nutrients.mineralsAndTraceElements.zinc
            "POTASSIUM"->food.nutrition.nutrients.mineralsAndTraceElements.potassium
            "TOTAL"->food.nutrition.nutrients.principlesAndDietaryFibers.dietaryFiber.total
            else->0f
        }
    }

    override fun onFoodViewClicked(food: Food) {
        if(TextUtils.isEmpty(plateId) && TextUtils.isEmpty(recipeId) ){
            //navigator.startFoodDetails()
        } else {
            var data = intent
            data.putExtra("foodId", food.id)
            data.putExtra("popularServingQty", food.standardServing.popularServing)
            data.putExtra("popularServingType", food.standardServing.popularServingType)
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

        tv_select_nutrient.setOnClickListener { MaterialDialog(this).show {
            listItemsSingleChoice(items =listOf("energy","carbohydrate","fat","protien",
                    "thiamineB1","riboflavinB2","niacinB3","pentothenicAcidB5","totalB6","bioinB7","totalFolatesB9","totalAscorbicAcid",
                    "aluminium","calcium","copper","iron","magnesium","potassium","sodium","zinc",
                    "total",
                    "starch","glucose","sucrose","maltos","totalFreeSugar","cho",
                    "totalSaturatedFatyAcids","totalMonoUnsaturatedFattyAcids","totalPolyUnsaturatedFattyAcids","cholesterol",
                    "citricAcid","mallicAcid",
                    "totalCarotenids"
            )) { dialog, index, text ->
                onCategorySelected(text)
            }
        } }
        tv_select_nutrient.setText("Select Nutrient ->")
        browseFoodViewModel.triggerFruits.value = FoodCategories.E.id
        browseFoodViewModel.triggerVegitables.value = FoodCategories.C.id


    }

    private fun onCategorySelected(text: String) {
        tv_select_nutrient.text = ""+text
        browseFoodViewModel.triggerEggOrMeat.value = text
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
