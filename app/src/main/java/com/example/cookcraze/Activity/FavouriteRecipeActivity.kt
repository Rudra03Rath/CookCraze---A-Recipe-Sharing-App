package com.example.cookcraze.Activity

import Recipe
import RecipeAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookcraze.R
import com.google.firebase.firestore.FirebaseFirestore

class FavouriteRecipeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val db = FirebaseFirestore.getInstance()
    private val favouriteRecipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_recipe)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecipeAdapter(this, favouriteRecipes)
        recyclerView.adapter = adapter

        fetchFavouriteRecipes()
    }

    private fun fetchFavouriteRecipes() {
        db.collection("favourites")
            .get()
            .addOnSuccessListener { result ->
                favouriteRecipes.clear()
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    favouriteRecipes.add(recipe)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle any errors
            }
    }
}
