package com.example.minerva_10.viewmodels

import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.SimpleExoPlayer

class VideoPlayerViewModel : ViewModel() {
    var player: SimpleExoPlayer? = null
    var playbackPosition: Long = 0
}