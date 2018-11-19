package com.techticz.app.ui.activity

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.techticz.app.repo.DeveloperRepository
import com.techticz.dietcalendar.R
import com.techticz.app.base.BaseDIActivity

import kotlinx.android.synthetic.main.activity_developer.*
import kotlinx.android.synthetic.main.content_developer.*
import javax.inject.Inject

class DeveloperActivity : BaseDIActivity(), View.OnClickListener {


    @Inject
    lateinit var developerRepo:DeveloperRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)
        activityCoordinatorLayout = coordinatorLayout
        activityToolbar = toolbar

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        bt_all.setOnClickListener(this)
        bt_foods.setOnClickListener(this)
        bt_recipes.setOnClickListener(this)
        bt_plates.setOnClickListener(this)
        bt_plans.setOnClickListener(this)
        bt_food_categories.setOnClickListener(this)
        bt_recipe_categories.setOnClickListener(this)
        bt_serving_types.setOnClickListener(this)
        bt_nutrient_units.setOnClickListener(this)
        bt_units.setOnClickListener(this)

        developerRepo.setHostContext(this)
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.bt_all-> developerRepo.uploadAll()
            R.id.bt_foods->developerRepo.uploadFoods()
            R.id.bt_recipes->developerRepo.uploadRecipes()
            R.id.bt_plates->developerRepo.uploadPlates()
            R.id.bt_plans->developerRepo.uploadPlans()
            R.id.bt_food_categories->developerRepo.uploadFoodCategories()
            R.id.bt_recipe_categories->developerRepo.uploadRecipeCaegories()
            R.id.bt_serving_types->developerRepo.uploadServingTypes()
            R.id.bt_nutrient_units->developerRepo.uploadNutrientsUnit()
            R.id.bt_units->developerRepo.uploadUnits()
        }
    }

}
