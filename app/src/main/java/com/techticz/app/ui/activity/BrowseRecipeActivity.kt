package com.techticz.app.ui.activity

import android.os.Bundle
import android.app.Activity
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.techticz.app.base.BaseDIActivity
import com.techticz.app.model.BrowseRecipeResponse
import com.techticz.app.model.mealplate.RecipeItem
import com.techticz.app.model.recipe.Recipe
import com.techticz.app.ui.adapter.AutoBrowseRecipeAdapter
import com.techticz.app.ui.adapter.BrowseRecipesAdapter
import com.techticz.app.viewmodel.BrowseRecipeViewModel
import com.techticz.dietcalendar.R
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import kotlinx.android.synthetic.main.activity_browse_recipe.*
import kotlinx.android.synthetic.main.content_browse_recipe.*


class BrowseRecipeActivity : BaseDIActivity(), BrowseRecipesAdapter.RecipeViewCallBacks, AutoBrowseRecipeAdapter.RecipeViewCallBacks {
    override fun onFeaturedRecipeViewClicked(mealRecipe: Recipe) {
        if(TextUtils.isEmpty(plateId)){
            navigator.startExploreRecipeScreen(RecipeItem(mealRecipe?.id,1))
        } else {
            var data = intent
            data.putExtra("recipeId", mealRecipe.id)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    override fun onRecipeViewClicked(recipe: Recipe) {
        if(TextUtils.isEmpty(plateId)){

        } else {
            var data = intent
            data.putExtra("recipeId", recipe.id)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    private lateinit var browseRecipesViewModel: BrowseRecipeViewModel

    private var plateId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_recipe)
        activityToolbar = toolbar
        activityCoordinatorLayout = coordinatorLayout
        activityCollapsingToolbar = toolbar_layout
        plateId = intent.getStringExtra("plateId")
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

        recycler_featured_recipes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_featured_recipes.adapter = AutoBrowseRecipeAdapter(ArrayList(),this)

        recycler_my_recipes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_my_recipes.adapter = AutoBrowseRecipeAdapter(ArrayList(),this)

        browseRecipesViewModel.liveBrowseRecipesResponse?.observe(this, Observer {
            res->
            onRecipesDataLoaded(res)
        })

        browseRecipesViewModel.liveFeaturedRecipesResponse?.observe(this, Observer {
            res->
            onFeaturedRecipesDataLoaded(res)
        })
        browseRecipesViewModel.liveMyRecipesResponse?.observe(this, Observer {
            res->
            onMyRecipesDataLoaded(res)
        })

        browseRecipesViewModel.triggerFeaturedRecipes.value = true
        browseRecipesViewModel.triggerMyRecipes.value = true
    }

    private fun onFeaturedRecipesDataLoaded(res: Resource<BrowseRecipeResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                spin_kit_featured_recipes.visibility = View.INVISIBLE
                (recycler_featured_recipes.adapter as AutoBrowseRecipeAdapter).recipes = res?.data?.recipes!!
                (recycler_featured_recipes.adapter as AutoBrowseRecipeAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun onMyRecipesDataLoaded(res: Resource<BrowseRecipeResponse>?) {
        when(res?.status){
            Status.SUCCESS->{
                spin_kit_my_recipes.visibility = View.INVISIBLE
                (recycler_my_recipes.adapter as AutoBrowseRecipeAdapter).recipes = res?.data?.recipes!!
                (recycler_my_recipes.adapter as AutoBrowseRecipeAdapter).notifyDataSetChanged()
            }
        }
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
