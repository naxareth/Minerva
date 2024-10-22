package com.example.minerva_10.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.api.responses.DownloadItem

class DownloadAdapter : ListAdapter<DownloadItem, DownloadAdapter.DownloadViewHolder>(DownloadDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download, parent, false)
        return DownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val downloadItem = getItem(position)
        holder.bind(downloadItem)
    }

    class DownloadViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val animeTitleTextView: TextView = itemView.findViewById(R.id.item_anime_title)
        private val episodeNumberTextView: TextView = itemView.findViewById(R.id.item_episode_number)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.item_download_progress)
        private val progressTextView: TextView = itemView.findViewById(R.id.item_progress_text)

        fun bind(downloadItem: DownloadItem) {
            animeTitleTextView.text = downloadItem.animeTitle
            episodeNumberTextView.text = "Episode: ${downloadItem.episodeNumber}"
            progressBar.progress = downloadItem.progress
            progressTextView.text = "Download Progress: ${downloadItem.progress}%"
        }
    }

    class DownloadDiffCallback : DiffUtil.ItemCallback<DownloadItem>() {
        override fun areItemsTheSame(oldItem: DownloadItem, newItem: DownloadItem): Boolean {
            return oldItem.animeId == newItem.animeId
        }

        override fun areContentsTheSame(oldItem: DownloadItem, newItem: DownloadItem): Boolean {
            return oldItem == newItem
        }
    }
}