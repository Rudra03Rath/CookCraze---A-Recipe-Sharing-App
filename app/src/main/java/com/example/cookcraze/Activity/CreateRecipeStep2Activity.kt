package com.example.cookcraze.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.cookcraze.databinding.ActivityCreateRecipeStep2Binding

class CreateRecipeStep2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRecipeStep2Binding
    private val ingredientViews = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val thumbnailUrl = intent.getStringExtra("thumbnailUrl")

        // Log the received values
        Log.d("CreateRecipeStep2", "Title: $title")
        Log.d("CreateRecipeStep2", "Thumbnail URL: $thumbnailUrl")

        // Restore ingredients if available
        if (savedInstanceState != null) {
            savedInstanceState.getStringArrayList("ingredients")?.let {
                for (ingredient in it) {
                    addIngredientField(ingredient)
                }
            }
        }

        binding.addIngredientButton.setOnClickListener {
            addIngredientField()
        }

        binding.nextButton.setOnClickListener {
            updateIngredientsList()
            val intent = Intent(this, CreateRecipeStep3Activity::class.java).apply {
                putExtra("title", title)
                putExtra("thumbnailUrl", thumbnailUrl)
                putStringArrayListExtra("ingredients", ArrayList(ingredientViews.map { it.text.toString() }))
            }
            startActivity(intent)
        }

        binding.previousButton.setOnClickListener {
            finish()
        }
    }

    private fun addIngredientField(ingredient: String = "") {
        val newIngredientView = EditText(this)
        newIngredientView.layoutParams = binding.ingredientEditText.layoutParams
        newIngredientView.setText(ingredient)
        binding.ingredientsContainer.addView(newIngredientView)
        ingredientViews.add(newIngredientView)
    }

    private fun updateIngredientsList() {
        ingredientViews.clear()
        for (i in 0 until binding.ingredientsContainer.childCount) {
            val childView = binding.ingredientsContainer.getChildAt(i)
            if (childView is EditText) {
                ingredientViews.add(childView)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val ingredientTexts = ArrayList(ingredientViews.map { it.text.toString() })
        outState.putStringArrayList("ingredients", ingredientTexts)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val ingredientTexts = savedInstanceState.getStringArrayList("ingredients")
        if (ingredientTexts != null) {
            ingredientViews.clear()
            binding.ingredientsContainer.removeAllViews()
            for (ingredient in ingredientTexts) {
                addIngredientField(ingredient)
            }
        }
    }
}
