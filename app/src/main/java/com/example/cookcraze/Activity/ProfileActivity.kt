package com.example.cookcraze.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.cookcraze.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var changeProfilePictureButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var toggleDarkModeButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private var isDarkModeEnabled: Boolean = false

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImageView = findViewById(R.id.profileImageView)
        changeProfilePictureButton = findViewById(R.id.changeProfilePictureButton)
        usernameEditText = findViewById(R.id.usernameEditText)
        emailTextView = findViewById(R.id.emailTextView)
        toggleDarkModeButton = findViewById(R.id.toggleDarkModeButton)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        loadUserProfile()

        changeProfilePictureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        toggleDarkModeButton.setOnClickListener {
            isDarkModeEnabled = !isDarkModeEnabled
            toggleDarkMode()
        }
    }

    private fun loadUserProfile() {
        emailTextView.text = user.email
        usernameEditText.setText(user.displayName)

        // Load profile picture
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/${user.uid}")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(profileImageView)
        }.addOnFailureListener {
            profileImageView.setImageResource(R.drawable.ic_app_logo)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            profileImageView.setImageBitmap(imageBitmap)
            uploadProfilePicture(imageBitmap)
        }
    }

    private fun uploadProfilePicture(bitmap: Bitmap) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/${user.uid}")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleDarkMode() {
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toggleDarkModeButton.text = "Turn Off Dark Mode"
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toggleDarkModeButton.text = "Turn On Dark Mode"
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val newUsername = usernameEditText.text.toString()
        val database = FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
        database.child("username").setValue(newUsername)

        val profileUpdates = userProfileChangeRequest {
            displayName = newUsername
        }
        user.updateProfile(profileUpdates)
    }
}
