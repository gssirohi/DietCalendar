package com.techticz.app.constants

import org.jetbrains.annotations.NotNull

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 6/10/18.
 */
enum class FoodServings(val serving: String,val servingLabel: String,val pinchEquivalent:Int) {
    PINCH("pinch","Pinch",1),
    DASH("dash","Dash",1),
    DROP("drop","Drop",1),
    TEA_SPOON("tspn","Tea Spoon",8),
    VERY_SMALL_PORTION("vsmp","V.Small Portion",8),
    TABLE_SPOON("tblspn","Table Spoon",24),
    SLICE("slice","Slice",24),
    HAND_FULL("hndfl","Hand Full",24),
    SMALL_PORTION("smp","Small Portion",24),
    MEDIUM_PORTION("mdp","Medium Portion",192),
    CUP("cup","Cup",384),
    STANDARD_PORTION("stdp","Standard Portion",384),
    PINT("pint","Pint",768),
    BOWL("bowl","Bowl",768),
    GLASS("glass","Glass",768),
    LARGE_PORTION("lrgp","Large Portion",768);

    fun getGrams(gramsInStdPortion: Int?): Float {
        var perPinch = gramsInStdPortion!!.toFloat() / STANDARD_PORTION.pinchEquivalent
        return perPinch*pinchEquivalent
    }

    companion object {

        fun getEnum(serving: String): FoodServings {
            var values = FoodServings.values();
            for(serve in values){
                if(serve.serving.equals(serving,true)){
                    return serve;
                }
            }
            return STANDARD_PORTION
        }
        fun getEnumByLabel(label: String): FoodServings {
            var values = FoodServings.values();
            for(serve in values){
                if(serve.servingLabel.equals(label,true)){
                    return serve;
                }
            }
            return STANDARD_PORTION
        }
        fun getAllNames(): List<String> {
            var values = FoodServings.values();
            var list = ArrayList<String>()
            for(serve in values){
                list.add(serve.servingLabel)
            }
            return list
        }

    }

}