package com.techticz.app.repo

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import android.util.Log
import androidx.lifecycle.Observer
import com.google.firebase.firestore.*
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import com.techticz.app.db.AppDatabase
import com.techticz.app.db.dao.BaseDao
import com.techticz.auth.utils.LoginUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

abstract class DocumentRepository<DocT,M_RespT,S_RespT> constructor(private val db: FirebaseFirestore, private val appDb: AppDatabase) : BaseDIRepository() {

    abstract fun getDocType():Class<DocT>

    abstract fun getListResponseInstance(docs:List<DocT>?):M_RespT

    abstract fun getResponseInstance(docs:DocT?):S_RespT

    abstract fun getCollectionName():String

    abstract fun getDao():BaseDao<DocT>

    fun fetchAllPublishedDocs():LiveData<Resource<M_RespT>>{

        var resp = getListResponseInstance(null)
        var resource = Resource<M_RespT>(Status.LOADING, resp, "Loading published "+getCollectionName()+"s..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<M_RespT>> =  MediatorLiveData<Resource<M_RespT>>()
        live.value = resource
        //Thread.sleep(4*1000)
      //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)

        if(true){
            getDao().findAllByProperty("published","1")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {docs ->   if (docs != null) {
                        var fetchedRes = getListResponseInstance(docs)
                        var resource = Resource<M_RespT>(Status.SUCCESS, fetchedRes, "Loading Success- DB "+getCollectionName() , DataSource.LOCAL)
                        live.value = resource
                    } else {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- DB:" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }}
                    .doOnError {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- DB :" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }
                    .subscribe()
        } else {
            db.collection(getCollectionName())
                    .whereEqualTo(FieldPath.of("adminInfo", "published"), true)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var docs: List<DocT> = task.result!!.toObjects(getDocType())
                            //callback.onPlansFetched(plans)
                            var resp = getListResponseInstance(docs)
                            var resource = Resource<M_RespT>(Status.SUCCESS, resp, "Loading published " + getCollectionName() + "s is successful", DataSource.LOCAL)
                            live.value = resource
                        } else {
                            Log.e("Repo", "Loading published " + getCollectionName() + "s is failed!", task.exception)
                        }
                    }
        }
        return live
    }

    fun fetchAllFeaturedDocs():LiveData<Resource<M_RespT>>{

        var resp = getListResponseInstance(null)
        var resource = Resource<M_RespT>(Status.LOADING, resp, "Loading featured "+getCollectionName()+"s..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<M_RespT>> =  MediatorLiveData<Resource<M_RespT>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)

        if(true){
            getDao().findAllByProperty("featured","1")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {docs ->   if (docs != null) {
                        var fetchedRes = getListResponseInstance(docs)
                        var resource = Resource<M_RespT>(Status.SUCCESS, fetchedRes, "Loading Success- DB "+getCollectionName() , DataSource.LOCAL)
                        live.value = resource
                    } else {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- DB:" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }}
                    .doOnError {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- DB :" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }
                    .subscribe()
        } else {
            db.collection(getCollectionName())
                    .whereEqualTo(FieldPath.of("adminInfo", "featured"), true)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var docs: List<DocT> = task.result!!.toObjects(getDocType())
                            //callback.onPlansFetched(plans)
                            var resp = getListResponseInstance(docs)
                            var resource = Resource<M_RespT>(Status.SUCCESS, resp, "Loading featured " + getCollectionName() + "s is successful", DataSource.LOCAL)
                            live.value = resource
                        } else {
                            Log.e("Repo", "Loading featured " + getCollectionName() + "s is failed!", task.exception)
                        }
                    }
        }
        return live
    }

    fun fetchAllDocsByMe():LiveData<Resource<M_RespT>>{

        return fetchAllDocsByProperty("createdBy",FieldPath.of("adminInfo", "createdBy"),LoginUtils.getUserCredential())
        var resp = getListResponseInstance(null)
        var resource = Resource<M_RespT>(Status.LOADING, resp, "Loading my docs "+getCollectionName()+"s..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<M_RespT>> =  MediatorLiveData<Resource<M_RespT>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)

        if(true){
            getDao().findAllByProperty("createdBy","\""+LoginUtils.getUserCredential()+"\"")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {docs ->   if (docs != null) {
                        var fetchedRes = getListResponseInstance(docs)
                        var resource = Resource<M_RespT>(Status.SUCCESS, fetchedRes, "Loading Success- My Docs DB "+getCollectionName() , DataSource.LOCAL)
                        live.value = resource
                    } else {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- My Docs DB:" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }}
                    .doOnError {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- My Docs DB :" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }
                    .subscribe()
        } else {
            db.collection(getCollectionName())
                    .whereEqualTo(FieldPath.of("adminInfo", "createdBy"), LoginUtils.getUserCredential())
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var docs: List<DocT> = task.result!!.toObjects(getDocType())
                            //callback.onPlansFetched(plans)
                            var resp = getListResponseInstance(docs)
                            var resource = Resource<M_RespT>(Status.SUCCESS, resp, "Loading my docs " + getCollectionName() + "s is successful", DataSource.LOCAL)
                            live.value = resource
                        } else {
                            Log.e("Repo", "Loading my docs " + getCollectionName() + "s is failed!", task.exception)
                        }
                    }
        }
        return live
    }

    fun browseDocsWithName(browseText: String?): LiveData<Resource<M_RespT>>? {
        var resp = getListResponseInstance(null)
        var resource = Resource<M_RespT>(Status.LOADING, resp, "Searching "+getCollectionName()+"..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<M_RespT>> =  MediatorLiveData<Resource<M_RespT>>()
        live.value = resource

        if(true){
            getDao().browseAllByProperty("english",browseText!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {docs ->   if (docs != null) {
                        var resp = getListResponseInstance(docs)
                        var resource = Resource<M_RespT>(Status.SUCCESS, resp, "Loading Success- DB:"+getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    } else {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- DB "+getCollectionName() + browseText, DataSource.LOCAL)
                        live.value = resource
                    }}
                    .doOnError {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- DB :"+getCollectionName() +","+ browseText, DataSource.LOCAL)
                        live.value = resource
                    }
                    .subscribe()
        } else {
            db.collection(getCollectionName())
                    // .whereArrayContains(FieldPath.of("basicInfo","name","searchArray"),browseText!!)
                    .orderBy(FieldPath.of("basicInfo", "name", "english"))
                    .limit(10)
                    .startAt(browseText)
                    .endAt(browseText + '\uf8ff')
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var docs: List<DocT> = task.result!!.toObjects(getDocType())
                            //callback.onPlansFetched(plans)
                            var resp = getListResponseInstance(docs)
                            var resource = Resource<M_RespT>(Status.SUCCESS, resp, "Loading Success- "+getCollectionName(), DataSource.LOCAL)
                            live.value = resource
                        } else {
                            Log.e("Repo", "Loading Failed- "+getCollectionName(), task.exception)
                        }
                    }
        }
        return live
    }

    fun fetchDocById(docId: String?): MediatorLiveData<Resource<S_RespT>> {

        if(TextUtils.isEmpty(docId)){
            var resource = Resource<S_RespT>(Status.ERROR, null, "Loading document Failed:"+getCollectionName()+":"+docId, DataSource.LOCAL)
            var live : MediatorLiveData<Resource<S_RespT>> = MediatorLiveData<Resource<S_RespT>>()
            live.value = resource
            return live
        }

        var dummyRes = getResponseInstance(null)
        var resource = Resource<S_RespT>(Status.LOADING, dummyRes, "Loading document:"+getCollectionName()+":"+docId, DataSource.LOCAL)
        var live : MediatorLiveData<Resource<S_RespT>> = MediatorLiveData<Resource<S_RespT>>()
        live.value = resource

        if(true){
            getDao().findByProperty("id","\""+docId+"\"")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {
                        // intercepting error before exception
                    }.doOnError {
                        // intercepting error before exception
                    }.subscribe({
                        //success
                        doc ->   if (doc != null) {
                           var fetchedRes = getResponseInstance(doc)
                           var resource = Resource<S_RespT>(Status.SUCCESS, fetchedRes, "Loading Success- Doc DB "+getCollectionName() , DataSource.LOCAL)
                           live.value = resource
                        } else {
                           var resource = Resource<S_RespT>(Status.ERROR, null, "Loading Failed- Doc DB:" +getCollectionName(), DataSource.LOCAL)
                           live.value = resource
                        }
                    },{
                        //error
                        Log.e("DOCUMENT","ERROR loading :"+docId)
                        var resource = Resource<S_RespT>(Status.ERROR, null, "Loading Failed- Doc DB :" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    })
        } else {
            db.collection(getCollectionName()).document(docId!!)
                    .get().addOnSuccessListener { documentSnapshot ->
                        val doc = documentSnapshot.toObject(getDocType())
                        if (doc != null) {
                            var fetchedRes = getResponseInstance(doc)
                            var resource = Resource<S_RespT>(Status.SUCCESS, fetchedRes, "Loading Success- "+getCollectionName()+":" + docId, DataSource.REMOTE)
                            live.value = resource
                        } else {
                            var resource = Resource<S_RespT>(Status.ERROR, null, "Loading Failed- "+getCollectionName()+":" + docId, DataSource.REMOTE)
                            live.value = resource
                        }
                    }.addOnFailureListener { e ->
                        Timber.e("Error fetching "+getCollectionName()+":" + docId)
                        var resource = Resource<S_RespT>(Status.ERROR, null, "Couldn't load- "+getCollectionName()+":" + docId, DataSource.REMOTE)
                        live.value = resource
                    }
        }
        return live
    }

    fun createDocument(id:String,newDoc: DocT,listner:DocumentCallBack<DocT>) {
        Timber.d("creating document:"+getCollectionName()+":"+id)
        if(true){
            getDao().insert(newDoc)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {index ->   if (index != null) {
                        listner.onDocCreated(newDoc)
                    } else {
                        listner.onCreateDocFailure(getCollectionName()+" could not be created!")
                    }}
                    .doOnError {
                        listner.onCreateDocFailure(getCollectionName()+" create failed with exception!")
                    }
                    .subscribe()
        } else {
            var batch: WriteBatch = db.batch()
            var ref: DocumentReference = db.collection(getCollectionName()).document(id)
            batch.set(ref, newDoc as Any)
            batch.commit()
                    .addOnSuccessListener { task ->
                        var message = getCollectionName() + " create plan success"
                        Timber.d(message)
                        listner.onDocCreated(newDoc)
                    }
                    .addOnFailureListener { task ->
                        var message = getCollectionName() + " create plan failed"
                        Timber.e(message)
                        listner.onCreateDocFailure(message)
                    }
        }
    }

    fun updateDocument(id:String,newDoc: DocT,listner:DocumentCallBack<DocT>) {
        Timber.d("updating document:"+getCollectionName()+":"+id)
        if(true){
            getDao().update(newDoc)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {index ->   if (index != null) {
                        listner.onDocUpdated(newDoc)
                    } else {
                        listner.onDocUpdateFailure(getCollectionName()+" could not updated!")
                    }}
                    .doOnError {
                        listner.onDocUpdateFailure(getCollectionName()+" date failed with exception")
                    }
                    .subscribe()
        } else {
            var batch: WriteBatch = db.batch()
            var ref: DocumentReference = db.collection(getCollectionName()).document(id)
            batch.set(ref, newDoc as Any)
            batch.commit()
                    .addOnSuccessListener { task ->
                        var message = getCollectionName() + " update plan success"
                        Timber.d(message)
                        listner.onDocCreated(newDoc)
                    }
                    .addOnFailureListener { task ->
                        var message = getCollectionName() + " update plan failed"
                        Timber.e(message)
                        listner.onDocUpdateFailure(getCollectionName()+" could not updated!")
                    }
        }
    }

    fun syncDocuments(): LiveData<Resource<String>>? {
        var resp = "syncing"
        var resource = Resource<String>(Status.LOADING, resp, "Syncing "+getCollectionName()+"..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<String>> =  MediatorLiveData<Resource<String>>()
        live.value = resource

        db.collection(getCollectionName())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var docs :List<DocT> = task.result!!.toObjects(getDocType())
                        getDao().insert(docs)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .doOnSuccess {
                                    Log.d("Repo", "Syncing success -"+getCollectionName()+"s")
                                    var resource = Resource<String>(Status.SUCCESS, "synced", "Syncing Success- "+getCollectionName(), DataSource.LOCAL)
                                    live.value = resource
                                }.doOnError {
                                    Log.e("Repo", "Syncing error - "+getCollectionName())
                                    var resource = Resource<String>(Status.ERROR, "synced", "Syncing Error- "+getCollectionName(), DataSource.LOCAL)
                                    live.value = resource
                                }.subscribe()

                    } else {
                        Log.e("Repo", "Syncing Failed- "+getCollectionName(), task.exception)
                        var resource = Resource<String>(Status.ERROR, "sync_failed", "Syncing Error- "+getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }
                }
        return live
    }

    fun fetchAllDocsByProperty(dbProperty:String,path:FieldPath,value:String):LiveData<Resource<M_RespT>>{
        var resp = getListResponseInstance(null)
        var resource = Resource<M_RespT>(Status.LOADING, resp, "Loading docs "+getCollectionName()+"s..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<M_RespT>> =  MediatorLiveData<Resource<M_RespT>>()
        live.value = resource

        if(true){
            getDao().findAllByProperty(dbProperty,"\""+value+"\"")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {docs ->   if (docs != null) {
                        var fetchedRes = getListResponseInstance(docs)
                        var resource = Resource<M_RespT>(Status.SUCCESS, fetchedRes, "Loading Success- Docs DB "+getCollectionName() , DataSource.LOCAL)
                        live.value = resource
                    } else {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- Docs DB:" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }}
                    .doOnError {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- Docs DB :" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }
                    .subscribe()
        } else {
            db.collection(getCollectionName())
                    .whereEqualTo(path, value)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var docs: List<DocT> = task.result!!.toObjects(getDocType())
                            //callback.onPlansFetched(plans)
                            var resp = getListResponseInstance(docs)
                            var resource = Resource<M_RespT>(Status.SUCCESS, resp, "Loading docs " + getCollectionName() + "s is successful", DataSource.LOCAL)
                            live.value = resource
                        } else {
                            Log.e("Repo", "Loading docs " + getCollectionName() + "s is failed!", task.exception)
                        }
                    }
        }
        return live
    }

    fun fetchAllDocsWhereArrayContains(dbProperty:String,path:FieldPath,value:String):LiveData<Resource<M_RespT>>{
        var resp = getListResponseInstance(null)
        var resource = Resource<M_RespT>(Status.LOADING, resp, "Loading docs "+getCollectionName()+"s..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<M_RespT>> =  MediatorLiveData<Resource<M_RespT>>()
        live.value = resource

        if(true){
            getDao().browseAllByProperty(dbProperty,value)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {docs ->   if (docs != null) {
                        var fetchedRes = getListResponseInstance(docs)
                        var resource = Resource<M_RespT>(Status.SUCCESS, fetchedRes, "Loading Success- Docs DB "+getCollectionName() , DataSource.LOCAL)
                        live.value = resource
                    } else {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- Docs DB:" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }}
                    .doOnError {
                        var resource = Resource<M_RespT>(Status.ERROR, null, "Loading Failed- Docs DB :" +getCollectionName(), DataSource.LOCAL)
                        live.value = resource
                    }
                    .subscribe()
        } else {
            db.collection(getCollectionName())
                    .whereArrayContains(path, value)
                    .limit(30)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var docs: List<DocT> = task.result!!.toObjects(getDocType())
                            //callback.onPlansFetched(plans)
                            var resp = getListResponseInstance(docs)
                            var resource = Resource<M_RespT>(Status.SUCCESS, resp, "Loading docs " + getCollectionName() + "s is successful", DataSource.LOCAL)
                            live.value = resource
                        } else {
                            Log.e("Repo", "Loading docs " + getCollectionName() + "s is failed!", task.exception)
                        }
                    }
        }
        return live
    }
    init {

    }

    public interface DocumentCallBack<DocT>{
        fun onDocCreated(doc: DocT)
        fun onCreateDocFailure(msg:String)
        fun onDocUpdated(doc:DocT)
        fun onDocUpdateFailure(msg:String)
    }
}