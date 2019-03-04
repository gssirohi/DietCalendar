package com.techticz.app.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import android.text.TextUtils
import android.util.Log
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.MealPlateResponse
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import timber.log.Timber
import javax.inject.Inject
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.techticz.app.db.AppDatabase
import com.techticz.app.db.dao.BaseDao
import com.techticz.app.model.BrowsePlateResponse
import com.techticz.app.model.meal.Meal
import com.techticz.auth.utils.LoginUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class MealPlateRepository @Inject constructor(private val db: FirebaseFirestore,private val appDb: AppDatabase) : DocumentRepository<MealPlate,BrowsePlateResponse,MealPlateResponse>(db,appDb), DocumentRepository.DocumentCallBack<MealPlate> {

    private lateinit var listner: PlateRepositoryCallback

    fun fetchMealPlateResponse(meal: Meal?): LiveData<Resource<MealPlateResponse>> {
        return fetchDocById(meal?.mealPlateId)
    }

    fun fetchPlatesWithText(triggerBrowse: String?): LiveData<Resource<BrowsePlateResponse>>? {
        return browseDocsWithName(triggerBrowse)
    }

    fun fetchPlatesForMealType(mealType: String?): LiveData<Resource<BrowsePlateResponse>>? {
        return fetchAllDocsWhereArrayContains("prefMeals",FieldPath.of("basicProperty","prefMeals"),mealType!!)
    }

    fun fetchAllFeaturedPlates(): LiveData<Resource<BrowsePlateResponse>>? {
        return fetchAllFeaturedDocs()
    }

    fun fetchPlatesForUser(): LiveData<Resource<BrowsePlateResponse>>? {
       return fetchAllDocsByMe()
    }

    fun updatePlate(plate: MealPlate, listner: PlateRepositoryCallback) {
        this.listner = listner
        updateDocument(plate.id!!,plate,this)
    }

    fun createPlate(plate: MealPlate, listner: PlateRepositoryCallback) {
       this.listner = listner
        createDocument(plate.id,plate,this)
    }


    fun sync(): LiveData<Resource<String>>? {
        return syncDocuments()
    }

    init {
        Timber.d("Injecting:" + this)
    }

    override fun getDocType(): Class<MealPlate> {
        return MealPlate::class.java
    }

    override fun getListResponseInstance(docs: List<MealPlate>?): BrowsePlateResponse {
        var resp = BrowsePlateResponse()
        docs?.let{resp.plates = it}
        return resp
    }

    override fun getResponseInstance(doc: MealPlate?): MealPlateResponse {
        var resp = MealPlateResponse()
        doc?.let { resp.mealPlate = it }
        return resp
    }

    override fun getCollectionName(): String {
        return AppCollections.PLATES.collectionName
    }

    override fun getDao(): BaseDao<MealPlate> {
        return appDb.plateDao()
    }

    override fun onDocCreated(doc: MealPlate) {
        listner.onPlateCreated(doc)
    }

    override fun onCreateDocFailure(msg: String) {
        listner.onCreatePlateFailure(msg)
    }

    override fun onDocUpdated(doc: MealPlate) {
        listner.onPlateUpdated(doc)
    }

    override fun onDocUpdateFailure(msg: String) {
        listner.onPlateUpdateFailure(msg)
    }


    interface PlateRepositoryCallback {
        fun onPlateUpdated(plate: MealPlate)
        fun onPlateUpdateFailure(message: String)
        fun onPlateCreated(plate: MealPlate)
        fun onCreatePlateFailure(message: String)
    }





}