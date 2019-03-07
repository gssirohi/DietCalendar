package com.techticz.app.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.techticz.app.ui.activity.RecipeDetailsActivity
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.meal_food_layout.view.*
import kotlinx.android.synthetic.main.recipe_step_layout.view.*

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class RecipeStepsAdapter constructor(var recipeActivity: RecipeDetailsActivity, var callBack:StepItemCallBacks?): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is StepViewHolder) {
            // holder.mItem = mValues.get(position);
            var step = recipeActivity?.recipeViewModel?.liveRecipeResponse?.value?.data?.recipe?.formula?.steps?.get(position)
            (holder as StepViewHolder).stepView.til_step.hint = "Step "+(position+1)
            (holder as StepViewHolder).stepView.til_step.editText?.setText(step)
            if(recipeActivity.mode == RecipeDetailsActivity.MODE_EXPLORE){
                (holder as StepViewHolder).stepView.fab_remove_step.visibility = View.GONE

                (holder as StepViewHolder).stepView.til_step.editText?.apply {
                    isFocusableInTouchMode = false
                    isClickable = false
                    isLongClickable = false
                    isEnabled = false
                }
            } else {
                (holder as StepViewHolder).stepView.fab_remove_step.visibility = View.VISIBLE
                (holder as StepViewHolder).stepView.til_step.editText?.apply {
                    isFocusableInTouchMode = true
                    isClickable = true
                    isLongClickable = true
                    isEnabled = true
                }
            }
            (holder as StepViewHolder).stepView.til_step.editText?.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    recipeActivity?.steps?.set(position,s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

            })
            (holder as StepViewHolder).stepView.fab_remove_step.setOnClickListener(View.OnClickListener {
             callBack?.onRemoveStepClicked(position,step)
            })
           /* if(position == itemCount -1){
                (holder as StepViewHolder).stepView.divider.visibility = View.GONE
            }*/
        }
    }

    override fun getItemCount(): Int {
        return recipeActivity?.steps?.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = View.inflate(recipeActivity, R.layout.recipe_step_layout,null)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            var holder =  StepViewHolder(view as ViewGroup)
                return holder
    }

    class StepViewHolder(val stepView: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(stepView) {}
    interface StepItemCallBacks{
        fun onRemoveStepClicked(position: Int, step: String?)
    }
}