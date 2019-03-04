package com.techticz.app.ui.adapter;

/**
 * Created by gssirohi on 18/7/16.
 */

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.techticz.app.model.dietplan.DietPlan;
import com.techticz.app.viewmodel.ImageViewModel;
import com.techticz.dietcalendar.R;
import com.techticz.app.ui.customView.AppImageView;

import java.util.List;


public class MealPlanPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final CallBack callBack;
    private final Context context;
    public List<DietPlan> data;

    public MealPlanPagerAdapter(Context context,List<DietPlan> data, CallBack callBack) {
        this.data = data;
        this.callBack = callBack;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {
        if(data.get(position).getId().equalsIgnoreCase("add_new")){
            return 2;
        }
        return 1;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof MealPlanViewHolder) {
            holder.setIsRecyclable(false);
        }
        super.onViewAttachedToWindow( holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof MealPlanViewHolder){
            holder.setIsRecyclable(true);
        }
        super.onViewDetachedFromWindow( holder);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v;
        if(viewType == 1) {
            v = inflater.inflate(R.layout.meal_plan_list_item_view, parent, false);
            return new MealPlanViewHolder(v);
        } else {
            v = inflater.inflate(R.layout.meal_plan_add_new_list_item_view, parent, false);
            return new AddPlanViewHolder(v);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
       // holder.planImage.setUrl(data.get(position).getBlobServingUrl());
        if(holder instanceof MealPlanViewHolder) {

            ((MealPlanViewHolder)holder).planName.setText(data.get(position).getBasicInfo().getName());
            ((MealPlanViewHolder)holder).planDesc.setText(data.get(position).getBasicInfo().getDesc());
            if (data.get(position).getBasicInfo().getType() != null && data.get(position).getBasicInfo().getType().equalsIgnoreCase("veg")) {
                ((MealPlanViewHolder)holder).planType.setTextColor(Color.parseColor("#ff669900"));
            } else if (data.get(position).getBasicInfo().getType() != null && data.get(position).getBasicInfo().getType().equalsIgnoreCase("non-veg")) {
                ((MealPlanViewHolder)holder).planType.setTextColor(Color.parseColor("#ffcc0000"));
            }
            if (data.get(position).getBasicInfo().getDailyCalories() != null && data.get(position).getBasicInfo().getDailyCalories() > 0) {
                ((MealPlanViewHolder)holder).planCalory.setText(data.get(position).getBasicInfo().getDailyCalories() + " DailyCalories");
            } else {
                ((MealPlanViewHolder)holder).planCalory.setVisibility(View.GONE);
            }
            ((MealPlanViewHolder)holder).planAuthor.setText(data.get(position).getAdminInfo().getCreatedBy());
            // holder.planCalory.setText("Daily Calories : "+data.get(position).getBasicInfo().getDailyCalories());
            ImageViewModel imageViewModel = new ImageViewModel(((MealPlanViewHolder)holder).planImage.getContext());
            imageViewModel.getTriggerImageUrl().setValue(data.get(position).getBasicInfo().getImage());
            ((MealPlanViewHolder)holder).planImage.setImageViewModel(imageViewModel, (LifecycleOwner) context);

            ((MealPlanViewHolder)holder).bExplore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onMealPlanItemClicked(data.get(position));
                }
            });
        } else {
            ((AddPlanViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onAddMealPlanClicked();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class AddPlanViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        public AddPlanViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
    class MealPlanViewHolder extends RecyclerView.ViewHolder {

        private FloatingActionButton bExplore;
        private AppImageView planImage;
        private TextView planName;
        private TextView planDesc;
        private TextView planCalory;
        private TextView planAuthor;
        private TextView planType;

        public MealPlanViewHolder(View itemView) {
            super(itemView);
            planImage = (AppImageView) itemView.findViewById(R.id.aiv_plan_image);
            planName = (TextView)itemView.findViewById(R.id.plan_name);
            planDesc = (TextView)itemView.findViewById(R.id.plan_desc);
            planType = (TextView)itemView.findViewById(R.id.plan_type);
            planCalory = (TextView)itemView.findViewById(R.id.tv_daily_cal);
            planAuthor = (TextView)itemView.findViewById(R.id.tv_plan_author);
            bExplore = (FloatingActionButton)itemView.findViewById(R.id.b_explore);
        }
    }


    public interface CallBack{
        void onMealPlanItemClicked(DietPlan plan);
        void onAddMealPlanClicked();
    }

}
