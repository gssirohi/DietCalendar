package com.techticz.app.ui.frag

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.food.Nutrition
import com.techticz.app.ui.customView.NutritionDetailsView
import com.techticz.dietcalendar.R;
import kotlinx.android.synthetic.main.fragment_nutrition_dialog.*
import kotlinx.android.synthetic.main.fragment_item_list_dialog_item.view.*

// TODO: Customize parameter argument names
const val ARG_TITLE = "dialog_title"
const val ARG_HEADER1 = "item_header1"
const val ARG_HEADER2 = "item_header2"


/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    NutritionDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [NutritionDialogFragment.Listener].
 */
class NutritionDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nutrition_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var title = arguments?.getString(ARG_TITLE)!!
        var header1 = arguments?.getString(ARG_HEADER1)!!
        var header2 = arguments?.getString(ARG_HEADER2)!!
        var nutri1 = mListener?.getNutrition1()
        var nutri2 = mListener?.getNutrition2()
        if(nutri1 != null && nutri2 != null) {
            ll_nutri_view_container.addView(NutritionDetailsView((activity as BaseDIActivity).activityCoordinatorLayout, title, header1, header2, nutri1!!, nutri2!!, NutritionDetailsView.MODE_RDA))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun getNutrition1(): Nutrition
        fun getNutrition2(): Nutrition
    }



    companion object {

        // TODO: Customize parameters
        fun newInstance(title: String,header1: String,header2: String): NutritionDialogFragment =
                NutritionDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putString(ARG_HEADER1, header1)
                        putString(ARG_HEADER2, header2)
                    }
                }

    }
}
