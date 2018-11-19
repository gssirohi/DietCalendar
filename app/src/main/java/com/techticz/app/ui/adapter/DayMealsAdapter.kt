package com.techticz.app.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.techticz.app.ui.customView.MealView
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.create_plan_copy_layout.view.*
import kotlinx.android.synthetic.main.meal_layout.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class DayMealsAdapter (val section: Int?, var dayMeals: List<MealPlateViewModel>, var callBack: MealCardCallBacks, val myPlan: Boolean): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is MealViewHolder) {
            // holder.mItem = mValues.get(position);
            var pos:Int
            if(!myPlan){
                pos = position-1
            } else {
                pos = position
            }
            (holder as MealViewHolder).mealView.fillDetails(dayMeals.get(pos))
            (holder as MealViewHolder).mealView.b_explore_plate.setOnClickListener(View.OnClickListener {
                if (null != callBack) {
                    callBack.onMealCardClicked(section!!,dayMeals.get(pos))
                }
            })
        } else {
            (holder as CreateCopyHolder).view.b_create_copy.setOnClickListener(View.OnClickListener { button->
                if (null != callBack) {
                    callBack.onCreateCopyClicked()
                }
            })
        }
    }

/*    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is PlateViewHolder) {
            holder!!.setIsRecyclable(false)
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder is PlateViewHolder) {
            holder!!.setIsRecyclable(true)
        }
        super.onViewDetachedFromWindow(holder)
    }*/
    override fun getItemCount(): Int {
    if(myPlan) {
        return dayMeals?.size
    } else {
        return dayMeals?.size +1
    }
        //return 1
    }

    private val VIEW_CREATE_COPY: Int = 1
    private val VIEW_MEAL: Int = 2

    override fun getItemViewType(position: Int): Int {
        if(!myPlan){
            if(position == 0) return VIEW_CREATE_COPY
            else return VIEW_MEAL
        } else {
            return VIEW_MEAL
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        if(viewType == VIEW_CREATE_COPY){
            val view = View.inflate(parent.context, R.layout.create_plan_copy_layout,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = CreateCopyHolder(view)
            return holder
        } else {
            val view = MealView(section,parent?.context)
            var holder = MealViewHolder(view)
            return holder
        }

    }

    class MealViewHolder(val mealView: MealView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mealView) {}
    class CreateCopyHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {}
    interface MealCardCallBacks{
        fun onMealCardClicked(section:Int, meal: MealPlateViewModel)
        fun onCreateCopyClicked()
    }
}