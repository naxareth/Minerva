package com.example.minerva_10.adapter  // Ensure this matches the file location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minerva_10.R
import com.example.minerva_10.api.responses.SearchResult

class AnimeAdapter2(private val animeList: MutableList<SearchResult>) :
    RecyclerView.Adapter<AnimeAdapter2.AnimeViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(anime: SearchResult)
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animeTitle: TextView = view.findViewById(R.id.animeTitle)
        val animeThumbnail: ImageView = view.findViewById(R.id.animeThumbnail)
        val releaseDate: TextView = view.findViewById(R.id.releaseDate)
        val subOrDub: TextView = view.findViewById(R.id.subOrDub)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.animeTitle.text = anime.title

        // Use strings.xml for release date and type text
        holder.releaseDate.text = holder.itemView.context.getString(R.string.release_date, anime.releaseDate)
        holder.subOrDub.text = holder.itemView.context.getString(R.string.type, anime.subOrDub)

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load(anime.image)  // Assuming anime.image contains the image URL
            .placeholder(R.drawable.placeholder)  // Ensure you have a placeholder image in res/drawable
            .into(holder.animeThumbnail)

        holder.itemView.setOnClickListener {
            listener.onItemClick(anime)
        }
    }

    override fun getItemCount(): Int {
        return animeList.size
    }
}
