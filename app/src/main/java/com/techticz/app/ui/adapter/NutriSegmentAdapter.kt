package com.techticz.app.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.techticz.app.model.food.Food
import com.techticz.app.ui.customView.NutritionDetailsView
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.meal_food_list_item_view.view.*
import kotlinx.android.synthetic.main.nutri_segment_layout.view.*


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class NutriSegmentAdapter (var ctx: Context, var nutriSegments: List<NutritionDetailsView.NutriSegment>): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is NutriSegmentViewHolder) {
            (holder as NutriSegmentViewHolder).nutriSegmentView.tv_nutri_segment_title.text = nutriSegments.get(position).title
            (holder as NutriSegmentViewHolder).nutriSegmentView.nutriItemsRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            (holder as NutriSegmentViewHolder).nutriSegmentView.nutriItemsRecycler.adapter = NutriItemAdapter(nutriSegments.get(position).nutriItems)
        }
    }

    override fun getItemCount(): Int {
        return nutriSegments?.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(parent.context, R.layout.nutri_segment_layout,null)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = params
            var holder = NutriSegmentViewHolder(view)
            return holder

    }

    class NutriSegmentViewHolder(val nutriSegmentView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(nutriSegmentView) {}

}