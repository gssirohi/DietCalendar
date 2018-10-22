package com.techticz.app.viewmodel

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import android.graphics.Bitmap
import com.techticz.app.model.ImageResponse
import com.techticz.app.repo.ImageRepository
import com.techticz.dietcalendar.ui.DietCalendarApplication
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.powerkit.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class ImageViewModel @Inject
constructor(context: Context) : BaseViewModel() {
    val triggerImageUrl = MutableLiveData<String>()
    val liveImageResponse: LiveData<Resource<ImageResponse>>
    @Inject
    lateinit var injectedRepo:ImageRepository
    init {
        DietCalendarApplication.getAppComponent().inject(this)
        Timber.d("Injecting:" + this)
        liveImageResponse = Transformations.switchMap(triggerImageUrl) { triggerLaunch ->
            if (triggerLaunch == null) {
                return@switchMap AbsentLiveData.create<Resource<ImageResponse>>()
            } else {
                Timber.d("Image Trigger detected for:" + triggerLaunch)
               // injectedRepo.hostActivityContext = context
                //injectedRepo.initImageCache()
                return@switchMap injectedRepo.fetchImageResponse(triggerImageUrl.value)
                //return@switchMap imageRepository.fetchImageResponse(triggerImageUrl.value)
            }
        }
    }

    @SuppressLint("ResourceType")
    fun getBitmap(): Bitmap?{
        when(liveImageResponse.value?.status){
            Status.LOADING->{
                return null
            }
            Status.SUCCESS->{
                return liveImageResponse?.value?.data?.bitmap
            }
            Status.EMPTY->{
                return null
            }
            Status.ERROR->{
                return null
            }
        }
        return null
    }
}