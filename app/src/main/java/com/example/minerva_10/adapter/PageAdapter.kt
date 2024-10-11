package com.example.minerva_10.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R

class PageAdapter(private val totalPages: Int, private val onPageClick: (Int) -> Unit) :
    RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_page_number, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(position + 1) // Page numbers start from 1
    }

    override fun getItemCount(): Int = totalPages

    inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val pageNumberTextView: TextView = view.findViewById(R.id.pageNumber)

        fun bind(pageNumber: Int) {
            pageNumberTextView.text = pageNumber.toString()
            pageNumberTextView.setOnClickListener {
                onPageClick(pageNumber)
            }
        }
    }
}
