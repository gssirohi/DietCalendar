package com.techticz.app.ui.customView

import androidx.lifecycle.Observer
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.techticz.app.model.FoodResponse
import com.techticz.app.viewmodel.FoodViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.food.Nutrition
import com.techticz.app.ui.adapter.NutriSegmentAdapter
import com.techticz.app.util.Utils
import kotlinx.android.synthetic.main.nutrition_layout.view.*
import kotlinx.android.synthetic.main.recipe_food_layout.view.*
import timber.log.Timber
import java.util.function.Consumer

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 8/10/18.
 */
class NutritionDetailsView(parent: ViewGroup?,var title:String,var itemHeader1:String,var itemHeader2:String,var nutrition1:Nutrition,var nutrition2:Nutrition,var mode:Int) : FrameLayout(parent?.context) {

    companion object {
        var MODE_COMPARISION = 1
        var MODE_RDA = 2
    }
    init {
        val params = LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
        layoutParams = params
        initData()
        initView(parent)
    }

    var segments = ArrayList<NutriSegment>()

    private fun initData() {
        // add principles segment
        var segments = ArrayList<NutriSegment>()
        segments = ArrayList()
        var principleSeg = NutriSegment()

        principleSeg.title = "Principles"
        var calories1 = Utils.calories(nutrition1.nutrients.principlesAndDietaryFibers?.energy)
        var calories2 = Utils.calories(nutrition2.nutrients.principlesAndDietaryFibers?.energy)

        var carbs1 = nutrition1.nutrients.principlesAndDietaryFibers?.carbohydrate
        var carbs2 = nutrition2.nutrients.principlesAndDietaryFibers?.carbohydrate

        var protine1 = nutrition1.nutrients.principlesAndDietaryFibers?.protien
        var protine2 = nutrition2.nutrients.principlesAndDietaryFibers?.protien

        var fat1 = nutrition1.nutrients.principlesAndDietaryFibers?.fat
        var fat2 = nutrition2.nutrients.principlesAndDietaryFibers?.fat

        principleSeg.nutriItems.add(NutriItem("Energy",getValue1(calories1,"KCAL"),getValue2(calories1,calories2,"KCAL")))
        principleSeg.nutriItems.add(NutriItem("Carbohydrates",getValue1(carbs1,"grams"),getValue2(carbs1,carbs2,"grams")))
        principleSeg.nutriItems.add(NutriItem("Fat",getValue1(fat1,"grams"),getValue2(fat1,fat2,"grams")))
        principleSeg.nutriItems.add(NutriItem("Protine",getValue1(protine1,"grams"),getValue2(protine1,protine2,"grams")))

        segments.add(principleSeg)

        //--------------------------------------------

        var fiberSeg = NutriSegment()
        fiberSeg.title = "Dietry Fibers"

        var soluable1 = nutrition1.nutrients.principlesAndDietaryFibers?.dietaryFiber?.soluble
        var soluable2 = nutrition2.nutrients.principlesAndDietaryFibers?.dietaryFiber?.soluble

        var insoluable1 = nutrition1.nutrients.principlesAndDietaryFibers?.dietaryFiber?.inSoluble
        var insoluable2 = nutrition2.nutrients.principlesAndDietaryFibers?.dietaryFiber?.inSoluble

        var total1 = nutrition1.nutrients.principlesAndDietaryFibers?.dietaryFiber?.total
        var total2 = nutrition2.nutrients.principlesAndDietaryFibers?.dietaryFiber?.total

        fiberSeg.nutriItems.add(NutriItem("Soluable",getValue1(soluable1,"grams"),getValue2(soluable1,soluable2,"grams")))
        fiberSeg.nutriItems.add(NutriItem("In-Soluable",getValue1(insoluable1,"grams"),getValue2(insoluable1,insoluable2,"grams")))
        fiberSeg.nutriItems.add(NutriItem("Total",getValue1(total1,"grams"),getValue2(total1,total2,"grams")))

        segments.add(fiberSeg)
        //------------------------------------------------------------
        var vitaminsSeg = NutriSegment()
        vitaminsSeg.title = "Water Soluable Vitamins"

        var b1_1 = nutrition1.nutrients.waterSolubleVitamins?.thiamineB1
        var b1_2 = nutrition2.nutrients.waterSolubleVitamins?.thiamineB1

        var b2_1 = nutrition1.nutrients.waterSolubleVitamins?.riboflavinB2
        var b2_2 = nutrition2.nutrients.waterSolubleVitamins?.riboflavinB2

        var b3_1 = nutrition1.nutrients.waterSolubleVitamins?.niacinB3
        var b3_2 = nutrition2.nutrients.waterSolubleVitamins?.niacinB3

        var b5_1 = nutrition1.nutrients.waterSolubleVitamins?.pentothenicAcidB5
        var b5_2 = nutrition2.nutrients.waterSolubleVitamins?.pentothenicAcidB5

        var b6_1 = nutrition1.nutrients.waterSolubleVitamins?.totalB6
        var b6_2 = nutrition2.nutrients.waterSolubleVitamins?.totalB6

        var b7_1 = nutrition1.nutrients.waterSolubleVitamins?.bioinB7
        var b7_2 = nutrition2.nutrients.waterSolubleVitamins?.bioinB7

        var b9_1 = nutrition1.nutrients.waterSolubleVitamins?.totalFolatesB9
        var b9_2 = nutrition2.nutrients.waterSolubleVitamins?.totalFolatesB9

        var ascorbic_1 = nutrition1.nutrients.waterSolubleVitamins?.totalAscorbicAcid
        var ascorbic_2 = nutrition2.nutrients.waterSolubleVitamins?.totalAscorbicAcid

        vitaminsSeg.nutriItems.add(NutriItem("Thiamine B1",getValue1(b1_1,"mg"),getValue2(b1_1,b1_2,"mg")))
        vitaminsSeg.nutriItems.add(NutriItem("Riboflavin B2",getValue1(b2_1,"mg"),getValue2(b2_1,b2_2,"grams")))
        vitaminsSeg.nutriItems.add(NutriItem("Niacin B3",getValue1(b3_1,"mg"),getValue2(b3_1,b3_2,"mg")))
        vitaminsSeg.nutriItems.add(NutriItem("Pentothenic Acid B5",getValue1(b5_1,"mg"),getValue2(b5_1,b5_2,"mg")))
        vitaminsSeg.nutriItems.add(NutriItem("Total B6",getValue1(b6_1,"mg"),getValue2(b6_1,b6_2,"mg")))
        vitaminsSeg.nutriItems.add(NutriItem("Biotin B7",getValue1(b7_1,"ug"),getValue2(b7_1,b7_2,"ug")))
        vitaminsSeg.nutriItems.add(NutriItem("Folates B9",getValue1(b9_1,"ug"),getValue2(b9_1,b9_2,"ug")))
        vitaminsSeg.nutriItems.add(NutriItem("Ascorbic Acid",getValue1(ascorbic_1,"mg"),getValue2(ascorbic_1,ascorbic_2,"mg")))

        segments.add(vitaminsSeg)

        var mineralSeg = NutriSegment()
        mineralSeg.title = "Minerals and Trace Elements"

        var alum_1 = nutrition1.nutrients.mineralsAndTraceElements.aluminium
        var arse_1 = nutrition1.nutrients.mineralsAndTraceElements.arsenic
        var cadi_1 = nutrition1.nutrients.mineralsAndTraceElements.cadium
        var calci1 = nutrition1.nutrients.mineralsAndTraceElements.calcium
        var chrom_1 = nutrition1.nutrients.mineralsAndTraceElements.chromium
        var cobal_1 = nutrition1.nutrients.mineralsAndTraceElements.cobalt
        var copper_1 = nutrition1.nutrients.mineralsAndTraceElements.copper
        var iron_1 = nutrition1.nutrients.mineralsAndTraceElements.iron
        var led_1 = nutrition1.nutrients.mineralsAndTraceElements.led
        var lithi_1 = nutrition1.nutrients.mineralsAndTraceElements.lithium
        var magnes_1 = nutrition1.nutrients.mineralsAndTraceElements.magnesium
        var mangne_1 = nutrition1.nutrients.mineralsAndTraceElements.manganees
        var mercu_1 = nutrition1.nutrients.mineralsAndTraceElements.mercury
        var moleb_1 = nutrition1.nutrients.mineralsAndTraceElements.molebdeum
        var nickl_1 = nutrition1.nutrients.mineralsAndTraceElements.nickle
        var phosp_1 = nutrition1.nutrients.mineralsAndTraceElements.phosphorus
        var potas_1 = nutrition1.nutrients.mineralsAndTraceElements.potassium
        var selen_1 = nutrition1.nutrients.mineralsAndTraceElements.selenium
        var sodiu_1 = nutrition1.nutrients.mineralsAndTraceElements.sodium
        var zinc_1 = nutrition1.nutrients.mineralsAndTraceElements.zinc

        var alum_2 = nutrition2.nutrients.mineralsAndTraceElements.aluminium
        var arse_2 = nutrition2.nutrients.mineralsAndTraceElements.arsenic
        var cadi_2 = nutrition2.nutrients.mineralsAndTraceElements.cadium
        var calci2 = nutrition2.nutrients.mineralsAndTraceElements.calcium
        var chrom_2 = nutrition2.nutrients.mineralsAndTraceElements.chromium
        var cobal_2 = nutrition2.nutrients.mineralsAndTraceElements.cobalt
        var copper_2 = nutrition2.nutrients.mineralsAndTraceElements.copper
        var iron_2 = nutrition2.nutrients.mineralsAndTraceElements.iron
        var led_2 = nutrition2.nutrients.mineralsAndTraceElements.led
        var lithi_2 = nutrition2.nutrients.mineralsAndTraceElements.lithium
        var magnes_2 = nutrition2.nutrients.mineralsAndTraceElements.magnesium
        var mangne_2 = nutrition2.nutrients.mineralsAndTraceElements.manganees
        var mercu_2 = nutrition2.nutrients.mineralsAndTraceElements.mercury
        var moleb_2 = nutrition2.nutrients.mineralsAndTraceElements.molebdeum
        var nickl_2 = nutrition2.nutrients.mineralsAndTraceElements.nickle
        var phosp_2 = nutrition2.nutrients.mineralsAndTraceElements.phosphorus
        var potas_2 = nutrition2.nutrients.mineralsAndTraceElements.potassium
        var selen_2 = nutrition2.nutrients.mineralsAndTraceElements.selenium
        var sodiu_2 = nutrition2.nutrients.mineralsAndTraceElements.sodium
        var zinc_2 = nutrition2.nutrients.mineralsAndTraceElements.zinc

        mineralSeg.nutriItems.add(NutriItem("Aluminium",getValue1(alum_1,"mg"),getValue2(alum_1,alum_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Arsenic",getValue1(arse_1,"ug"),getValue2(arse_1,arse_2,"ug")))
        mineralSeg.nutriItems.add(NutriItem("Cadium",getValue1(cadi_1,"mg"),getValue2(cadi_1,cadi_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Calcium",getValue1(calci1,"mg"),getValue2(calci1,calci2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Chromium",getValue1(chrom_1,"mg"),getValue2(chrom_1,chrom_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Cobalt",getValue1(cobal_1,"mg"),getValue2(cobal_1,cobal_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Copper",getValue1(copper_1,"mg"),getValue2(copper_1,copper_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Iron",getValue1(iron_1,"mg"),getValue2(iron_1,iron_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Led",getValue1(led_1,"mg"),getValue2(led_1,led_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Lithium",getValue1(lithi_1,"mg"),getValue2(lithi_1,lithi_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Magnesium",getValue1(magnes_1,"mg"),getValue2(magnes_1,magnes_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Manganees",getValue1(mangne_1,"mg"),getValue2(mangne_1,mangne_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Mercury",getValue1(mercu_1,"ug"),getValue2(mercu_1,mercu_2,"ug")))
        mineralSeg.nutriItems.add(NutriItem("Nickle",getValue1(nickl_1,"mg"),getValue2(nickl_1,nickl_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Phosphorus",getValue1(phosp_1,"mg"),getValue2(phosp_1,phosp_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Potassium",getValue1(potas_1,"mg"),getValue2(potas_1,potas_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Selenium",getValue1(selen_1,"ug"),getValue2(selen_1,selen_2,"ug")))
        mineralSeg.nutriItems.add(NutriItem("Sodium",getValue1(sodiu_1,"mg"),getValue2(sodiu_1,sodiu_2,"mg")))
        mineralSeg.nutriItems.add(NutriItem("Zinc",getValue1(zinc_1,"mg"),getValue2(zinc_1,zinc_2,"mg")))

        segments.add(mineralSeg)

        // remove empty Fields
        this.segments = ArrayList()
        segments.forEach {
            var items = ArrayList<NutriItem>()
            it.nutriItems.forEach {
                if(!it.value1.equals("0.0 mg") && !it.value1.equals("0.0 ug") && !it.value1.equals("-")){
                    items.add(it)
                }
            }
            if(!items.isEmpty()){
                it.nutriItems = items
                this.segments.add(it)
            }
        }
    }

    private fun getValue1(value1: Float?, unit: String): String {
        if(value1 != null) {
            return "" + Utils.roundUpFloatToOneDecimal(value1) + " " + unit
        } else {
            return "-"
        }
    }

    private fun getValue2(value1:Float?,value2: Float?, unit: String): String {
        if(mode == MODE_RDA){
            return getRDAValue(value1,value2)
        } else {
            if(value2 != null) {
                return "" + Utils.roundUpFloatToOneDecimal(value2) + " " + unit
            } else {
                return "-"
            }
        }
    }

    private fun getRDAValue(amount: Float?, rdaAmount: Float?): String {
        if(amount != null && rdaAmount != null){
            return ""+Utils.roundUpFloatToOneDecimal((amount*100)/rdaAmount) + " %"
        } else if(amount == null){
            return "0.0 %"
        } else {
            return "-"
        }
    }

    class NutriSegment {
        var title:String = ""
        var nutriItems = ArrayList<NutriItem>()
    }

    class NutriItem {
        var lable:String = ""
        var value1:String = ""
        var value2:String = ""

        constructor(lable: String, value1: String, value2: String) {
            this.lable = lable
            this.value1 = value1
            this.value2 = value2
        }
    }

    private fun initView(parent: ViewGroup?) {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.nutrition_layout, parent, false) as ViewGroup
        addView(itemView)
        tv_nutrition_details_title.text = title
        tv_articles_header.text = "Nutrients"
        tv_nutrition_item1_header.text = itemHeader1
        tv_nutrition_item2_header.text = itemHeader2
        nutriSegmentRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        nutriSegmentRecycler.adapter = NutriSegmentAdapter(context,segments)
    }

    private var foodViewModel: FoodViewModel? = null

    fun fillDetails(foodViewModel: FoodViewModel?){
        this.foodViewModel = foodViewModel
        tv_food_name.setText(foodViewModel?.triggerFoodItem?.value?.id)
        tv_food_qty.setText(""+ foodViewModel?.triggerFoodItem?.value?.qty)
        foodViewModel?.liveFoodResponse?.observe(context as BaseDIActivity, Observer {
            resource ->
            onViewModelDataLoaded(resource)

        })
       // foodViewModel?.triggerFoodItem?.value = foodViewModel?.triggerFoodItem?.value
    }

    private fun onViewModelDataLoaded(resource: Resource<FoodResponse>?) {
        Timber.d("foodViewModel?.liveFoodResponse? Data Changed : Status="+resource?.status+" : Source=" + resource?.dataSource)
        /*if(resource?.status == Status.SUCCESS && resource?.isFresh!!) {
            var resOld = recipeView.recipeViewModel?.liveFoodViewModelList?.value
            var resNew = resOld?.createCopy(resource?.status)
            recipeView.recipeViewModel?.liveFoodViewModelList?.value = resNew
        }*/
        onFoodLoaded(resource)
    }

    private fun onFoodLoaded(resource: Resource<FoodResponse>?) {
        //launcherBinding?.viewModel1 = launcherViewModel
        resource?.isFresh = false
        when(resource?.status){
            Status.LOADING ->{
                spin_kit.visibility = View.VISIBLE
            }
            Status.SUCCESS ->
            {
                spin_kit.visibility = View.INVISIBLE
                tv_food_name.text = resource.data?.food?.basicInfo?.name?.english
                tv_food_qty.setText(""+
                        foodViewModel?.triggerFoodItem?.value?.qty+"\n"+
                        resource.data?.food?.standardServing?.servingUnit)
                tv_food_calory.text = ""+foodViewModel?.getCaloriesPerStdServing()+
                        "\nKcal/"+resource.data?.food?.standardServing?.servingUnit
                tv_food_name.visibility = View.VISIBLE

                when(foodViewModel?.isVeg()){
                    true->tv_food_type.setTextColor(Color.parseColor("#ff669900"))
                    else->tv_food_type.setTextColor(Color.parseColor("#ffcc0000"))
                }
            }
            Status.ERROR ->
            {
                spin_kit.visibility = View.INVISIBLE
                tv_food_name.text = resource?.message
                tv_food_name.visibility = View.VISIBLE
            }
        }

    }



}