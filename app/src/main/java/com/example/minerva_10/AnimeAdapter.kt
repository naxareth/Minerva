package com.example.minerva_10

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
        private val shimmerLayout: com.facebook.shimmer.ShimmerFrameLayout = view.findViewById(R.id.shimmerLayout)

        fun bind(item: Item, onItemClick: (Item) -> Unit) {
            Log.d("AnimeAdapter", "Binding item: ${item.title}")

            // Show shimmer while loading
            shimmerLayout.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            title.visibility = View.GONE

            // Load data
            title.text = item.title
            imageView.load(item.image) {
                listener(
                    onSuccess = { _, _ ->
                        // Hide shimmer once image is loaded
                        shimmerLayout.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        title.visibility = View.VISIBLE
                    },
                    onError = { _, _ ->
                        // Hide shimmer in case of error
                        shimmerLayout.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        title.visibility = View.VISIBLE
                    }
                )
            }

            // Set an onClickListener for the ImageView
            imageView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
