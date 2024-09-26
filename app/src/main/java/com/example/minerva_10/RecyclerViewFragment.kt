package com.example.minerva_10

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = listOf(
            Category("Popular", listOf(Item("Item 1"), Item("Item 2"), Item("Item 3"))),
            Category("Romance", listOf(Item("Item 4"), Item("Item 5"), Item("Item 6"))),
            Category("Action", listOf(Item("Item 7"), Item("Item 8"), Item("Item 9")))
        )

        val parentRecyclerView: RecyclerView = view.findViewById(R.id.parentRecyclerView)
        parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        parentRecyclerView.adapter = ParentAdapter(categories)
    }
}

