package com.example.minerva_10

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update the categories list with additional categories.
        val categories = listOf(
            Category("Top Airing", listOf(Item("Item 1"), Item("Item 2"), Item("Item 3"), Item("Item 4"), Item("Item 5"), Item("Item 6"), Item("Item 7"), Item("Item 8"), Item("Item 9"), Item("Item 10"))),
            Category("Adventure", listOf(Item("Item 11"), Item("Item 12"), Item("Item 13"), Item("Item 14"), Item("Item 15"), Item("Item 16"), Item("Item 17"), Item("Item 18"), Item("Item 19"), Item("Item 20"))),
            Category("Slice of Life", listOf(Item("Item 21"), Item("Item 22"), Item("Item 23"), Item("Item 24"), Item("Item 25"), Item("Item 26"),Item("Item 27"), Item("Item 28"), Item("Item 29"), Item("Item 30"))),
            Category("Isekai", listOf(Item("Item 31"), Item("Item 32"), Item("Item 33"), Item("Item 34"), Item("Item 35"), Item("Item 36"), Item("Item 37"), Item("Item 38"), Item("Item 39"), Item("Item 40"))),
            Category("Action", listOf(Item("Item 41"), Item("Item 42"), Item("Item 43"), Item("Item 44"), Item("Item 45"), Item("Item 46"), Item("Item 47"), Item("Item 48"), Item("Item 49"), Item("Item 50"))),
            Category("Shounen", listOf(Item("Item 51"), Item("Item 52"), Item("Item 53"), Item("Item 54"), Item("Item 55"), Item("Item 56"), Item("Item 57"), Item("Item 58"), Item("Item 59"), Item("Item 60"))),
            Category("Comedy", listOf(Item("Item 61"), Item("Item 62"), Item("Item 63"), Item("Item 64"), Item("Item 65"), Item("Item 66"), Item("Item 67"), Item("Item 68"), Item("Item 69"), Item("Item 70"))),
            Category("Seinen", listOf(Item("Item 71"), Item("Item 72"), Item("Item 73"), Item("Item 74"), Item("Item 75"), Item("Item 76"), Item("Item 77"), Item("Item 78"), Item("Item 79"), Item("Item 80"))),
        )

        // Set up the parent RecyclerView
        val parentRecyclerView: RecyclerView = view.findViewById(R.id.parentRecyclerView)
        parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        parentRecyclerView.adapter = ParentAdapter(categories)
    }
}


