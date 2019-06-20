package com.techticz.app.repo

import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techticz.app.constants.AppCollections
import com.techticz.app.model.dietplan.DietPlan
import com.techticz.app.model.food.*
import com.techticz.app.model.mealplate.MealPlate
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.base.BaseDIRepository
import com.techticz.app.model.launch.Launching
import com.techticz.powerkit.utils.JSONUtils
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */

class DeveloperRepository @Inject constructor(private val db: FirebaseFirestore) : BaseDIRepository() {
    inline fun <reified T> genericType() = object: TypeToken<T>() {}.type



    private fun uploadCollection(collection: AppCollections) {
        try {
            var jsonString: String = JSONUtils.readJsonFromFile(hostActivityContext!!, collection.jsonName)
            var sig = collection.className


            var gson: Gson = Gson()
            var batch: WriteBatch = db.batch()
            when (sig.canonicalName) {
                Food::class.java.canonicalName -> {
                    var listType = object : TypeToken<List<Food>>() {}.type
                    var items: List<Food> = gson.fromJson(jsonString, listType)
                    for (item in items) {
                        try {
                            var ref: DocumentReference = db.collection(collection.collectionName).document(item.id)
                            batch.set(ref, item)
                        } catch (e:Exception){
                            e.printStackTrace()
                            Timber.e("Error uploading Food:"+item.id+" Name:"+item.basicInfo.name.english)
                        }
                    }
                }

                Recipe::class.java.canonicalName -> {
                    var listType = object : TypeToken<List<Recipe>>() {}.type
                    var items: List<Recipe> = gson.fromJson(jsonString, listType)
                    for (item in items) {
                        var ref: DocumentReference = db.collection(collection.collectionName).document(item.id)
                        batch.set(ref, item)
                    }
                }

                MealPlate::class.java.canonicalName -> {
                    var listType = object : TypeToken<List<MealPlate>>() {}.type
                    var items: List<MealPlate> = gson.fromJson(jsonString, listType)
                    for (item in items) {
                        var ref: DocumentReference = db.collection(collection.collectionName).document(item.id)
                        batch.set(ref, item)
                    }
                }

                DietPlan::class.java.canonicalName -> {
                    var listType = object : TypeToken<List<DietPlan>>() {}.type
                    var items: List<DietPlan> = gson.fromJson(jsonString, listType)
                    for (item in items) {
                        var ref: DocumentReference = db.collection(collection.collectionName).document(item.id)
                        batch.set(ref, item)
                    }
                }

                Launching::class.java.canonicalName -> {
                    var listType = object : TypeToken<List<Launching>>() {}.type
                    var items: List<Launching> = gson.fromJson(jsonString, listType)
                    for (item in items) {
                        var ref: DocumentReference = db.collection(collection.collectionName).document(item.id)
                        batch.set(ref, item)
                    }
                }

            }

            batch.commit()
                    .addOnSuccessListener { task ->
                        var message = collection.collectionName + " upload success"
                        hideProgress()
                        showSuccess(message)
                    }
                    .addOnFailureListener(OnFailureListener { task ->
                        var message = collection.collectionName + " upload failed"
                        hideProgress()
                        showError(message)
                    })
        } catch (e:Exception){
            e.printStackTrace()
            showError(e.toString())
        }

    }


    private fun addFood() {
        var food: Food = Food()
        food.id = "Z0001"
        food.basicInfo = BasicInfo()
        food.basicProperty = BasicProperty()
        food.cost = Cost()
        food.additionalInfo = AdditionalInfo()
        food.nutrition = Nutrition()

        db.collection("foods")
                .document(food.id)
                .set(food)
                .addOnSuccessListener { documentReference ->
                    Timber.d("appRepo", "Food added with ID: ")
                    hideProgress()
                    (hostActivityContext as BaseDIActivity).showSuccess("Food added with ID: " + documentReference)
                }
                .addOnFailureListener { e -> Timber.w("appRepo", "Error adding food", e) }
    }

    private fun getUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            Timber.d("Repo", document.id + " => " + document.data)
                        }
                    } else {
                        Timber.w("Repo", "Error getting documents.", task.exception)
                    }
                }
    }


    init {
        Timber.d("Injecting:" + this)
    }

    fun uploadAll() {
        Timber.d("uploading all..");
        showProgress()
        execute(Runnable {
            uploadFoods()
            uploadRecipes()
            uploadPlates()
            uploadPlans()
        })
    }

    fun uploadFoods() {
        showProgress("Uploading","Hold on, Uploading "+AppCollections.FOODS.collectionName+"...")
        execute(Runnable {
            uploadCollection(AppCollections.FOODS)
        })
    }

    fun uploadRecipes() {
        showProgress("Uploading","Hold on, Uploading "+AppCollections.RECIPES.collectionName+"...")
        execute(Runnable {
            uploadCollection(AppCollections.RECIPES)
        })
    }

    fun uploadPlates() {
        showProgress("Uploading","Hold on, Uploading "+AppCollections.PLATES.collectionName+"...")
        execute(Runnable {
            uploadCollection(AppCollections.PLATES)
        })
    }

    fun uploadPlans() {
        showProgress("Uploading","Hold on, Uploading "+AppCollections.PLANS.collectionName+"...")
        execute(Runnable {
            uploadCollection(AppCollections.PLANS)
        })
    }

    fun uploadLaunching() {
        showProgress("Uploading","Hold on, Uploading "+AppCollections.LAUNCHINGS.collectionName+"...")
        execute(Runnable {
            uploadCollection(AppCollections.LAUNCHINGS)
        })
    }

    fun uploadFoodCategories() {
        (hostActivityContext as BaseDIActivity).showError("Not Implemented")
    }

    fun uploadRecipeCaegories() {
        (hostActivityContext as BaseDIActivity).showError("Not Implemented")
    }

    fun uploadServingTypes() {
        (hostActivityContext as BaseDIActivity).showError("Not Implemented")
    }

    fun uploadNutrientsUnit() {
        (hostActivityContext as BaseDIActivity).showError("Not Implemented")
    }

    fun uploadUnits() {
        (hostActivityContext as BaseDIActivity).showError("Not Implemented")
    }


}