package com.techticz.app.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.techticz.app.ui.activity.DietChartActivity
import com.techticz.app.ui.frag.DietChartFragment

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class DietChartPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {


    private var monFrag: DietChartFragment? = null
    private var tueFrag: DietChartFragment? = null
    private var wedFrag: DietChartFragment? = null
    private var thuFrag: DietChartFragment? = null
    private var friFrag: DietChartFragment? = null
    private var satFrag: DietChartFragment? = null
    private var sunFrag: DietChartFragment? = null

    override fun getItem(position: Int): Fragment {
        when(position+1){
            1-> {
                if(monFrag == null) monFrag = DietChartFragment.newInstance(position + 1)
                return monFrag!!;
            }
            2-> {
                if(tueFrag == null) tueFrag = DietChartFragment.newInstance(position + 1)
                return tueFrag!!;
            }
            3-> {
                if(wedFrag == null) wedFrag = DietChartFragment.newInstance(position + 1)
                return wedFrag!!;
            }
            4-> {
                if(thuFrag == null) thuFrag = DietChartFragment.newInstance(position + 1)
                return thuFrag!!;
            }
            5-> {
                if(friFrag == null) friFrag = DietChartFragment.newInstance(position + 1)
                return friFrag!!;
            }
            6-> {
                if(satFrag == null) satFrag = DietChartFragment.newInstance(position + 1)
                return satFrag!!;
            }
            7-> {
                if(sunFrag == null) sunFrag = DietChartFragment.newInstance(position + 1)
                return sunFrag!!;
            }
        }
        return DietChartFragment.newInstance(position + 1)
    }

    override fun getCount(): Int {
        // Show 7 total pages.
        return 7
    }
}