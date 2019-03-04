package com.techticz.app.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.RecipeListResponse
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.model.recipe.RecipeResponse
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.db.AppDatabase
import com.techticz.app.db.dao.BaseDao
import com.techticz.app.model.BrowseRecipeResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class RecipeRepository @Inject constructor(private val db: FirebaseFirestore,private val appDb: AppDatabase) : DocumentRepository<Recipe,BrowseRecipeResponse,RecipeResponse>(db,appDb), DocumentRepository.DocumentCallBack<Recipe> {

    private lateinit var listner: RecipeRepositoryCallback

    init {
        Timber.d("Injecting:" + this)
    }

    fun fetchRecipeResponse(recipeItem: RecipeItem?): MediatorLiveData<Resource<RecipeResponse>> {
        return fetchDocById(recipeItem?.id!!)
    }

    fun fetchRecipesWithText(triggerBrowse: String?): LiveData<Resource<BrowseRecipeResponse>>? {
        return browseDocsWithName(triggerBrowse)
    }

    fun createRecipe(recipe: Recipe, listner: RecipeRepositoryCallback) {
        this.listner = listner
        createDocument(recipe.id,recipe,this)
    }
    fun updateRecipe(recipe: Recipe, listner: RecipeRepositoryCallback) {
        this.listner = listner
        updateDocument(recipe.id,recipe,this)
    }

    fun sync(): LiveData<Resource<String>>? {
       return syncDocuments()
    }

    override fun getDocType(): Class<Recipe> {
        return Recipe::class.java
    }

    override fun getListResponseInstance(docs: List<Recipe>?): BrowseRecipeResponse {
        var resp = BrowseRecipeResponse()
        docs?.let{resp.recipes = it}
        return resp
    }

    override fun getResponseInstance(doc: Recipe?): RecipeResponse {
        var resp = RecipeResponse()
        doc?.let { resp.recipe = it }
        return resp
    }

    override fun getCollectionName(): String {
        return AppCollections.RECIPES.collectionName
    }

    override fun getDao(): BaseDao<Recipe> {
        return appDb.recipeDao()
    }

    override fun onDocCreated(doc: Recipe) {
        listner.onRecipeCreated(doc)
    }

    override fun onCreateDocFailure(msg:String) {
        listner.onCreateRecipeFailure(msg)
    }

    override fun onDocUpdated(doc: Recipe) {
        listner.onRecipeUpdated(doc)
    }

    override fun onDocUpdateFailure(msg:String) {
        listner.onRecipeUpdateFailure(msg)
    }

    fun fetchFeaturedRecipes(): LiveData<Resource<BrowseRecipeResponse>>? {
        return fetchAllDocsByProperty("featured", FieldPath.of("adminInfo","featured"),"1")
    }

    fun fetchMyRecipes(): LiveData<Resource<BrowseRecipeResponse>>? {
        return fetchAllDocsByMe()
    }

    interface RecipeRepositoryCallback {
        fun onRecipeUpdated(recipe: Recipe)
        fun onRecipeUpdateFailure(message: String)
        fun onRecipeCreated(recipe: Recipe)
        fun onCreateRecipeFailure(message: String)
    }
}