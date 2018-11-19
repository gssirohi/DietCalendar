package com.techticz.app.base

import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
open class BaseDIFragment : androidx.fragment.app.Fragment(), Injectable {

    val viewModelFactory: ViewModelProvider.Factory?
        @Throws(Exception::class)
        get() {
            if (activity != null) {
                if (activity is BaseDIActivity) {
                    return (activity as BaseDIActivity).viewModelFactory
                }
            } else {
                throw Exception("Couldn't provide viewModelFactory as Fragment's Activity does not extend BaseDIActivity")
            }
            return null
        }

    val toolbar: androidx.appcompat.widget.Toolbar?
        get() = if (activity is BaseDIActivity) {
            (activity as BaseDIActivity).activityToolbar
        } else {
            null
        }

    fun showProgress() {
        if (activity is BaseDIActivity) {
            (activity as BaseDIActivity).showProgress()
        }
    }

    fun hideProgress() {
        if (activity is BaseDIActivity) {
            (activity as BaseDIActivity).hideProgress()
        }
    }

    fun showError(message: String) {
        if (activity is BaseDIActivity) {
            (activity as BaseDIActivity).showToast(message)
        }
    }

    fun showSuccess(message: String) {
        if (activity is BaseDIActivity) {
            (activity as BaseDIActivity).showToast(message)
        }
    }
}