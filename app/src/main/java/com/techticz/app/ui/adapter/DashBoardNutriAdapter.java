package com.techticz.app.ui.adapter;

/**
 * Created by gssirohi on 18/7/16.
 */

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.techticz.app.model.NutriPair;
import com.techticz.app.model.dietplan.DietPlan;
import com.techticz.dietcalendar.R;
import com.techticz.powerkit.customview.CircleGraphView;

import java.util.List;


public class DashBoardNutriAdapter extends RecyclerView.Adapter<DashBoardNutriAdapter.CircleGraphViewHolder> {

    private final CallBack callBack;
    public List<NutriPair> data;

    public DashBoardNutriAdapter(List<NutriPair> data, CallBack callBack) {
        this.data = data;
        this.callBack = callBack;
    }
    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public void onViewAttachedToWindow(CircleGraphViewHolder holder) {
        if (holder instanceof CircleGraphViewHolder) {
            holder.setIsRecyclable(false);
        }
        super.onViewAttachedToWindow((CircleGraphViewHolder) holder);
    }

    @Override
    public void onViewDetachedFromWindow(CircleGraphViewHolder holder) {
        if (holder instanceof CircleGraphViewHolder){
            holder.setIsRecyclable(true);
        }
        super.onViewDetachedFromWindow((CircleGraphViewHolder) holder);
    }

    @Override
    public CircleGraphViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CircleGraphView view = new CircleGraphView(parent.getContext());

        return new CircleGraphViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CircleGraphViewHolder holder, final int position) {
       // holder.planImage.setUrl(data.get(position).getBlobServingUrl());
        NutriPair pair = data.get(position);
        holder.circleGraphView.start(pair.getName(),pair.getRda(),pair.getValue(),R.color.secondaryColor);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class CircleGraphViewHolder extends RecyclerView.ViewHolder {

        private CircleGraphView circleGraphView;

        public CircleGraphViewHolder(CircleGraphView itemView) {
            super(itemView);
            this.circleGraphView = itemView;
        }
    }


    public interface CallBack{
        void onMealPlanItemClicked(DietPlan plan);
    }

}
