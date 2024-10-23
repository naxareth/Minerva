package com.example.minerva_10

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minerva_10.api.responses.FavoriteResource
import com.example.minerva_10.fragments.AnimeInfoFragment

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private var favoriteResources: List<FavoriteResource> = emptyList()
    private lateinit var context: Context
    private lateinit var onItemClick: (FavoriteResource) -> Unit

    fun submitList(favoriteResources: List<FavoriteResource>, context: Context, onItemClick: (FavoriteResource) -> Unit) {
        this.favoriteResources = favoriteResources
        this.context = context
        this.onItemClick = onItemClick
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favoriteResource = favoriteResources[position]
        holder.bind(favoriteResource, onItemClick) // Pass onItemClick to the bind function
    }

    override fun getItemCount(): Int {
        return favoriteResources.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

        fun bind(favoriteResource: FavoriteResource, onItemClick: (FavoriteResource) -> Unit) { // Add onItemClick as a parameter
            tvTitle.text = favoriteResource.title
            Glide.with(itemView.context)
                .load(favoriteResource.image)
                .into(ivImage)

            itemView.setOnClickListener {
                onItemClick(favoriteResource)
            }
        }
    }
}