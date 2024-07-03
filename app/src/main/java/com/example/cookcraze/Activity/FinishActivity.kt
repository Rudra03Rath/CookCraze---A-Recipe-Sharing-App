package com.example.cookcraze.Activity

import Recipe
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cookcraze.R
import com.google.firebase.firestore.FirebaseFirestore

class FinishActivity : AppCompatActivity() {

    private lateinit var imageViewThumbnail: ImageView
    private lateinit var btnAddToFavourite: Button
    private lateinit var btnDownload: Button
    private lateinit var btnBack: Button
    private lateinit var progressBar: ProgressBar
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        imageViewThumbnail = findViewById(R.id.imageViewThumbnail)
        btnAddToFavourite = findViewById(R.id.btnAddToFavourite)
        btnDownload = findViewById(R.id.btnDownload)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)

        val recipe = intent.getParcelableExtra<Recipe>("recipe")
        recipe?.let {
            loadImage(it.thumbnailUrl)
        }

        btnAddToFavourite.setOnClickListener {
            recipe?.let {
                checkAndAddToFavourites(it)
            }
        }

        btnDownload.setOnClickListener {
            // TODO
        }
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadImage(thumbnailUrl: String?) {
        if (thumbnailUrl != null) {
            Glide.with(this)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_app_logo)
                .into(imageViewThumbnail)
        }
    }

    private fun checkAndAddToFavourites(recipe: Recipe) {
        progressBar.visibility = android.view.View.VISIBLE
        db.collection("favourites")
            .whereEqualTo("title", recipe.title)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    addToFavourites(recipe)
                } else {
                    progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this, "Already added to Favourite", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Failed to check favourites: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addToFavourites(recipe: Recipe) {
        db.collection("favourites")
            .add(recipe)
            .addOnSuccessListener {
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Failed to add to Favourite: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
