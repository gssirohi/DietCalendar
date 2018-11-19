package com.techticz.app.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.techticz.app.ui.frag.DayPlansFragment

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 7/10/18.
 */
class DietChartPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {


    private var monFrag: DayPlansFragment? = null
    private var tueFrag: DayPlansFragment? = null
    private var wedFrag: DayPlansFragment? = null
    private var thuFrag: DayPlansFragment? = null
    private var friFrag: DayPlansFragment? = null
    private var satFrag: DayPlansFragment? = null
    private var sunFrag: DayPlansFragment? = null

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        when(position+1){
            1-> {
                if(monFrag == null) monFrag = DayPlansFragment.newInstance(position + 1)
                return monFrag!!;
            }
            2-> {
                if(tueFrag == null) tueFrag = DayPlansFragment.newInstance(position + 1)
                return tueFrag!!;
            }
            3-> {
                if(wedFrag == null) wedFrag = DayPlansFragment.newInstance(position + 1)
                return wedFrag!!;
            }
            4-> {
                if(thuFrag == null) thuFrag = DayPlansFragment.newInstance(position + 1)
                return thuFrag!!;
            }
            5-> {
                if(friFrag == null) friFrag = DayPlansFragment.newInstance(position + 1)
                return friFrag!!;
            }
            6-> {
                if(satFrag == null) satFrag = DayPlansFragment.newInstance(position + 1)
                return satFrag!!;
            }
            7-> {
                if(sunFrag == null) sunFrag = DayPlansFragment.newInstance(position + 1)
                return sunFrag!!;
            }
        }
        return DayPlansFragment.newInstance(position + 1)
    }

    override fun getCount(): Int {
        // Show 7 total pages.
        return 7
    }
}