package com.techticz.app.viewmodel

import android.arch.lifecycle.*
import android.content.Context

import com.techticz.app.model.UserResponse

import com.techticz.app.repo.UserRepository
import com.techticz.networking.livedata.AbsentLiveData
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseViewModel
import com.techticz.app.model.user.User
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 1/12/17.
 */

class UserViewModel @Inject
constructor() : BaseViewModel() {
    @Inject
    lateinit var injectedRepo: UserRepository
    val triggerUserId = MutableLiveData<String>()
    val liveUserResponse: LiveData<Resource<UserResponse>>
    var liveImage: MediatorLiveData<Resource<ImageViewModel>>? = MediatorLiveData<Resource<ImageViewModel>>()
    init {

        liveUserResponse = Transformations.switchMap(triggerUserId) { triggerUserId ->
            if (triggerUserId == null) {
                return@switchMap AbsentLiveData.create<Resource<UserResponse>>()
            } else {
                Timber.d("User Trigger detected for:" + triggerUserId)
                var liveImageResource = Resource<ImageViewModel>(Status.EMPTY, null, "Empty image user..", DataSource.LOCAL)
                liveImage?.value = liveImageResource
                return@switchMap injectedRepo.fetchUserResponse(this.triggerUserId.value)
            }
        }
    }


    fun autoLoadChildren(lifecycleOwner: LifecycleOwner) {
        liveUserResponse?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){
                Status.SUCCESS ->{
                    //load children view model here
                    observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner)
                }
            }
        })
    }

    private fun observeAndLoadChildrenViewModelsIfRequired(lifecycleOwner: LifecycleOwner) {
        liveImage?.observe(lifecycleOwner, Observer { resource->
            when(resource?.status){ Status.EMPTY ->loadImageViewModel(lifecycleOwner)}
        })
    }

    private fun loadImageViewModel(lifecycleOwner: LifecycleOwner) {
        var imageViewModel = ImageViewModel(lifecycleOwner as Context)
        imageViewModel.triggerImageUrl.value = liveUserResponse?.value?.data?.user?.basicInfo?.image
        var imageRes = Resource<ImageViewModel>(Status.SUCCESS, imageViewModel, "Loading user image model success..", DataSource.REMOTE)
        liveImage?.value = imageRes
    }

    var registrationAttampted: Boolean = false

    fun registerUser(newUser: User, listner: UserRepository.UserProfileCallback) {
        registrationAttampted = true
        injectedRepo.registerUser(newUser,listner)
    }

    fun updateUser(user: User?, listner: UserRepository.UserProfileCallback) {
        if(user != null){
            injectedRepo.updateUser(user!!,listner)
        }
    }


}