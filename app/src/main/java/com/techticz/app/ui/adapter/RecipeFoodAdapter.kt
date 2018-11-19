package com.techticz.app.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.techticz.app.ui.customView.MealFoodView
import com.techticz.app.ui.customView.MealRecipeView
import com.techticz.app.ui.customView.RecipeFoodView
import com.techticz.app.viewmodel.FoodViewModel
import kotlinx.android.synthetic.main.meal_food_layout.view.*

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class RecipeFoodAdapter constructor(var mealRecipeView: MealRecipeView, var callBack:RecipItemCallBacks?): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is RecipeFoodViewHolder) {
            // holder.mItem = mValues.get(position);
            var foodViewModel = mealRecipeView?.recipeViewModel?.liveFoodViewModelList?.value?.data?.get(position)
            (holder as RecipeFoodViewHolder).recipeFoodView.fillDetails(foodViewModel)
            (holder as RecipeFoodViewHolder).recipeFoodView.setOnClickListener(View.OnClickListener {
             callBack?.onFoodItemClicked(foodViewModel)
            })
            if(position == itemCount -1){
               // (holder as RecipeFoodViewHolder).recipeFoodView.divider.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        if(mealRecipeView?.recipeViewModel?.liveFoodViewModelList?.value?.data == null)
            return 0;
        return mealRecipeView?.recipeViewModel?.liveFoodViewModelList?.value?.data?.size!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = RecipeFoodView(parent, mealRecipeView)
            var holder =  RecipeFoodViewHolder(view)
                return holder
    }

    class RecipeFoodViewHolder(val recipeFoodView: RecipeFoodView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(recipeFoodView) {}
    interface RecipItemCallBacks{
        fun onFoodItemClicked(item: FoodViewModel?)
    }
}