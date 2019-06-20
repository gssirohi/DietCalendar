package com.techticz.app.ui.customView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.techticz.app.model.dietplan.CaloryDistribution
import com.techticz.dietcalendar.R
import kotlinx.android.synthetic.main.calory_distribution_layout.view.*
import com.hookedonplay.decoviewlib.charts.SeriesItem
import com.hookedonplay.decoviewlib.events.DecoEvent


class CaloryDistributionView(context: Context?,attributeSet: AttributeSet) : LinearLayout(context,attributeSet) {
    private var distribution: CaloryDistribution = CaloryDistribution()
    private var mode = MODE_EXPLORE
    companion object{
        var MODE_EXPLORE = 2
        var MODE_EDIT = 3
    }
    init {
        orientation = VERTICAL
        init()
    }

    private fun init() {
        val itemView = View.inflate(context,R.layout.calory_distribution_layout, this)

        sbv_em.min = 0
        sbv_em.max = 40

        sbv_bf.min = 0
        sbv_bf.max = 40

        sbv_lc.min = 0
        sbv_lc.max = 40

        sbv_es.min = 0
        sbv_es.max = 40

        sbv_dn.min = 0
        sbv_dn.max = 40

        sbv_bt.min = 0
        sbv_bt.max = 40


        sbv_em.setSeekbarValueChangedListner { position->
            distribution.earlyMorning = position
            balanceFromPosition(listOf(2,3,4,5,6))
        }
        sbv_bf.setSeekbarValueChangedListner { position->
            distribution.breakfast = position
            balanceFromPosition(listOf(3,4,5,6,1))
        }
        sbv_lc.setSeekbarValueChangedListner { position->
            distribution.lunch = position
            balanceFromPosition(listOf(4,5,6,1,2))
        }
        sbv_es.setSeekbarValueChangedListner { position->
            distribution.eveningSnacks = position
            balanceFromPosition(listOf(5,6,1,2,3))
        }
        sbv_dn.setSeekbarValueChangedListner { position->
            distribution.dinner = position
            balanceFromPosition(listOf(6,1,2,3,4))
        }
        sbv_bt.setSeekbarValueChangedListner { position->
            distribution.bedTime = position
            balanceFromPosition(listOf(1,2,3,4,5))
        }
        initUI()
        setCalDistribution(CaloryDistribution(10,20,30,10,20,10))
        setMode(MODE_EXPLORE)
    }

    private fun updateEditability() {
        if(mode == MODE_EXPLORE) {
            sbv_em.isEnabled = false
            sbv_em.visibility = View.INVISIBLE
            sbv_bf.isEnabled = false
            sbv_bf.visibility = View.INVISIBLE
            sbv_lc.isEnabled = false
            sbv_lc.visibility = View.INVISIBLE
            sbv_es.isEnabled = false
            sbv_es.visibility = View.INVISIBLE
            sbv_dn.isEnabled = false
            sbv_dn.visibility = View.INVISIBLE
            sbv_bt.isEnabled = false
            sbv_bt.visibility = View.INVISIBLE
            fl_arcView.visibility = View.VISIBLE
        } else {
            sbv_em.isEnabled = true
            sbv_em.visibility = View.VISIBLE
            sbv_bf.isEnabled = true
            sbv_bf.visibility = View.VISIBLE
            sbv_lc.isEnabled = true
            sbv_lc.visibility = View.VISIBLE
            sbv_es.isEnabled = true
            sbv_es.visibility = View.VISIBLE
            sbv_dn.isEnabled = true
            sbv_dn.visibility = View.VISIBLE
            sbv_bt.isEnabled = true
            sbv_bt.visibility = View.VISIBLE
            fl_arcView.visibility = View.GONE
        }

    }

    public fun setMode(mode:Int){
        this.mode = mode
        updateEditability()
    }

    private fun updateCalDistributionHeaders() {
        tv_calory_percent.setText(""+totalDistribution()+"%")
        tv_em.setText("Early Morning: "+distribution.earlyMorning+"%")
        tv_bf.setText("Breakfast: "+distribution.breakfast+"%")
        tv_ln.setText("Lunch: "+distribution.lunch+"%")
        tv_es.setText("Snacks: "+distribution.eveningSnacks+"%")
        tv_dn.setText("Dinner: "+distribution.dinner+"%")
        tv_bt.setText("Bed Time: "+distribution.bedTime+"%")
    }

    private fun balanceFromPosition(positions: List<Int>) {
        var total = totalDistribution()
        if(total >100){
            var extra = total-100
            for(j in 0..positions.size-1){
                extra = balanceExtraFrom(positions.get(j),extra)
                if(extra <= 0)break;
            }
        } else if(total < 100){
            var less = 100 - total
            for(j in 0..positions.size-1){
                less = balanceLessTo(positions.get(j),less)
                if(less <= 0)break;
            }
        }
   //     updateSeekBars()
        updateArcView()
        updateCalDistributionHeaders()
    }

    private fun updateArcView() {
        arcView.addEvent(DecoEvent.Builder(distribution.earlyMorning.toFloat()).setIndex(seriesIndexEM).setDelay(0).build())
        arcView.addEvent(DecoEvent.Builder((distribution.earlyMorning+ distribution.breakfast).toFloat()).setIndex(seriesIndexBF).setDelay(0).build())
        arcView.addEvent(DecoEvent.Builder((distribution.earlyMorning+ distribution.breakfast+distribution.lunch).toFloat()).setIndex(seriesIndexLC).setDelay(0).build())
        arcView.addEvent(DecoEvent.Builder((distribution.earlyMorning+ distribution.breakfast+distribution.lunch+distribution.eveningSnacks).toFloat()).setIndex(seriesIndexES).setDelay(0).build())
        arcView.addEvent(DecoEvent.Builder((distribution.earlyMorning+ distribution.breakfast+distribution.lunch+distribution.eveningSnacks+distribution.dinner).toFloat()).setIndex(seriesIndexDN).setDelay(0).build())
        arcView.addEvent(DecoEvent.Builder((distribution.earlyMorning+ distribution.breakfast+distribution.lunch+distribution.eveningSnacks+distribution.dinner+distribution.bedTime).toFloat()).setIndex(seriesIndexBT).setDelay(0).build())
    }

    private fun updateSeekBars() {
        sbv_em.updateCurrentProgress(distribution.earlyMorning)
        sbv_bf.updateCurrentProgress(distribution.breakfast)
        sbv_lc.updateCurrentProgress(distribution.lunch)
        sbv_es.updateCurrentProgress(distribution.eveningSnacks)
        sbv_dn.updateCurrentProgress(distribution.dinner)
        sbv_bt.updateCurrentProgress(distribution.bedTime)
    }

    private fun totalDistribution(): Int {
        var total = distribution.sum()
        return total
    }

    private fun balanceExtraFrom(j: Int, extra: Int): Int {
        var value:Int? = null
        when(j){
            1->{
                var diff:Int = 0
                if(distribution.earlyMorning!! < extra!!){
                    diff = extra-distribution.earlyMorning
                    distribution.earlyMorning = 0
                } else {
                    distribution.earlyMorning = distribution.earlyMorning - extra
                    diff = 0
                }
                sbv_em.updateCurrentProgress(distribution.earlyMorning)
                return diff
            }
            2-> {
                var diff:Int = 0
                if(distribution.breakfast!! < extra!!){
                    diff = extra-distribution.breakfast
                    distribution.breakfast = 0
                } else {
                    distribution.breakfast = distribution.breakfast - extra
                    diff = 0
                }
                sbv_bf.updateCurrentProgress(distribution.breakfast)
                return diff
            }
            3-> {
                var diff:Int = 0
                if(distribution.lunch!! < extra!!){
                    diff = extra-distribution.lunch
                    distribution.lunch = 0
                } else {
                    distribution.lunch = distribution.lunch - extra
                    diff = 0
                }
                sbv_lc.updateCurrentProgress(distribution.lunch)
                return diff
            }
            4->  {
                var diff:Int = 0
                if(distribution.eveningSnacks!! < extra!!){
                    diff = extra-distribution.eveningSnacks
                    distribution.eveningSnacks = 0
                } else {
                    distribution.eveningSnacks = distribution.eveningSnacks - extra
                    diff = 0
                }
                sbv_es.updateCurrentProgress(distribution.eveningSnacks)
                return diff
            }
            5-> {
                var diff:Int = 0
                if(distribution.dinner!! < extra!!){
                    diff = extra-distribution.dinner
                    distribution.dinner = 0
                } else {
                    distribution.dinner = distribution.dinner - extra
                    diff = 0
                }
                sbv_dn.updateCurrentProgress(distribution.dinner)
                return diff
            }
            6-> {
                var diff:Int = 0
                if(distribution.bedTime!! < extra!!){
                    diff = extra-distribution.bedTime
                    distribution.bedTime = 0
                } else {
                    distribution.bedTime = distribution.bedTime - extra
                    diff = 0
                }
                sbv_bt.updateCurrentProgress(distribution.bedTime)
                return diff
            }
        }

        return 0
    }

    private fun balanceLessTo(j: Int, less: Int): Int {
        var value:Int? = null
        when(j){
            1->{
                var diff:Int = 0
                if((40-distribution.earlyMorning!!) < less!!){
                    diff = less-(40-distribution.earlyMorning)
                    distribution.earlyMorning = 40
                } else {
                    distribution.earlyMorning = distribution.earlyMorning + less
                    diff = 0
                }
                sbv_em.updateCurrentProgress(distribution.earlyMorning)
                return diff
            }
            2-> {
                var diff:Int = 0
                if((40-distribution.breakfast!!) < less!!){
                    diff = less-(40-distribution.breakfast)
                    distribution.breakfast = 40
                } else {
                    distribution.breakfast = distribution.breakfast + less
                    diff = 0
                }
                sbv_bf.updateCurrentProgress(distribution.breakfast)
                return diff
            }
            3-> {
                var diff:Int = 0
                if((40-distribution.lunch!!) < less!!){
                    diff = less-(40-distribution.lunch)
                    distribution.lunch = 40
                } else {
                    distribution.lunch = distribution.lunch + less
                    diff = 0
                }
                sbv_lc.updateCurrentProgress(distribution.lunch)
                return diff
            }
            4->  {
                var diff:Int = 0
                if((40-distribution.eveningSnacks!!) < less!!){
                    diff = less-(40-distribution.eveningSnacks)
                    distribution.eveningSnacks = 40
                } else {
                    distribution.eveningSnacks = distribution.eveningSnacks + less
                    diff = 0
                }
                sbv_es.updateCurrentProgress(distribution.eveningSnacks)
                return diff
            }
            5-> {
                var diff:Int = 0
                if((40-distribution.dinner!!) < less!!){
                    diff = less-(40-distribution.dinner)
                    distribution.dinner = 40
                } else {
                    distribution.dinner = distribution.dinner + less
                    diff = 0
                }
                sbv_dn.updateCurrentProgress(distribution.dinner)
                return diff
            }
            6-> {
                var diff:Int = 0
                if((40-distribution.bedTime!!) < less!!){
                    diff = less-(40-distribution.bedTime)
                    distribution.bedTime = 40
                } else {
                    distribution.bedTime = distribution.bedTime + less
                    diff = 0
                }
                sbv_bt.updateCurrentProgress(distribution.bedTime)
                return diff
            }
        }

        return 0
    }


    public fun setCalDistribution(distribution:CaloryDistribution){
        this.distribution = distribution
        updateArcView()
        updateSeekBars()
        updateCalDistributionHeaders()
    }

    private var seriesIndexEM: Int = 0
    private var seriesIndexBF: Int = 0
    private var seriesIndexLC: Int = 0
    private var seriesIndexES: Int = 0
    private var seriesIndexDN: Int = 0
    private var seriesIndexBT: Int = 0

    private var seriesItemEM: SeriesItem? = null
    private var seriesItemBF: SeriesItem? = null;
    private var seriesItemLC: SeriesItem? = null;
    private var seriesItemES: SeriesItem? = null;
    private var seriesItemDN: SeriesItem? = null;
    private var seriesItemBT: SeriesItem? = null;

    private fun initUI() {

        arcView.addSeries(SeriesItem.Builder(context.resources.getColor(R.color.translucent))
                .setRange(0f, 100f, 100f)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build())

        arcView.addEvent(DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(0)
                .build())


//Create data series track
        tv_em.setTextColor(context.resources.getColor(R.color.primaryDarkColor))
        seriesItemEM = SeriesItem.Builder(context.resources.getColor(R.color.primaryDarkColor))
                .setRange(0f, 100f, 0f)
                .setLineWidth(32f)
                .build()

        tv_bf.setTextColor(context.resources.getColor(R.color.primaryColor))
        seriesItemBF = SeriesItem.Builder(context.resources.getColor(R.color.primaryColor))
                .setRange(0f, 100f, 0f)
                .setLineWidth(32f)
                .build()

        tv_ln.setTextColor(context.resources.getColor(R.color.primaryLightColor))
        seriesItemLC = SeriesItem.Builder(context.resources.getColor(R.color.primaryLightColor))
                .setRange(0f, 100f, 0f)
                .setLineWidth(32f)
                .build()

        tv_es.setTextColor(context.resources.getColor(R.color.secondaryLightColor))
        seriesItemES = SeriesItem.Builder(context.resources.getColor(R.color.secondaryLightColor))
                .setRange(0f, 100f, 0f)
                .setLineWidth(32f)
                .build()

        tv_dn.setTextColor(context.resources.getColor(R.color.secondaryColor))
        seriesItemDN = SeriesItem.Builder(context.resources.getColor(R.color.secondaryColor))
                .setRange(0f, 100f, 0f)
                .setLineWidth(32f)
                .build()

        tv_bt.setTextColor(context.resources.getColor(R.color.secondaryDarkColor))
        seriesItemBT = SeriesItem.Builder(context.resources.getColor(R.color.secondaryDarkColor))
                .setRange(0f, 100f, 0f)
                .setLineWidth(32f)
                .build()

        seriesIndexBT = arcView.addSeries(seriesItemBT!!)
        seriesIndexDN = arcView.addSeries(seriesItemDN!!)
        seriesIndexES = arcView.addSeries(seriesItemES!!)
        seriesIndexLC = arcView.addSeries(seriesItemLC!!)
        seriesIndexBF = arcView.addSeries(seriesItemBF!!)
        seriesIndexEM = arcView.addSeries(seriesItemEM!!)

    }

    fun getCaloryDistribution(): CaloryDistribution? {
        return distribution
    }
}