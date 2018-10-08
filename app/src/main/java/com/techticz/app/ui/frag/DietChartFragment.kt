package com.techticz.app.ui.frag

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techticz.app.constants.Meals
import com.techticz.app.model.dietplan.DayPlan
import com.techticz.app.model.meal.Meal
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.ui.activity.DietChartActivity
import com.techticz.app.ui.adapter.DayMealsAdapter
import com.techticz.dietcalendar.R
import com.techticz.powerkit.base.BaseDIFragment
import kotlinx.android.synthetic.main.fragment_diet_chart.*
import kotlinx.android.synthetic.main.fragment_diet_chart.view.*
import timber.log.Timber
import kotlin.text.Typography.section

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
/**
 * A placeholder fragment containing a simple view.
 */
class DietChartFragment : BaseDIFragment(), DayMealsAdapter.MealCardCallBacks {

    private var sectionNumber: Int? = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_diet_chart, container, false)
        sectionNumber = arguments?.getInt(ARG_SECTION_NUMBER)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = DayMealsAdapter(arguments?.getInt(ARG_SECTION_NUMBER),getDayMeals(),this)
    }

    private fun getDayMeals(): List<Meal> {
        var dayPlan:DayPlan = DayPlan()
        when(sectionNumber){
            1->{
                dayPlan = (activity as DietChartActivity).dietPlan.calendar.monday
            }
            2->{
                dayPlan = (activity as DietChartActivity).dietPlan.calendar.tuesday
            }
            3->{
                dayPlan = (activity as DietChartActivity).dietPlan.calendar.wednesday
            }
            4->{
                dayPlan = (activity as DietChartActivity).dietPlan.calendar.thursday
            }
            5->{
                dayPlan = (activity as DietChartActivity).dietPlan.calendar.friday
            }
            6->{
                dayPlan = (activity as DietChartActivity).dietPlan.calendar.saturday
            }
            7->{
                dayPlan = (activity as DietChartActivity).dietPlan.calendar.sunday
            }
        }
        var meals :ArrayList<Meal> = ArrayList<Meal>()
        meals.add(Meal(Meals.EARLY_MORNING,dayPlan.earlyMorning))
        meals.add(Meal(Meals.BREAKFAST,dayPlan.breakfast))
        meals.add(Meal(Meals.LUNCH,dayPlan.lunch))
        meals.add(Meal(Meals.BRUNCH,dayPlan.brunch))
        meals.add(Meal(Meals.DINNER,dayPlan.dinner))
        meals.add(Meal(Meals.BED_TIME,dayPlan.bedTime))

        return meals
    }

    override fun onMealCardClicked(section:Int,meal:Meal){
        showSuccess("Meal Clicked:"+meal.mealType.mealName+" with plate:"+meal.mealPlateId)
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
        fun newInstance(sectionNumber: Int): DietChartFragment {
            val fragment = DietChartFragment()
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