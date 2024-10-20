package com.example.minerva_10.adapter

import GridSpacingItemDecoration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.Category
import com.example.minerva_10.api.responses.Item
import kotlinx.coroutines.launch

class AnimeParentAdapter(
    private var categories: List<Category>,
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
        private val previousPageButton: Button = view.findViewById(R.id.previousPageButton)
        private val nextPageButton: Button = view.findViewById(R.id.nextPageButton)
        private val currentPageTextView: TextView = view.findViewById(R.id.currentPageTextView)

        // Separate variables for pagination state
        private var currentPage = 1
        private var hasNextPage = true
        private var isLoading = false
        private val fetchedPages = mutableMapOf<String, List<Item>>() // Cache for fetched pages

        fun bind(category: Category, onItemClick: (Item) -> Unit) {
            title.text = category.title

            // Set up the GridLayoutManager with 3 columns for the items in this category
            subRecyclerView.layoutManager = GridLayoutManager(subRecyclerView.context, 3) // 3 columns
            subRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, 10)) // 3 columns, 10dp spacing

            // Load initial data
            loadPage(category.title, currentPage, onItemClick)

            // Set up button click listeners
            previousPageButton.setOnClickListener {
                Log.d("AnimeParentAdapter", "Previous page button clicked for ${category.title}")
                if (currentPage > 1) {
                    currentPage-- // Decrement page
                    loadPage(category.title, currentPage, onItemClick) // Load items for the previous page
                }
            }

            nextPageButton.setOnClickListener {
                Log.d("AnimeParentAdapter", "Next page button clicked for ${category.title}")
                if (hasNextPage) {
                    currentPage++ // Increment page
                    loadPage(category.title, currentPage, onItemClick) // Load items for the next page
                }
            }
        }

        private fun loadPage(categoryTitle: String, page: Int, onItemClick: (Item) -> Unit) {
            if (isLoading) return // Prevent multiple simultaneous loads
            isLoading = true

            activity.lifecycleScope.launch {
                try {
                    Log.d("AnimeParentAdapter", "Loading items for category: $categoryTitle, page: $page") // Debug log

                    // Check if the page is already fetched
                    if (fetchedPages.containsKey("$categoryTitle$page")) {
                        // Use cached data
                        val items = fetchedPages["$categoryTitle$page"]!!
                        updateRecyclerView(items, onItemClick)
                    } else {
                        // Fetch new data from API
                        val itemsResponse = when (categoryTitle.uppercase()) {
                            "TOP AIRING" -> RetrofitClient.animeApiService.getTopAiringAnimes(page)
                            "RECENT EPISODES" -> RetrofitClient.animeApiService.getRecentEpisodes(page)
                            else -> throw IllegalArgumentException("Unknown category: $categoryTitle")
                        }

                        // Cache the fetched items
                        fetchedPages["$categoryTitle$page"] = itemsResponse.results
                        updateRecyclerView(itemsResponse.results, onItemClick)

                        // Update pagination state
                        hasNextPage = itemsResponse.hasNextPage
                    }

                    currentPageTextView.text = "Page $currentPage"
                } catch (e: Exception) {
                    Log.e("AnimeParentAdapter", "Error loading items", e)
                } finally {
                    isLoading = false
                }
            }
        }

        private fun updateRecyclerView(items: List<Item>, onItemClick: (Item) -> Unit) {
            val subAdapter = AnimeAdapter(items, activity, onItemClick)
            subRecyclerView.adapter = subAdapter
        }
    }

    // Function to update the adapter with new categories
    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}