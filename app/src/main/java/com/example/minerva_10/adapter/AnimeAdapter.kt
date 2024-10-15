package com.example.minerva_10.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.minerva_10.R
import com.example.minerva_10.api.responses.Item

class AnimeAdapter(
    private val items: List<Item>,
    private val activity: FragmentActivity,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onItemClick)
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.itemTitle)
        private val imageView: ImageView = view.findViewById(R.id.itemImage)

        fun bind(item: Item, onItemClick: (Item) -> Unit) {
            title.text = item.title
            imageView.load(item.image)

            // Set an onClickListener for the ImageView
            imageView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}