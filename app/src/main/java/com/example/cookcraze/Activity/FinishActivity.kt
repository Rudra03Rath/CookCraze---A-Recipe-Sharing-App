package com.example.cookcraze.Activity

import Recipe
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cookcraze.R

class FinishActivity : AppCompatActivity() {

    private lateinit var imageViewThumbnail: ImageView
    private lateinit var btnAddToFavourite: Button
    private lateinit var btnDownload: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        imageViewThumbnail = findViewById(R.id.imageViewThumbnail)
        btnAddToFavourite = findViewById(R.id.btnAddToFavourite)
        btnDownload = findViewById(R.id.btnDownload)
        btnBack = findViewById(R.id.btnBack)

        val recipe = intent.getParcelableExtra<Recipe>("recipe")
        recipe?.let {
            loadImage(it.thumbnailUrl)
        }

        btnAddToFavourite.setOnClickListener {
            // TODO: Implement add to favourite functionality
        }

        btnDownload.setOnClickListener {
            // TODO: Implement download functionality
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
}
