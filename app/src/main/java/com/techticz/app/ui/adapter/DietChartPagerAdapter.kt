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

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return DietChartFragment.newInstance(position + 1)
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return 7
    }
}