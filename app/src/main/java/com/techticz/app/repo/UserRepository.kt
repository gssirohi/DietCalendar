package com.techticz.app.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.UserResponse
import com.techticz.app.model.user.User
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class UserRepository @Inject constructor(private val prefRepo:PrefRepo,private val db: FirebaseFirestore) : BaseDIRepository() {

    fun fetchUserResponse(userId: String?): LiveData<Resource<UserResponse>> {

        var dummyRes = UserResponse()
        var resource = Resource<UserResponse>(Status.LOADING, dummyRes, "Loading User:" + userId, DataSource.LOCAL)
        var live : MediatorLiveData<Resource<UserResponse>> = MediatorLiveData<Resource<UserResponse>>()
        live.value = resource

        if(userId.equals("local")) {
            var user = prefRepo.user
            if (user != null) {
                var fetchedRes = UserResponse()
                fetchedRes.user = user
                var resource = Resource<UserResponse>(Status.SUCCESS, fetchedRes, "Loading Offline Success- User:" + userId, DataSource.LOCAL)
                live.value = resource
            } else {
                var resource = Resource<UserResponse>(Status.EMPTY, UserResponse(), "Loading Offline Failed- User Empty:" + userId, DataSource.LOCAL)
                live.value = resource
            }
        } else {
            db.collection(AppCollections.USERS.collectionName).document(userId!!)
                    .get().addOnSuccessListener { documentSnapshot ->
                        Timber.i("Success fetching User docID:" + documentSnapshot.id)
                        val user = documentSnapshot.toObject(User::class.java!!)
                        if (user != null) {
                            var fetchedRes = UserResponse()
                            fetchedRes.user = user
                            var resource = Resource<UserResponse>(Status.SUCCESS, fetchedRes, "Loading Success- User:" + userId, DataSource.REMOTE)
                            live.value = resource
                        } else {
                            var resource = Resource<UserResponse>(Status.EMPTY, UserResponse(), "Loading Failed- User Empty:" + userId, DataSource.REMOTE)
                            live.value = resource
                        }
                    }.addOnFailureListener { e ->
                        Timber.e("Error fetching User: %s" + userId, e)
                        var resource = Resource<UserResponse>(Status.ERROR, UserResponse(), "Couldn't load- User:" + userId, DataSource.REMOTE)
                        live.value = resource
                    }
        }

    return live
    }


    fun registerUser(user: User, listner: UserProfileCallback){
        Timber.d("Registering user..:"+user.id)
        if(user.id.equals("local")){
            prefRepo.user = user
            listner.onUserRegistered(user.id)
        } else {
            var batch: WriteBatch = db.batch()
            var ref: DocumentReference = db.collection(AppCollections.USERS.collectionName).document(user.id)
            batch.set(ref, user)
            batch.commit()
                    .addOnSuccessListener { task ->
                        var message = AppCollections.USERS.collectionName.toString() + " register success"
                        Timber.d(message)
                        //hideProgress()
                        //showSuccess(message!!)
                        listner.onUserRegistered(user.id)
                    }
                    .addOnFailureListener { task ->
                        var message = AppCollections.USERS.collectionName + " register failed"
                        Timber.e(message)
                        //hideProgress()
                        //showError(message!!)
                        listner.onUserRegistrationFailure()
                    }
        }
    }

    fun updateUser(user: User, listner: UserProfileCallback){
        if(user.id.equals("local")){
            prefRepo.user = user
            listner.onUserUpdated(user.id)
        } else {
            var batch: WriteBatch = db.batch()
            var ref: DocumentReference = db.collection(AppCollections.USERS.collectionName).document(user.id)
            batch.set(ref, user)
            batch.commit()
                    .addOnSuccessListener { task ->
                        var message = AppCollections.USERS.collectionName.toString() + " update success"
                        Timber.d(message)
                        listner.onUserUpdated(user.id)
//                    hideProgress()
//                    showSuccess(message!!)
                    }
                    .addOnFailureListener { task ->
                        var message = AppCollections.USERS.collectionName + " update failed"
                        Timber.d(message)
                        listner.onUserUpdateFailure()
                        //                   hideProgress()
                        //                  showError(message!!)
                    }
        }
    }

    init {

    }

interface UserProfileCallback{
    fun onUserRegistered(userId:String)
    fun onUserRegistrationFailure()
    fun onUserUpdated(id: String)
    fun onUserUpdateFailure()
}

}