package com.techticz.dietcalendar.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import com.techticz.dietcalendar.model.LauncherResponse
import com.techticz.networking.model.AppExecutors
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.networking.util.RateLimiter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.support.annotation.NonNull
import android.util.Log
import android.util.Xml
import com.google.android.gms.common.util.IOUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.techticz.app.model.User

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techticz.app.model.food.*
import com.techticz.app.model.food.category.FoodCategory
import com.techticz.app.model.recipe.category.RecipeCategory
import com.techticz.app.model.serving.ServingType
import com.techticz.powerkit.utils.JSONUtils
import java.lang.reflect.Type


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class AppRepository @Inject
constructor(private val appExecutors: AppExecutors/*, private val syncPrefDao: SyncPrefDao*/, private val db: FirebaseFirestore,private val context:Context) {
    // Fetch again if the data is older than 48 hours
    private val rateLimiter = RateLimiter<String>(48, TimeUnit.HOURS)

    //HotelParcel.buildHotelParcel(hotelDetailResponseContainer);
    //
    val launcherResponse: LiveData<Resource<LauncherResponse>>
        get() {
            Timber.d("Accessing:" + this + "[launchResponse]")
            return fetchLauncherResponse()
        }

    private fun fetchLauncherResponse(): LiveData<Resource<LauncherResponse>> {
        //addFood()
        //addFoodCategories()
        //addRecipeCategories()
        addServingTypes()
       // getUsers()
        var resp = LauncherResponse()
        resp.launchMessage = "This is local launch message not from network or DB"
        var resource = Resource<LauncherResponse>(Status.LOADING, resp, "Loading Data..", DataSource.LOCAL)
        var live :MediatorLiveData<Resource<LauncherResponse>> =  MediatorLiveData<Resource<LauncherResponse>>()
        live.value = resource
        //Thread.sleep(4*1000)
        var resourceS = Resource<LauncherResponse>(Status.SUCCESS, resp, "Data Loading Success", DataSource.LOCAL)

        live.value = resourceS
        return live
    }

    private fun addFoodCategories(){
        var jsonString : String = JSONUtils.readJsonFromFile(context,"food_categories.json")
        var listType = object : TypeToken<List<FoodCategory>>(){}.type
        var gson:Gson = Gson()
        var cats:List<FoodCategory> = gson.fromJson(jsonString,listType)
        Log.d("Repo","categories count:"+cats.size)
        Log.d("Repo","categories[0] name:"+cats.get(0).name)

        var batch:WriteBatch = db.batch()
        for(cat in  cats){
            var ref:DocumentReference = db.collection("foodCategories").document(cat.name)
            batch.set(ref,cat)
        }
        batch.commit().addOnSuccessListener { task->Log.d("Repo","Categories insertion success") }
                .addOnFailureListener(OnFailureListener {task->Log.e("Repo","Categories insertion failed")  })

    }

    private fun addRecipeCategories(){
        var jsonString : String = JSONUtils.readJsonFromFile(context,"recipe_categories.json")
        var listType = object : TypeToken<List<RecipeCategory>>(){}.type
        var gson:Gson = Gson()
        var cats:List<RecipeCategory> = gson.fromJson(jsonString,listType)
        Log.d("Repo","categories count:"+cats.size)
        Log.d("Repo","categories[0] name:"+cats.get(0).name)

        var batch:WriteBatch = db.batch()
        for(cat in  cats){
            var ref:DocumentReference = db.collection("recipeCategories").document(cat.name)
            batch.set(ref,cat)
        }
        batch.commit().addOnSuccessListener { task->Log.d("Repo","Categories insertion success") }
                .addOnFailureListener(OnFailureListener {task->Log.e("Repo","Categories insertion failed")  })

    }
    private fun addServingTypes(){
        var jsonString : String = JSONUtils.readJsonFromFile(context,"serving_types.json")
        var listType = object : TypeToken<List<ServingType>>(){}.type
        var gson:Gson = Gson()
        var cats:List<ServingType> = gson.fromJson(jsonString,listType)
        Log.d("Repo","categories count:"+cats.size)
        Log.d("Repo","categories[0] name:"+cats.get(0).name)

        var batch:WriteBatch = db.batch()
        for(cat in  cats){
            var ref:DocumentReference = db.collection("servingTypes").document(cat.name)
            batch.set(ref,cat)
        }
        batch.commit().addOnSuccessListener { task->Log.d("Repo","Serving Types insertion success") }
                .addOnFailureListener(OnFailureListener {task->Log.e("Repo","Serving Types insertion failed")  })

    }
    private fun addFood() {
        var food:Food = Food()
        food.basicInfo = BasicInfo()
        food.basicInfo.id = 1
        food.basicInfo.nameEnglish = "Orange"
        food.basicInfo.nameHindi = "Santra"
        food.basicProperty = BasicProperty()
        food.cost = Cost()
        food.nutrition = Nutrition()

        db.collection("foods")
                .document(food.basicInfo.nameEnglish)
                .set(food)
                .addOnSuccessListener { documentReference -> Log.d("appRepo", "DocumentSnapshot added with ID: " ) }
                .addOnFailureListener { e -> Log.w("appRepo", "Error adding document", e) }
        /*db.collection("foods")
                .add(food)
                .addOnSuccessListener { documentReference -> Log.d("appRepo", "DocumentSnapshot added with ID: " + documentReference.id) }
                .addOnFailureListener { e -> Log.w("appRepo", "Error adding document", e) }*/
    }

    private fun getUsers(){
        db.collection("users")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d("Repo", document.id + " => " + document.data)
                        }
                    } else {
                        Log.w("Repo", "Error getting documents.", task.exception)
                    }
                }
    }

    private fun addNewUser(){
        // Create a new user with a first and last name
        val user = User("Tony","Stark","ironman@marvel.com")

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference -> Timber.d("appRepo", "DocumentSnapshot added with ID: " + documentReference.id) }
                .addOnFailureListener { e -> Timber.w("appRepo", "Error adding document", e) }

        user.userMiddleName = "Iron Man"
        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference -> Log.d("Repo", "DocumentSnapshot added with ID: " + documentReference.id) }
                .addOnFailureListener { e -> Log.w("Repo", "Error adding document", e) }

    }

    init {
        Timber.d("Injecting:" + this)
    }


}
