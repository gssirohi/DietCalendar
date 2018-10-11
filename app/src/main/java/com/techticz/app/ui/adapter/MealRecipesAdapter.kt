package com.techticz.app.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.techticz.app.ui.customView.MealRecipeView
import com.techticz.app.ui.customView.MealView
import com.techticz.app.viewmodel.RecipeViewModel
import kotlinx.android.synthetic.main.meal_recipe_layout.view.*

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class MealRecipesAdapter constructor(var mealView:MealView, var callBack:RecipItemCallBacks?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is MealRecipeViewHolder) {
            // holder.mItem = mValues.get(position);
            var recipeViewModel = mealView.mealPlateViewModel?.liveRecipeViewModelList?.value?.data?.get(position)
            (holder as MealRecipeViewHolder).mealRecipeView.fillDetails(recipeViewModel)
            (holder as MealRecipeViewHolder).mealRecipeView.setOnClickListener(View.OnClickListener {
             callBack?.onRecipeItemClicked(recipeViewModel)
            })
            if(position == itemCount -1){
                (holder as MealRecipeViewHolder).mealRecipeView.divider.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        if(mealView.mealPlateViewModel?.liveRecipeViewModelList?.value?.data == null)
            return 0;
        return mealView.mealPlateViewModel?.liveRecipeViewModelList?.value?.data?.size!!
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = MealRecipeView(mealView,parent)
            var holder =  MealRecipeViewHolder(view)

        return holder
    }

    class MealRecipeViewHolder(val mealRecipeView: MealRecipeView) : RecyclerView.ViewHolder(mealRecipeView) {}
    interface RecipItemCallBacks{
        fun onRecipeItemClicked(item: RecipeViewModel?)
    }
}