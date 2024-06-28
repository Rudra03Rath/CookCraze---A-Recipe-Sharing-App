package com.example.cookcraze.Activity

import Recipe
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.cookcraze.R

class IngredientsActivity : AppCompatActivity() {

    private lateinit var ingredientsContainer: LinearLayout
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        ingredientsContainer = findViewById(R.id.ingredientsContainer)
        btnStart = findViewById(R.id.btnStart)

        val recipe = intent.getParcelableExtra<Recipe>("recipe")
        recipe?.ingredients?.forEach { ingredient ->
            val checkBox = CheckBox(this)
            checkBox.text = ingredient
            ingredientsContainer.addView(checkBox)
        }

        btnStart.setOnClickListener {
            val intent = Intent(this, InstructionsActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }

        ingredientsContainer.children.forEach { view ->
            (view as CheckBox).setOnCheckedChangeListener { _, _ ->
                checkAllChecked()
            }
        }
    }

    private fun checkAllChecked() {
        btnStart.isEnabled = ingredientsContainer.children
            .map { it as CheckBox }
            .all { it.isChecked }
    }
}
