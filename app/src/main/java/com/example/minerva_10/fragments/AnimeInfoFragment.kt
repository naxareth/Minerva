package com.example.minerva_10.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.minerva_10.R
import com.example.minerva_10.api.responses.AnimeInfo
import coil.load

class AnimeInfoFragment : Fragment() {

    private var animeInfo: AnimeInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the anime info passed as an argument
        animeInfo = arguments?.getParcelable(ARG_ANIME_INFO)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_animeinfo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind the anime info to the UI components
        val titleTextView: TextView = view.findViewById(R.id.animeTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.animeDescription)
        val imageView: ImageView = view.findViewById(R.id.animeImage)

        titleTextView.text = animeInfo?.title
        descriptionTextView.text = animeInfo?.description
        imageView.load(animeInfo?.image)
    }

    companion object {
        private const val ARG_ANIME_INFO = "arg_anime_info"

        // Function to create a new instance of AnimeInfoFragment with anime info
        fun newInstance(animeInfo: AnimeInfo): AnimeInfoFragment {
            val fragment = AnimeInfoFragment()
            val args = Bundle()
            args.putParcelable(ARG_ANIME_INFO, animeInfo)
            fragment.arguments = args
            return fragment
        }
    }
}