package com.techticz.app.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.FoodResponse
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import com.techticz.app.db.AppDatabase
import com.techticz.app.db.dao.BaseDao
import com.techticz.app.model.BrowseFoodResponse
import timber.log.Timber
import javax.inject.Inject
import com.techticz.app.model.food.Food
import com.techticz.app.model.mealplate.FoodItem
import com.techticz.networking.livedata.AbsentLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class FoodRepository @Inject constructor(private val db: FirebaseFirestore,private val appDb: AppDatabase) : DocumentRepository<Food, BrowseFoodResponse,FoodResponse>(db,appDb) {
    init {
        Timber.d("Injecting:" + this)
    }

    fun fetchFoodResponse(foodItem: FoodItem?): LiveData<Resource<FoodResponse>> {
        return fetchDocById(foodItem?.id!!)
    }

    fun fetchFoodsWithText(triggerBrowse: String?): LiveData<Resource<BrowseFoodResponse>>? {
        return browseDocsWithName(triggerBrowse)

    }

    fun fetchFoodsWithCategory(triggerCategory: String?): LiveData<Resource<BrowseFoodResponse>>? {
        return fetchAllDocsByProperty("category", FieldPath.of("basicInfo","category"),triggerCategory!!)
    }

    fun fetchFoodsForNutrient(nutrientName: String?): LiveData<Resource<BrowseFoodResponse>>? {
        return fetchAllDocsOrderByProperty(nutrientName!!, FieldPath.of("basicInfo","category"),"DESC")
    }

    fun sync(): LiveData<Resource<String>>? {
        return syncDocuments()
    }


    override fun getDocType(): Class<Food> {
        return Food::class.java
    }

    override fun getListResponseInstance(docs: List<Food>?): BrowseFoodResponse {
        var resp = BrowseFoodResponse()
        docs?.let{resp.foods = docs}
        return resp
    }

    override fun getResponseInstance(doc: Food?): FoodResponse {
        var resp = FoodResponse()
        doc?.let{resp.food = doc}
        return resp
    }

    override fun getCollectionName(): String {
        return AppCollections.FOODS.collectionName
    }

    override fun getDao(): BaseDao<Food> {
        return appDb.foodDao()
    }

}