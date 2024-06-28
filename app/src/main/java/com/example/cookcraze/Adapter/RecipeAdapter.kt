import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cookcraze.Activity.IngredientsActivity
import com.example.cookcraze.R
import com.example.cookcraze.databinding.ItemRecipeBinding

class RecipeAdapter(private val context: Context, private val recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.titleTextView.text = recipe.title
            Log.d("RecipeAdapter", "Loading image URL: ${recipe.thumbnailUrl}") // Add this line for logging
            Glide.with(context)
                .load(recipe.thumbnailUrl)
                .placeholder(R.drawable.ic_app_logo) // Optional: add a placeholder image
                .into(binding.recipeImageView)

            binding.titleTextView.setOnClickListener {
                navigateToIngredientsActivity(recipe)
            }
            binding.recipeImageView.setOnClickListener {
                navigateToIngredientsActivity(recipe)
            }
        }

        private fun navigateToIngredientsActivity(recipe: Recipe) {
            val intent = Intent(context, IngredientsActivity::class.java)
            intent.putExtra("recipe", recipe)
            context.startActivity(intent)
        }
    }
}

