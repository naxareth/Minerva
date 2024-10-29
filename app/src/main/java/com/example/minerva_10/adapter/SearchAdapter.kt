package com.example.minerva_10.adapter // Ensure this matches the file location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minerva_10.R
import com.example.minerva_10.api.responses.SearchResult

class SearchAdapter(
    private val animeList: MutableList<SearchResult>,
    private val listener: OnItemClickListener // Add listener parameter
) : RecyclerView.Adapter<SearchAdapter.AnimeViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(anime: SearchResult)
    }

    class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animeTitle: TextView = view.findViewById(R.id.animeTitle)
        val animeThumbnail: ImageView = view.findViewById(R.id.animeThumbnail)
        val releaseDate: TextView = view.findViewById(R.id.releaseDate)
        val subOrDub: TextView = view.findViewById(R.id.subOrDub)

        fun bind(anime: SearchResult, listener: OnItemClickListener) {
            animeTitle.text = anime.title
            releaseDate.text = itemView.context.getString(R.string.release_date, anime.releaseDate ?: "N/A")
            subOrDub.text = itemView.context.getString(R.string.type, anime.subOrDub ?: "Unknown")


            Glide.with(itemView.context)
                .load(anime.image)
                .placeholder(R.drawable.placeholder)
                .into(animeThumbnail)

            itemView.setOnClickListener {
                listener.onItemClick(anime) // Notify listener of the click
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.bind(anime, listener) // Bind data and listener
    }

    override fun getItemCount(): Int {
        return animeList.size
    }

    fun updateAnimeList(newAnimeList: List<SearchResult>) {
        animeList.clear()
        animeList.addAll(newAnimeList)
        notifyDataSetChanged()
    }
}