package com.example.minerva_10

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.api.responses.FavoriteResource

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private var favoriteResources: List<FavoriteResource> = emptyList()

    fun submitList(favoriteResources: List<FavoriteResource>) {
        this.favoriteResources = favoriteResources
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favoriteResource = favoriteResources[position]
        holder.bind(favoriteResource)
    }

    override fun getItemCount(): Int {
        return favoriteResources.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvImage: TextView = itemView.findViewById(R.id.tvImage)
        private val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)

        fun bind(favoriteResource: FavoriteResource) {
            tvId.text = favoriteResource.`anime-id`.toString()
            tvTitle.text = favoriteResource.title
            tvImage.text = favoriteResource.image
            tvUserId.text = favoriteResource.user_id.toString()
        }
    }
}