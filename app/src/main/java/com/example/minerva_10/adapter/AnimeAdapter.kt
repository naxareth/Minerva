package com.example.minerva_10.adapter

import android.util.Log
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
import com.facebook.shimmer.ShimmerFrameLayout

class AnimeAdapter(
    private var items: List<Item>,
    private val activity: FragmentActivity,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.ItemViewHolder>() {

    // To manage shimmer visibility
    private var isLoading: Boolean = true

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

    // Method to update the item list and hide shimmer
    fun updateItems(newItems: List<Item>) {
        this.items = newItems
        isLoading = false // Data is ready
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.itemTitle)
        private val imageView: ImageView = view.findViewById(R.id.itemImage)
        private val shimmerFrameLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmer_view_container)

        fun bind(item: Item, onItemClick: (Item) -> Unit) {
            if (isLoading) {
                shimmerFrameLayout.visibility = View.VISIBLE
                title.visibility = View.GONE
                imageView.visibility = View.GONE
            } else {
                shimmerFrameLayout.visibility = View.GONE
                title.visibility = View.VISIBLE
                imageView.visibility = View.VISIBLE

                Log.d("AnimeAdapter", "Binding item: ${item.title}")
                title.text = item.title
                imageView.load(item.image)

                // Set an onClickListener for the ImageView
                imageView.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }
}
