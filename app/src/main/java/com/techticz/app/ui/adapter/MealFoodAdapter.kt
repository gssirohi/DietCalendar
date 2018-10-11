package com.techticz.app.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.techticz.app.ui.customView.MealFoodView
import com.techticz.app.ui.customView.MealView
import com.techticz.app.viewmodel.FoodViewModel
import kotlinx.android.synthetic.main.meal_food_layout.view.*

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class MealFoodAdapter constructor(var mealView: MealView, var callBack:RecipItemCallBacks?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is MealFoodViewHolder) {
            // holder.mItem = mValues.get(position);
            var foodViewModel = mealView?.mealPlateViewModel?.liveFoodViewModelList?.value?.data?.get(position)
            (holder as MealFoodViewHolder).mealFoodView.fillDetails(foodViewModel)
            (holder as MealFoodViewHolder).mealFoodView.setOnClickListener(View.OnClickListener {
             callBack?.onFoodItemClicked(foodViewModel)
            })
            if(position == itemCount -1){
                (holder as MealFoodViewHolder).mealFoodView.divider.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        if(mealView?.mealPlateViewModel?.liveFoodViewModelList?.value?.data == null)
            return 0;
        return mealView?.mealPlateViewModel?.liveFoodViewModelList?.value?.data?.size!!
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = MealFoodView(parent,mealView)
            var holder =  MealFoodViewHolder(view)
                return holder
    }

    class MealFoodViewHolder(val mealFoodView: MealFoodView) : RecyclerView.ViewHolder(mealFoodView) {}
    interface RecipItemCallBacks{
        fun onFoodItemClicked(item: FoodViewModel?)
    }
}