package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.ui.customView.MealView
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.meal_recipe_list_item_view.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class RecipesAdapter (var dayMeals: List<Recipe>, var callBack: RecipeViewCallBacks): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is RecipeViewHolder) {
            // holder.mItem = mValues.get(position);

            (holder as RecipeViewHolder).recipeView.tv_recipe_cal.text = ""+dayMeals.get(position)?.basicInfo?.perServingCalories
            (holder as RecipeViewHolder).recipeView.tv_recipe_name.text = ""+dayMeals.get(position)?.basicInfo?.name?.english
            (holder as RecipeViewHolder).recipeView.tv_recipe_desc.text = ""+dayMeals.get(position)?.basicInfo?.desc
            (holder as RecipeViewHolder).recipeView.setOnClickListener(View.OnClickListener {
                if (null != callBack) {
                    callBack.onRecipeViewClicked(dayMeals.get(position))
                }
            })
        }
    }

/*    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is RecipeViewHolder) {
            holder!!.setIsRecyclable(false)
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder is RecipeViewHolder) {
            holder!!.setIsRecyclable(true)
        }
        super.onViewDetachedFromWindow(holder)
    }*/
    override fun getItemCount(): Int {
        return dayMeals?.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(parent.context, R.layout.meal_recipe_list_item_view,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = RecipeViewHolder(view)
            return holder

    }

    class RecipeViewHolder(val recipeView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(recipeView) {}

    interface RecipeViewCallBacks{
        fun onRecipeViewClicked(recipe:Recipe)
    }
}