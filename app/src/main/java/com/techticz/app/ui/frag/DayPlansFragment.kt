package com.techticz.app.ui.frag

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techticz.app.ui.activity.DietChartActivity
import com.techticz.app.ui.adapter.DayMealsAdapter
import com.techticz.app.viewmodel.MealPlateViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIFragment
import kotlinx.android.synthetic.main.fragment_day_plans.*
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
/**
 * A placeholder fragment containing a simple view.
 */
class DayPlansFragment : BaseDIFragment(), DayMealsAdapter.MealCardCallBacks {


    private var sectionNumber: Int? = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_day_plans, container, false)
        sectionNumber = arguments?.getInt(ARG_SECTION_NUMBER)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        (activity as DietChartActivity).dietChartViewModel.getDayMealViewModels(sectionNumber!!)?.observe(activity as DietChartActivity, Observer {
            res->
            onDayMealsViewModelLoaded(res)
        })

    }

    private fun onDayMealsViewModelLoaded(res: Resource<List<MealPlateViewModel>>?) {
        when(res?.status){
            Status.LOADING->{
                var isMyPlan = (activity as DietChartActivity).dietChartViewModel?.isMyPlan()
                recyclerView.adapter = DayMealsAdapter(arguments?.getInt(ARG_SECTION_NUMBER), res?.data!!,this,isMyPlan)
            }
        }
    }

    override fun onMealCardClicked(section:Int, mealViewModel: MealPlateViewModel){
        showSuccess("Meal Clicked:"+ mealViewModel?.triggerMealPlateID?.value?.mealType?.mealName+" with plate:"+ mealViewModel?.triggerMealPlateID?.value?.mealPlateId)
        (activity as DietChartActivity).navigator.startExplorePlateScreen(mealViewModel?.triggerMealPlateID?.value?.mealPlateId);
    }

    override fun onCreateCopyClicked() {
        (activity as DietChartActivity).navigator.startCreatePlanActivity(activity as DietChartActivity,(activity as DietChartActivity).dietChartViewModel?.liveDietPlanResponse?.value?.data?.dietPlan)
        (activity as DietChartActivity).finish()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): DayPlansFragment {
            val fragment = DayPlansFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }

    init {
        Timber.d("Injecting:"+this)
    }
}