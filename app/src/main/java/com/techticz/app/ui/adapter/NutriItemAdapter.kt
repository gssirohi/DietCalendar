package com.techticz.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.food.Food
import com.techticz.app.ui.customView.NutritionDetailsView
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.meal_food_list_item_view.view.*
import kotlinx.android.synthetic.main.nutrition_item_layout.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class NutriItemAdapter (var nutriItems: List<NutritionDetailsView.NutriItem>): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is NutriItemViewHolder) {
            (holder as NutriItemViewHolder).nutriItemView.tv_nutrition_label.text = nutriItems.get(position).lable
            (holder as NutriItemViewHolder).nutriItemView.tv_nutrition_value1.text = nutriItems.get(position).value1
            (holder as NutriItemViewHolder).nutriItemView.tv_nutrition_value2.text = nutriItems.get(position).value2
        }
    }

    override fun getItemCount(): Int {
        return nutriItems?.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(parent.context, R.layout.nutrition_item_layout,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = NutriItemViewHolder(view)
            return holder

    }

    class NutriItemViewHolder(val nutriItemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(nutriItemView) {}

}