package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.ui.activity.RecipeDetailsActivity
import com.techticz.app.ui.customView.RecipeDetailsFoodView
import com.techticz.app.viewmodel.FoodViewModel
import kotlinx.android.synthetic.main.meal_food_layout.view.*

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class RecipeFoodAdapter constructor(var recipeActivity: RecipeDetailsActivity, var callBack:RecipItemCallBacks?): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is MealFoodViewHolder) {
            // holder.mItem = mValues.get(position);
            var foodViewModel = recipeActivity?.recipeViewModel?.liveFoodViewModelList?.value?.data?.get(position)
            (holder as MealFoodViewHolder).recipeDetailsFoodView.fillDetails(foodViewModel)
            (holder as MealFoodViewHolder).recipeDetailsFoodView.setOnClickListener(View.OnClickListener {
             callBack?.onFoodItemClicked(foodViewModel)
            })
            if(position == itemCount -1){
                (holder as MealFoodViewHolder).recipeDetailsFoodView.divider.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        if(recipeActivity?.recipeViewModel?.liveFoodViewModelList?.value?.data == null)
            return 0;
        return recipeActivity?.recipeViewModel?.liveFoodViewModelList?.value?.data?.size!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = RecipeDetailsFoodView(parent,recipeActivity)
            var holder =  MealFoodViewHolder(view)
                return holder
    }

    class MealFoodViewHolder(val recipeDetailsFoodView: RecipeDetailsFoodView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(recipeDetailsFoodView) {}
    interface RecipItemCallBacks{
        fun onFoodItemClicked(item: FoodViewModel?)
    }
}