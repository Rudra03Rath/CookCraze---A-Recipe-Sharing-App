package com.example.cookcraze.Activity

import Recipe
import RecipeAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookcraze.databinding.ActivityMyRecipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MyRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRecipeBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addRecipeButton.setOnClickListener {
            startActivity(Intent(this, CreateRecipeStep1Activity::class.java))
        }

        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(this)
        recipeAdapter = RecipeAdapter(this, recipes)
        binding.recipesRecyclerView.adapter = recipeAdapter

        fetchRecipes()
    }

    private fun fetchRecipes() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email?.replace(".", ",") // Firebase keys can't contain periods

        if (email == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().reference
        database.child("recipes").child(email).get()
            .addOnSuccessListener { snapshot ->
                recipes.clear()
                for (recipeSnapshot in snapshot.children) {
                    val recipe = recipeSnapshot.getValue(Recipe::class.java)
                    if (recipe != null) {
                        Log.d("MyRecipeActivity", "Fetched recipe: ${recipe.title}, URL: ${recipe.thumbnailUrl}")
                        recipes.add(recipe)
                    } else {
                        Log.e("MyRecipeActivity", "Error: Recipe is null")
                    }
                }
                recipeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("MyRecipeActivity", "Error fetching recipes", e)
                Toast.makeText(this, "Error fetching recipes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
