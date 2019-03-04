package com.techticz.app.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.BrowseDietPlanResponse
import com.techticz.app.model.DietPlanResponse
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import com.techticz.app.db.AppDatabase
import com.techticz.app.db.dao.BaseDao
import com.techticz.auth.utils.LoginUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class DietPlanRepository @Inject constructor(private val db: FirebaseFirestore,private val appDb: AppDatabase) : DocumentRepository<DietPlan,BrowseDietPlanResponse,DietPlanResponse>(db,appDb), DocumentRepository.DocumentCallBack<DietPlan> {
    private lateinit var listner: DietPlanCallBack

    init {

    }

    val browsePublishedDietPlansResponseData: LiveData<Resource<BrowseDietPlanResponse>>
        get() {
            return fetchAllPublishedDocs()
        }

    val browseMyDietPlansResponseData: LiveData<Resource<BrowseDietPlanResponse>>
        get() {
            return fetchAllDocsByMe()
        }

    fun fetchDietPlan(dietPlanId: String?): MediatorLiveData<Resource<DietPlanResponse>> {
        return fetchDocById(dietPlanId!!)
    }


    fun createDietPlan(newPlan: DietPlan, listner:DietPlanCallBack) {
        this.listner = listner
        createDocument(newPlan.id,newPlan,this)
    }

    fun updatePlan(dietPlan: DietPlan?, listner: DietPlanCallBack) {
        this.listner = listner
        updateDocument(dietPlan?.id!!,dietPlan,this)
    }

    fun sync(): LiveData<Resource<String>>? {
        return syncDocuments()
    }

    override fun onDocCreated(doc: DietPlan) {
        listner.onPlanCreated(doc)
    }

    override fun onCreateDocFailure(msg:String) {
        listner.onCreatePlanFailure()
    }

    override fun onDocUpdated(doc: DietPlan) {
        listner.onPlanUpdated(doc)
    }

    override fun onDocUpdateFailure(msg:String) {
        listner.onPlanUpdateFailure()
    }

    override fun getDao(): BaseDao<DietPlan> {
        return appDb.planDao()
    }

    override fun getDocType(): Class<DietPlan> {
        return DietPlan::class.java
    }

    override fun getListResponseInstance(docs: List<DietPlan>?): BrowseDietPlanResponse {
        var res = BrowseDietPlanResponse()
        docs?.let{res.plans = it}
        return res
    }

    override fun getResponseInstance(doc: DietPlan?): DietPlanResponse {
        var res = DietPlanResponse()
        doc?.let{res.dietPlan = it}
        return res
    }

    override fun getCollectionName(): String {
        return AppCollections.PLANS.collectionName
    }

    interface DietPlanCallBack{
        fun onPlanCreated(planId: DietPlan)
        fun onCreatePlanFailure()
        fun onPlanUpdated(plan:DietPlan)
        fun onPlanUpdateFailure()
    }
}