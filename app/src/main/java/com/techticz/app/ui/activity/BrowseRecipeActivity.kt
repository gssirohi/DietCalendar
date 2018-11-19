package com.techticz.app.ui.activity

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.widget.SearchView
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.BrowseRecipeResponse
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.ui.adapter.RecipesAdapter
import com.techticz.app.viewmodel.BrowseRecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.activity_browse_recipe.*
import kotlinx.android.synthetic.main.content_browse_recipe.*


class BrowseRecipeActivity : BaseDIActivity(), RecipesAdapter.RecipeViewCallBacks {
    override fun onRecipeViewClicked(recipe: Recipe) {
        var data = intent
        data.putExtra("recipeId",recipe.id)
        setResult(Activity.RESULT_OK,data)
        finish()
    }

    private lateinit var browseRecipesViewModel: BrowseRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_recipe)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        fab.setOnClickListener { view ->
            onCreateRecipeClicked()
        }
        browseRecipesViewModel = ViewModelProviders.of(this, viewModelFactory!!).get(BrowseRecipeViewModel::class.java)
        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                //Task HERE
                browseRecipesViewModel?.triggerRecipeText.value  = query
                return false
            }

        })

        recycler_recipes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_recipes.adapter = RecipesAdapter(ArrayList(),this)

        browseRecipesViewModel.liveBrowseRecipesResponse?.observe(this, Observer {
            res->
            onRecipesDataLoaded(res)
        })

    }

    private fun onRecipesDataLoaded(res: Resource<BrowseRecipeResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                (recycler_recipes.adapter as RecipesAdapter).dayMeals = res?.data?.recipes!!
                (recycler_recipes.adapter as RecipesAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onCreateRecipeClicked() {
       // navigator.startCreateRecipeScreen()
    }

}
