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
import com.techticz.app.base.BaseDIRepository
import com.techticz.app.model.BrowseRecipeResponse
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class RecipeRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {

    fun fetchRecipeResponse(recipeItem: RecipeItem?): MediatorLiveData<Resource<RecipeResponse>> {

        var dummyRes = RecipeResponse()
        var resource = Resource<RecipeResponse>(Status.LOADING, dummyRes, "Loading Recipe:"+recipeItem?.id, DataSource.LOCAL)
        var live : MediatorLiveData<Resource<RecipeResponse>> = MediatorLiveData<Resource<RecipeResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)

         db.collection(AppCollections.RECIPES.collectionName).document(recipeItem?.id!!)
                    .get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val recipe = documentSnapshot.toObject(Recipe::class.java!!)
                var fetchedRes = RecipeResponse()
             if(recipe != null) {
                 fetchedRes.recipe = recipe
                 var resource = Resource<RecipeResponse>(Status.SUCCESS, fetchedRes, "Loading Success- Recipe:"+recipeItem?.id, DataSource.REMOTE)
                 live.value = resource
             } else {
                 Timber.e("Recipe found NULL for ID:" + recipeItem?.id)
                 var resource = Resource<RecipeResponse>(Status.ERROR, null, "Loading Failed- Recipe:"+recipeItem?.id, DataSource.REMOTE)
                 live.value = resource
             }
            }).addOnFailureListener { e -> Timber.e("Error fetching recipe:" + recipeItem?.id, e)
             var resource = Resource<RecipeResponse>(Status.ERROR, null, "Couldn't load- Recipe:"+recipeItem?.id, DataSource.REMOTE)
             live.value = resource}

        return live
    }

    fun fetchRecipeListResponse(recipeItems: List<RecipeItem>?): LiveData<Resource<RecipeListResponse>> {

        var dummyRes = RecipeListResponse()
        var resource = Resource<RecipeListResponse>(Status.LOADING, dummyRes, "Loading recipe list..", DataSource.LOCAL)
        var live : MediatorLiveData<Resource<RecipeListResponse>> = MediatorLiveData<Resource<RecipeListResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
      //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)

        for (recipeId in recipeItems!!) {
            db.collection(AppCollections.RECIPES.collectionName).document(recipeId.id!!)
                    .get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val recipe = documentSnapshot.toObject(Recipe::class.java!!)
                var fetchedRes = RecipeListResponse()
                fetchedRes.recipes?.add(recipe!!)
                var resource = Resource<RecipeListResponse>(Status.SUCCESS, fetchedRes, "Data Loading Success", DataSource.REMOTE)
                live.value = resource
            }).addOnFailureListener { e -> Timber.e("Error fetching recipe:" + recipeId, e)
                var resource = Resource<RecipeListResponse>(Status.ERROR, null, "Data Loading Failed", DataSource.REMOTE)
                live.value = resource}
        }
    return live
    }

    fun fetchRecipesWithText(triggerBrowse: String?): LiveData<Resource<BrowseRecipeResponse>>? {
        var resp = BrowseRecipeResponse()
        var resource = Resource<BrowseRecipeResponse>(Status.LOADING, resp, "Loading recipes..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<BrowseRecipeResponse>> =  MediatorLiveData<Resource<BrowseRecipeResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        //  var resourceS = Resource<BrowseDietPlanResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)


        db.collection(AppCollections.RECIPES.collectionName)
                // .whereArrayContains(FieldPath.of("basicInfo","name","searchArray"),triggerBrowse!!)
                .orderBy(FieldPath.of("basicInfo","name","english"))
                .limit(10)
                .startAt(triggerBrowse)
                .endAt(triggerBrowse + '\uf8ff')
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var recipes :List<Recipe> = task.result!!.toObjects(Recipe::class.java)
                        //callback.onPlansFetched(plans)
                        var resp = BrowseRecipeResponse()
                        resp.recipes = recipes
                        var resource = Resource<BrowseRecipeResponse>(Status.SUCCESS, resp, "Loading Success- recipes", DataSource.LOCAL)
                        live.value = resource
                    } else {
                        Log.e("Repo", "Loading Failed- recipes", task.exception)
                    }
                }
        return live
    }

    fun createRecipe(recipe: Recipe, listner: RecipeRepositoryCallback) {
        Timber.d("Creating recipe..:"+recipe.id)
        var batch: WriteBatch = db.batch()
        var ref: DocumentReference = db.collection(AppCollections.RECIPES.collectionName).document(recipe.id)
        batch.set(ref, recipe)
        batch.commit()
                .addOnSuccessListener { task ->
                    var message =  AppCollections.RECIPES.collectionName.toString()+" create recipe success"
                    Timber.d(message)
                    //hideProgress()
                    //showSuccess(message!!)
                    listner.onRecipeCreated(recipe)
                }
                .addOnFailureListener { task ->
                    var message =  AppCollections.RECIPES.collectionName+" create recipe failed"
                    Timber.e(message)
                    //hideProgress()
                    //showError(message!!)
                    listner.onCreateRecipeFailure(message)
                }
    }
    fun updateRecipe(recipe: Recipe, listner: RecipeRepositoryCallback) {
        Timber.d("update recipe..:"+recipe?.id)
        var batch: WriteBatch = db.batch()
        var ref: DocumentReference = db.collection(AppCollections.RECIPES.collectionName).document(recipe?.id!!)
        batch.set(ref, recipe!!)
        batch.commit()
                .addOnSuccessListener { task ->
                    var message =  AppCollections.RECIPES.collectionName.toString()+" update recipe success"
                    Timber.d(message)
                    //hideProgress()
                    //showSuccess(message!!)
                    listner.onRecipeUpdated(recipe)
                }
                .addOnFailureListener { task ->
                    var message =  AppCollections.RECIPES.collectionName+" update recipe failed"
                    Timber.e(message)
                    //hideProgress()
                    //showError(message!!)
                    listner.onRecipeUpdateFailure(message)
                }
    }

    init {
        Timber.d("Injecting:" + this)
    }

    interface RecipeRepositoryCallback {
        fun onRecipeUpdated(recipe: Recipe)
        fun onRecipeUpdateFailure(message: String)
        fun onRecipeCreated(recipe: Recipe)
        fun onCreateRecipeFailure(message: String)
    }





}