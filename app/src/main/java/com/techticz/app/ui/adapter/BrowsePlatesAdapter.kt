package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.ui.customView.MealView
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.meal_plate_list_item_view.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class BrowsePlatesAdapter (var dayMeals: List<MealPlate>, var callBack: PlateViewCallBacks): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is PlateViewHolder) {
            // holder.mItem = mValues.get(position);

            (holder as PlateViewHolder).plateView.tv_plate_cal.text = ""+dayMeals.get(position)?.basicInfo?.calories
            (holder as PlateViewHolder).plateView.tv_plate_name.text = ""+dayMeals.get(position)?.basicInfo?.name?.english
            (holder as PlateViewHolder).plateView.tv_plate_desc.text = ""+dayMeals.get(position)?.basicInfo?.desc
            (holder as PlateViewHolder).plateView.setOnClickListener(View.OnClickListener {
                if (null != callBack) {
                    callBack.onPlateViewClicked(dayMeals.get(position))
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
        return dayMeals?.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(parent.context, R.layout.meal_plate_list_item_view,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = PlateViewHolder(view)
            return holder

    }

    class PlateViewHolder(val plateView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(plateView) {}

    interface PlateViewCallBacks{
        fun onPlateViewClicked(mealPlate:MealPlate)
    }
}