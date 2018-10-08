package com.techticz.app.ui.adapter;

/**
 * Created by gssirohi on 18/7/16.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.techticz.app.model.dietplan.DietPlan;
import com.techticz.dietcalendar.R;
import com.techticz.powerkit.customview.AppImageView;

import java.util.Calendar;
import java.util.List;


public class MealPlanPagerAdapter extends RecyclerView.Adapter<MealPlanPagerAdapter.ViewHolder> {

    private final CallBack callBack;
    public List<DietPlan> data;

    public MealPlanPagerAdapter(List<DietPlan> data, CallBack callBack) {
        this.data = data;
        this.callBack = callBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.meal_plan_list_item_view, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
       // holder.planImage.setUrl(data.get(position).getBlobServingUrl());
        holder.planName.setText(data.get(position).getBasicInfo().getName());
        holder.planDesc.setText(data.get(position).getBasicInfo().getName());
        holder.planCalory.setText("Daily Calories : "+data.get(position).getBasicInfo().getDailyCalories());
        holder.planImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onMealPlanItemClicked(data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppImageView planImage;
        private TextView planName;
        private TextView planDesc;
        private TextView planCalory;

        public ViewHolder(View itemView) {
            super(itemView);
            planImage = (AppImageView) itemView.findViewById(R.id.plan_image);
            planName = (TextView)itemView.findViewById(R.id.plan_name);
            planDesc = (TextView)itemView.findViewById(R.id.plan_desc);
            planCalory = (TextView)itemView.findViewById(R.id.plan_calory);
        }
    }


    public interface CallBack{
        void onMealPlanItemClicked(DietPlan plan);
    }

}
