package com.techticz.app.ui.adapter;

/**
 * Created by gssirohi on 18/7/16.
 */

import android.arch.lifecycle.LifecycleOwner;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.techticz.app.model.ImageResponse;
import com.techticz.app.model.dietplan.DietPlan;
import com.techticz.app.repo.ImageRepository;
import com.techticz.app.viewmodel.ImageViewModel;
import com.techticz.dietcalendar.R;
import com.techticz.app.ui.customView.AppImageView;
import com.techticz.networking.model.Resource;
import com.techticz.networking.model.Status;
import com.techticz.app.base.BaseDIActivity;

import java.util.List;
import java.util.Observer;

import javax.inject.Inject;


public class MealPlanPagerAdapter extends RecyclerView.Adapter<MealPlanPagerAdapter.MealPlanViewHolder> {

    private final CallBack callBack;
    public List<DietPlan> data;

    public MealPlanPagerAdapter(List<DietPlan> data, CallBack callBack) {
        this.data = data;
        this.callBack = callBack;
    }
    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public void onViewAttachedToWindow(MealPlanViewHolder holder) {
        if (holder instanceof MealPlanViewHolder) {
            holder.setIsRecyclable(false);
        }
        super.onViewAttachedToWindow((MealPlanViewHolder) holder);
    }

    @Override
    public void onViewDetachedFromWindow(MealPlanViewHolder holder) {
        if (holder instanceof MealPlanViewHolder){
            holder.setIsRecyclable(true);
        }
        super.onViewDetachedFromWindow((MealPlanViewHolder) holder);
    }

    @Override
    public MealPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.meal_plan_list_item_view, parent, false);

        return new MealPlanViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MealPlanViewHolder holder, final int position) {
       // holder.planImage.setUrl(data.get(position).getBlobServingUrl());
        holder.planName.setText(data.get(position).getBasicInfo().getName());
        holder.planDesc.setText(data.get(position).getBasicInfo().getName());
       // holder.planCalory.setText("Daily Calories : "+data.get(position).getBasicInfo().getDailyCalories());
        ImageViewModel imageViewModel = new ImageViewModel(holder.planImage.getContext());
        imageViewModel.getTriggerImageUrl().setValue(data.get(position).getBasicInfo().getImage());
        holder.planImage.setImageViewModel(imageViewModel,(LifecycleOwner) holder.planImage.getContext());
        holder.bExplore.setOnClickListener(new View.OnClickListener() {
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

    class MealPlanViewHolder extends RecyclerView.ViewHolder {

        private Button bExplore;
        private AppImageView planImage;
        private TextView planName;
        private TextView planDesc;
        private TextView planCalory;

        public MealPlanViewHolder(View itemView) {
            super(itemView);
            planImage = (AppImageView) itemView.findViewById(R.id.aiv_plan_image);
            planName = (TextView)itemView.findViewById(R.id.plan_name);
            planDesc = (TextView)itemView.findViewById(R.id.plan_desc);
            bExplore = (Button)itemView.findViewById(R.id.b_explore);
        }
    }


    public interface CallBack{
        void onMealPlanItemClicked(DietPlan plan);
    }

}
