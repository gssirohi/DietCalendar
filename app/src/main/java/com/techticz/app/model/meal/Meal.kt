package com.techticz.app.model.meal

import com.techticz.app.constants.Meals
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.viewmodel.BrowseDietPlanViewModel
import com.techticz.app.viewmodel.MealPlateViewModel

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
open class Meal {
    var mealPlateId: String? = null
    var mealPlate: MealPlate? = null
    var mealType: Meals

    constructor(mealType: Meals, mealPlateId: String?) {
        this.mealPlateId = mealPlateId
        this.mealType = mealType
    }
}