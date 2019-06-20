package com.techticz.app.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup

import com.techticz.app.ui.activity.DietPlanActivity

import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.typical_meal_plate_list_item.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class TypicalPlatesAdapter (var context: Context, var dayMeals: List<MealPlateViewModel>): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is PlateViewHolder) {
            var id = dayMeals.get(position)?.liveMealPlateResponse?.value?.data?.mealPlate?.id
            if(TextUtils.isEmpty(id)){
                (holder as PlateViewHolder).plateView.visibility = View.GONE
            } else {
                (holder as PlateViewHolder).plateView.visibility = View.VISIBLE
                (holder as PlateViewHolder).plateView.tv_plate_cal.text = "" + dayMeals.get(position)?.liveMealPlateResponse?.value?.data?.mealPlate?.basicInfo?.calories
                (holder as PlateViewHolder).plateView.tv_plate_name.text = "" + dayMeals.get(position)?.liveMealPlateResponse?.value?.data?.mealPlate?.basicInfo?.name?.english
                (holder as PlateViewHolder).plateView.tv_plate_desc.text = "" + dayMeals.get(position)?.liveMealPlateResponse?.value?.data?.mealPlate?.basicInfo?.desc

                var imageVM = ImageViewModel((holder as PlateViewHolder).plateView.context)//dayMeals.get(position)?.liveImage?.value?.data
                imageVM?.triggerImageUrl?.value = dayMeals.get(position)?.liveMealPlateResponse?.value?.data?.mealPlate?.basicInfo?.image
                (holder as PlateViewHolder).plateView.aiv_plate.setImageViewModel(imageVM, this.context as DietPlanActivity)
            }
        }
    }

    override fun getItemCount(): Int {
        return dayMeals?.size
    }


    private val VIEW_MEAL: Int = 2

    override fun getItemViewType(position: Int): Int {
        return VIEW_MEAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

            val view = View.inflate(parent.context, R.layout.typical_meal_plate_list_item,null)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = params
            var holder = PlateViewHolder(view)
            return holder

    }

    class PlateViewHolder(val plateView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(plateView) {

    }

}