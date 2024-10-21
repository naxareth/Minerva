package com.example.minerva_10.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.bumptech.glide.Glide
import com.example.minerva_10.api.responses.DownloadItem

class DownloadAdapter(private val items: List<DownloadItem>) :
    RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {

    class DownloadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animeCover: ImageView = view.findViewById(R.id.item_anime_cover)
        val animeTitle: TextView = view.findViewById(R.id.item_anime_title)
        val episodeNumber: TextView = view.findViewById(R.id.item_episode_number)
        val progressBar: ProgressBar = view.findViewById(R.id.item_download_progress)
        val progressText: TextView = view.findViewById(R.id.item_progress_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_download, parent, false)
        return DownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val item = items[position]
        holder.animeTitle.text = item.animeTitle
        holder.episodeNumber.text = "Episode: ${item.episodeNumber}"
        holder.progressBar.progress = item.progress
        holder.progressText.text = "Download Progress: ${item.progress}%"

        // Load the anime cover image using Glide
        Glide.with(holder.animeCover.context)
            .load(item.coverImageUrl)
            .into(holder.animeCover)
    }

    override fun getItemCount(): Int = items.size
}