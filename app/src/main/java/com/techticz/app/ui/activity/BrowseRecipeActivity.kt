package com.techticz.app.ui.activity

import android.os.Bundle
import android.app.Activity
import android.view.View
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.BrowseRecipeResponse
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.ui.adapter.BrowseRecipesAdapter
import com.techticz.app.viewmodel.BrowseRecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.activity_browse_recipe.*
import kotlinx.android.synthetic.main.content_browse_recipe.*


class BrowseRecipeActivity : BaseDIActivity(), BrowseRecipesAdapter.RecipeViewCallBacks {
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
        activityCollapsingToolbar = toolbar_layout
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
                spin_kit_browse_recipes.visibility = View.VISIBLE
                return false
            }

        })

        recycler_recipes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_recipes.adapter = BrowseRecipesAdapter(ArrayList(),this)

        browseRecipesViewModel.liveBrowseRecipesResponse?.observe(this, Observer {
            res->
            onRecipesDataLoaded(res)
        })

    }

    private fun onRecipesDataLoaded(res: Resource<BrowseRecipeResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                spin_kit_browse_recipes.visibility = View.INVISIBLE
                (recycler_recipes.adapter as BrowseRecipesAdapter).dayMeals = res?.data?.recipes!!
                (recycler_recipes.adapter as BrowseRecipesAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onCreateRecipeClicked() {
       navigator.startCreateRecipeScreen()
    }

}
