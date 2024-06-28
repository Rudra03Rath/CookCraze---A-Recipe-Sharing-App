package com.example.cookcraze.Activity

import Recipe
import RecipeAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cookcraze.R
import com.example.cookcraze.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var recipeAdapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)

        drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar,
            R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        binding.recyclerViewRecipes.layoutManager = GridLayoutManager(this, 1)
        recipeAdapter = RecipeAdapter(this, recipes)
        binding.recyclerViewRecipes.adapter = recipeAdapter

        fetchAllRecipes()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_recipes -> {
                val intent = Intent(this, MyRecipeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_liked_recipes -> {
                // Handle Liked Recipes action
            }
            R.id.nav_downloaded_recipes -> {
                // Handle Downloaded Recipes action
            }
            R.id.nav_logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes") { _, _ ->
                        auth.signOut()
                        navigateToAuthActivity()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun fetchAllRecipes() {
        val database = FirebaseDatabase.getInstance().reference
        database.child("recipes").get()
            .addOnSuccessListener { snapshot ->
                recipes.clear()
                for (userSnapshot in snapshot.children) {
                    for (recipeSnapshot in userSnapshot.children) {
                        val recipe = recipeSnapshot.getValue(Recipe::class.java)
                        if (recipe != null) {
                            Log.d("MainActivity", "Fetched recipe: ${recipe.title}, URL: ${recipe.thumbnailUrl}")
                            recipes.add(recipe)
                        } else {
                            Log.e("MainActivity", "Error: Recipe is null")
                        }
                    }
                }
                recipeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error fetching recipes", e)
            }
    }

}
