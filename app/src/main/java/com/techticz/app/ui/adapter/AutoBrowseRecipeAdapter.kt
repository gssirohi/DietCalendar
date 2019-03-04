package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.ui.activity.BrowseRecipeActivity
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.recipe_featured_list_item_view.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class AutoBrowseRecipeAdapter (var recipes: List<Recipe>, var callBack: RecipeViewCallBacks): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is RecipeViewHolder) {
            // holder.mItem = mValues.get(position);

            (holder as RecipeViewHolder).recipeView.tv_recipe_cal.text = ""+recipes.get(position)?.basicInfo?.perServingCalories
            (holder as RecipeViewHolder).recipeView.tv_recipe_name.text = ""+recipes.get(position)?.basicInfo?.name?.english
            (holder as RecipeViewHolder).recipeView.tv_recipe_desc.text = ""+recipes.get(position)?.basicInfo?.desc
            (holder as RecipeViewHolder).recipeView.setOnClickListener(View.OnClickListener {
                if (null != callBack) {
                    callBack.onFeaturedRecipeViewClicked(recipes.get(position))
                }
            })
            var imageViewModel = ImageViewModel((holder as RecipeViewHolder).recipeView.context)
            imageViewModel.triggerImageUrl.value = recipes.get(position).basicInfo.image
            (holder as RecipeViewHolder).recipeView.aiv_recipe.setImageViewModel(imageViewModel,(holder as RecipeViewHolder).recipeView.context as BrowseRecipeActivity)
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
        return recipes?.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(parent.context, R.layout.recipe_featured_list_item_view,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = RecipeViewHolder(view)
            return holder

    }

    class RecipeViewHolder(val recipeView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(recipeView) {}

    interface RecipeViewCallBacks{
        fun onFeaturedRecipeViewClicked(mealRecipe:Recipe)
    }
}