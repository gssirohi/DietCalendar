package com.techticz.app.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.techticz.app.ui.customView.MealView
import com.techticz.app.viewmodel.MealPlateViewModel


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class DayMealsAdapter constructor(val section:Int?, var dayMeals:List<MealPlateViewModel>, var callBack:MealCardCallBacks): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is MealViewHolder) {
            // holder.mItem = mValues.get(position);
            (holder as MealViewHolder).mealView.fillDetails(dayMeals.get(position))
            (holder as MealViewHolder).mealView.setOnClickListener(View.OnClickListener {
                if (null != callBack) {
                    callBack.onMealCardClicked(section!!,dayMeals.get(position))
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return dayMeals?.size
        //return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = MealView(section,parent)
        var holder = MealViewHolder(view)
        return holder
    }

    class MealViewHolder(val mealView: MealView) : RecyclerView.ViewHolder(mealView) {}
    interface MealCardCallBacks{
        fun onMealCardClicked(section:Int, meal: MealPlateViewModel)
    }
}