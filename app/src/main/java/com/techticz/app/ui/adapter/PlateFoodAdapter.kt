package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.ui.customView.MealFoodView
import com.techticz.app.ui.customView.PlateView
import com.techticz.app.viewmodel.FoodViewModel
import kotlinx.android.synthetic.main.meal_food_layout.view.*

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class PlateFoodAdapter constructor(var plateView: PlateView, var callBack:RecipItemCallBacks?): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is MealFoodViewHolder) {
            // holder.mItem = mValues.get(position);
            var foodViewModel = plateView?.mealPlateViewModel?.liveFoodViewModelList?.value?.data?.get(position)
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
        if(plateView?.mealPlateViewModel?.liveFoodViewModelList?.value?.data == null)
            return 0;
        return plateView?.mealPlateViewModel?.liveFoodViewModelList?.value?.data?.size!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = MealFoodView(parent,plateView)
            var holder =  MealFoodViewHolder(view)
                return holder
    }

    class MealFoodViewHolder(val mealFoodView: MealFoodView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mealFoodView) {}
    interface RecipItemCallBacks{
        fun onFoodItemClicked(item: FoodViewModel?)
    }
}