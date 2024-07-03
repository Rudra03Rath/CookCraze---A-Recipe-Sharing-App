package com.example.cookcraze.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cookcraze.databinding.ActivityCreateRecipeStep3Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreateRecipeStep3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRecipeStep3Binding
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private var videoUrl: String? = null
    private val PICK_VIDEO_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val title = intent.getStringExtra("title")
        val thumbnailUrl = intent.getStringExtra("thumbnailUrl")
        val ingredients = intent.getStringArrayListExtra("ingredients")

        Log.d("CreateRecipeStep3", "Title: $title")
        Log.d("CreateRecipeStep3", "Thumbnail URL: $thumbnailUrl")
        Log.d("CreateRecipeStep3", "Ingredients: $ingredients")

        binding.addVideoButton.setOnClickListener {
            chooseVideo()
        }

        binding.previousButton.setOnClickListener {
            finish()
        }

        binding.finishButton.setOnClickListener {
            val instructions = binding.instructionEditText.text.toString()

            Log.d("CreateRecipeStep3", "Title: $title")
            Log.d("CreateRecipeStep3", "Thumbnail URL: $thumbnailUrl")
            Log.d("CreateRecipeStep3", "Ingredients: $ingredients")
            Log.d("CreateRecipeStep3", "Instructions: $instructions")
            Log.d("CreateRecipeStep3", "Video URL: $videoUrl")

            if (title != null && thumbnailUrl != null && ingredients != null && instructions.isNotEmpty()) {
                uploadRecipe(title, thumbnailUrl, ingredients, instructions, videoUrl)
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chooseVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val fileUri = data.data!!
            uploadFile(fileUri)
        }
    }

    private fun uploadFile(fileUri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        binding.finishButton.isEnabled = false

        val storageRef = storage.reference.child("videos/${System.currentTimeMillis()}")
        val uploadTask = storageRef.putFile(fileUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                videoUrl = task.result.toString()
                binding.videoView.setVideoURI(Uri.parse(videoUrl))
                binding.videoView.visibility = View.VISIBLE
                val mediaController = MediaController(this)
                mediaController.setAnchorView(binding.videoView)
                binding.videoView.setMediaController(mediaController)
                binding.videoView.start()
                binding.finishButton.isEnabled = true
            } else {
                Toast.makeText(this, "Failed to upload video", Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun uploadRecipe(title: String, thumbnailUrl: String, ingredients: ArrayList<String>, instructions: String, videoUrl: String?) {
        binding.progressBar.visibility = View.VISIBLE
        binding.finishButton.isEnabled = false

        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email?.replace(".", ",") // Firebase keys can't contain periods

        if (email == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val recipe = hashMapOf(
            "title" to title,
            "thumbnailUrl" to thumbnailUrl,
            "ingredients" to ingredients,
            "instructions" to instructions,
            "videoUrl" to videoUrl
        )

        val database = FirebaseDatabase.getInstance().reference
        database.child("recipes").child(email).push().setValue(recipe)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Recipe added successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MyRecipeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.finishButton.isEnabled = true
                Toast.makeText(this, "Failed to add recipe: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
