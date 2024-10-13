package com.example.minerva_10.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.api.responses.EpisodeInfo

class EpisodeAdapter(private var episodes: List<EpisodeInfo>) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episode_info_layout, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.episodeNumberTextView.text = episode.number.toString()
        holder.episodeUrlTextView.text = episode.url
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    fun updateEpisodes(newEpisodes: List<EpisodeInfo>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }

    inner class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val episodeNumberTextView: TextView = itemView.findViewById(R.id.episode_number)
        val episodeUrlTextView: TextView = itemView.findViewById(R.id.episode_url)
    }
}