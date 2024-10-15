package com.example.minerva_10.adapter

import GridSpacingItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.FavoriteAdapter
import com.example.minerva_10.R
import com.example.minerva_10.api.responses.FavoriteResource

class FavoriteParentAdapter(
    var favoriteResources: List<FavoriteResource>,
    private val activity: FragmentActivity,
    private val onItemClick: (FavoriteResource) -> Unit
) : RecyclerView.Adapter<FavoriteParentAdapter.FavoriteWithItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteWithItemsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorites_grid_view, parent, false)
        return FavoriteWithItemsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteWithItemsViewHolder, position: Int) {
        holder.bind(favoriteResources, activity, onItemClick)
    }

    override fun getItemCount(): Int = 1

    inner class FavoriteWithItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.Favorites)
        private val gridView: RecyclerView = view.findViewById(R.id.gridView)

        fun bind(favoriteResources: List<FavoriteResource>, activity: FragmentActivity, onItemClick: (FavoriteResource) -> Unit) {
            // Set the category title
            title.text = "FAVORITES"

            // Set up the GridLayoutManager with 3 columns for the favorites
            gridView.layoutManager = GridLayoutManager(gridView.context, 3)
            gridView.adapter = FavoriteAdapter().apply {
                submitList(favoriteResources, activity, onItemClick)
            }
            gridView.addItemDecoration(GridSpacingItemDecoration(3, 16)) // Add item decoration
        }
    }
}