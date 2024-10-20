import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapter.AnimeAdapter
import com.example.minerva_10.api.responses.Category
import com.example.minerva_10.api.responses.Item

class AnimeParentAdapter(
    private val categories: List<Category>,
    private val activity: FragmentActivity,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<AnimeParentAdapter.CategoryWithItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryWithItemsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime_recycler_view, parent, false)
        return CategoryWithItemsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryWithItemsViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category, onItemClick)
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryWithItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.categoryTitle)
        private val subRecyclerView: RecyclerView = view.findViewById(R.id.subRecyclerView)

        fun bind(category: Category, onItemClick: (Item) -> Unit) {
            // Set the category title
            title.text = category.title

            // Set up the GridLayoutManager with 5 columns for the items in this category
            subRecyclerView.layoutManager = GridLayoutManager(subRecyclerView.context, 3) // 5 columns
            subRecyclerView.adapter = AnimeAdapter(category.items, activity, onItemClick)
            subRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, 16))  // 5 columns, 16dp spacing

        }
    }
}