package com.example.cookcraze.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cookcraze.databinding.ActivityCreateRecipeStep1Binding
import com.google.firebase.storage.FirebaseStorage

class CreateRecipeStep1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRecipeStep1Binding
    private lateinit var storage: FirebaseStorage
    private var thumbnailUrl: String? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()

        binding.addThumbnailButton.setOnClickListener {
            chooseImage()
        }

        binding.addIngredientsButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            if (title.isNotEmpty() && thumbnailUrl != null) {
                val intent = Intent(this, CreateRecipeStep2Activity::class.java).apply {
                    putExtra("title", title)
                    putExtra("thumbnailUrl", thumbnailUrl)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a title and select a thumbnail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val fileUri = data.data!!
            uploadFile(fileUri)
        }
    }

    private fun uploadFile(fileUri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        binding.addIngredientsButton.isEnabled = false

        val storageRef = storage.reference.child("thumbnails/${System.currentTimeMillis()}")
        val uploadTask = storageRef.putFile(fileUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                thumbnailUrl = task.result.toString()
                Glide.with(this).load(thumbnailUrl).into(binding.thumbnailImageView)
                binding.thumbnailImageView.visibility = View.VISIBLE
                binding.addIngredientsButton.isEnabled = true
            } else {
                Toast.makeText(this, "Failed to upload thumbnail", Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.GONE
        }
    }
}
