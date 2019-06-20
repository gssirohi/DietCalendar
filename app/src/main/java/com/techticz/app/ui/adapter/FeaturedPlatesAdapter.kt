package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.ui.activity.BrowsePlateActivity
import com.techticz.app.ui.customView.MealView
import com.techticz.app.viewmodel.ImageViewModel
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.meal_plate_featured_list_item_view.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class FeaturedPlatesAdapter (var dayMeals: List<MealPlate>, var callBack: PlateViewCallBacks): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is PlateViewHolder) {
            // holder.mItem = mValues.get(position);

            (holder as PlateViewHolder).plateView.tv_plate_cal.text = ""+dayMeals.get(position)?.basicInfo?.calories
            (holder as PlateViewHolder).plateView.tv_plate_name.text = ""+dayMeals.get(position)?.basicInfo?.name?.english
            (holder as PlateViewHolder).plateView.tv_plate_desc.text = ""+dayMeals.get(position)?.basicInfo?.desc
            (holder as PlateViewHolder).plateView.setOnClickListener(View.OnClickListener {
                if (null != callBack) {
                    callBack.onFeaturedPlateViewClicked(dayMeals.get(position))
                }
            })
            var imageViewModel = ImageViewModel((holder as PlateViewHolder).plateView.context)
            imageViewModel.triggerImageUrl.value = dayMeals.get(position).basicInfo.image
            (holder as PlateViewHolder).plateView.aiv_plate.setImageViewModel(imageViewModel,(holder as PlateViewHolder).plateView.context as BrowsePlateActivity)
        }
    }

    override fun getItemCount(): Int {
        return dayMeals?.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(parent.context, R.layout.meal_plate_featured_list_item_view,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = PlateViewHolder(view)
            return holder

    }

    class PlateViewHolder(val plateView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(plateView) {}

    interface PlateViewCallBacks{
        fun onFeaturedPlateViewClicked(mealPlate:MealPlate)
    }
}