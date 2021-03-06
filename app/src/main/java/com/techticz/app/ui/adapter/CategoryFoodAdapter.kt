package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.food.Food
import com.techticz.app.ui.activity.BrowseFoodActivity
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.food_featured_list_item_view.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class CategoryFoodAdapter (var foods: List<Food>, var callBack: FoodViewCallBacks): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is FoodViewHolder) {
            // holder.mItem = mValues.get(position);

            (holder as FoodViewHolder).foodView.tv_food_cal.text = ""+callBack.getFoodNutrient(foods.get(position))
            (holder as FoodViewHolder).foodView.tv_food_name.text = ""+foods.get(position)?.basicInfo?.name?.english
            (holder as FoodViewHolder).foodView.tv_food_desc.text = ""+foods.get(position)?.basicInfo?.desc
            (holder as FoodViewHolder).foodView.setOnClickListener(View.OnClickListener {
                if (null != callBack) {
                    callBack.onFeaturedFoodViewClicked(foods.get(position))
                }
            })
            var imageViewModel = ImageViewModel((holder as FoodViewHolder).foodView.context)
            imageViewModel.triggerImageUrl.value = foods.get(position).basicInfo.image
            (holder as FoodViewHolder).foodView.aiv_food.setImageViewModel(imageViewModel,(holder as FoodViewHolder).foodView.context as BrowseFoodActivity)
        }
    }

/*    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is FoodViewHolder) {
            holder!!.setIsRecyclable(false)
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder is FoodViewHolder) {
            holder!!.setIsRecyclable(true)
        }
        super.onViewDetachedFromWindow(holder)
    }*/
    override fun getItemCount(): Int {
        return foods?.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(parent.context, R.layout.food_featured_list_item_view,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = FoodViewHolder(view)
            return holder

    }

    class FoodViewHolder(val foodView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(foodView) {}

    interface FoodViewCallBacks{
        fun onFeaturedFoodViewClicked(mealFood:Food)
        fun getFoodNutrient(get: Food): Float
    }
}