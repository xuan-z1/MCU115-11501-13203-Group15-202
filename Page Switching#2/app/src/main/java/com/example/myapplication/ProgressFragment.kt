package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ProgressFragment : Fragment(R.layout.frag_progress) {
    private val vm: WorkViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bar = view.findViewById<ProgressBar>(R.id.progressBar)
        val text = view.findViewById<TextView>(R.id.txt)

        bar?.isIndeterminate = true  // placeholder to keep indeterminate default

        vm.status.observe(viewLifecycleOwner) { s ->
            text?.text = s
            if (s.startsWith("Working")) {
                bar?.isIndeterminate = false
                startMusic()
            } else {
                stopMusic()
            }
        }

        vm.progress.observe(viewLifecycleOwner) { p ->
            if (bar?.isIndeterminate == false) {
                bar.max = 100
                bar.progress = p
                text?.text = "Working… %d%%".format(p)
                val currentStatus = vm.status.value.orEmpty()
                if (currentStatus.startsWith("Working")) {
                    startMusic()
                }
            }
        }
    }

    private fun startMusic() {
        val ctx = context?.applicationContext ?: return
        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(ctx, R.raw.bg_music)?.apply {
                    setVolume(1.0f, 1.0f)
                    isLooping = true
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (mediaPlayer?.isPlaying == false) {
            try {
                mediaPlayer?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopMusic() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }

    override fun onPause() {
        super.onPause()
        stopMusic()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMusic()
    }
}