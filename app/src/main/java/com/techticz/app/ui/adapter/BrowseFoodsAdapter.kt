package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.food.Food
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.meal_food_list_item_view.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class BrowseFoodsAdapter (var dayMeals: List<Food>, var callBack: FoodViewCallBacks): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is FoodViewHolder) {
            // holder.mItem = mValues.get(position);
            var calPerStdPortion = dayMeals.get(position)?.getCaloriesPerStdPortion()
            if(calPerStdPortion != null){
                (holder as FoodViewHolder).foodView.tv_food_cal.text = ""+calPerStdPortion+"\uD83D\uDD25"+" KCAL/"+dayMeals.get(position)?.standardServing?.portion+" "+dayMeals.get(position)?.standardServing?.servingUnit
                (holder as FoodViewHolder).foodView.tv_food_cal.visibility = View.VISIBLE
            } else {
                (holder as FoodViewHolder).foodView.tv_food_cal.visibility = View.GONE
            }
//            (holder as FoodViewHolder).foodView.tv_food_cal.text = ""+dayMeals.get(position)?.basicInfo?.caloriesPerStdPortion
            (holder as FoodViewHolder).foodView.tv_food_name.text = ""+dayMeals.get(position)?.basicInfo?.name?.english
            (holder as FoodViewHolder).foodView.tv_food_desc.text = ""+dayMeals.get(position)?.basicInfo?.desc
            (holder as FoodViewHolder).foodView.setOnClickListener(View.OnClickListener {
                if (null != callBack) {
                    callBack.onFoodViewClicked(dayMeals.get(position))
                }
            })
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
        return dayMeals?.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(parent.context, R.layout.meal_food_list_item_view,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = FoodViewHolder(view)
            return holder

    }

    class FoodViewHolder(val foodView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(foodView) {}

    interface FoodViewCallBacks{
        fun onFoodViewClicked(food:Food)
    }
}