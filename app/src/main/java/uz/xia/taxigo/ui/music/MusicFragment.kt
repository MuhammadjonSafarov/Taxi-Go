package uz.xia.taxigo.ui.music

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioRouting
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.FragmentMusicBinding
import uz.xia.taxigo.utils.lazyFast
import java.io.File
import java.io.IOException

private const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1
@AndroidEntryPoint
class MusicFragment : Fragment() {
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private var playButton: Button? = null
    private var seekBar: SeekBar? = null
    private val mediaPlayer: MediaPlayer by lazyFast { MediaPlayer.create(requireContext(), R.raw.song)}
    private var isPlaying = false
    private var mHandler: Handler = Handler(Looper.getMainLooper())

    private val mUpdateSeekbar = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                val currentPosition: Int = mediaPlayer.currentPosition
                val totalDuration: Int = mediaPlayer.duration
                binding.seekBar.progress = currentPosition * 100 / totalDuration
                mHandler.postDelayed(this, 1000)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Check for permission to read external storage
        // Check for permission to read external storage
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
            )
        }

        // Construct the path to your music file

        // Construct the path to your music file
        val downloadsFolder: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val musicFileName = "UndergroundAcademy.mp3"
        val musicFile = File(downloadsFolder, musicFileName)
        //Create media player

        //Create media player

        try {
            mediaPlayer.setDataSource(musicFile.absolutePath)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mediaPlayer.addOnRoutingChangedListener(object :AudioRouting.OnRoutingChangedListener{
                    override fun onRoutingChanged(audioRouting: AudioRouting?) {

                    }

                },Handler(Looper.getMainLooper()))
            }
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newPosition: Int = mediaPlayer.duration * progress / 100
                    mediaPlayer.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.start()
            }
        })


        //Setup playback button


        //Setup playback button

        binding.playButton.setOnClickListener(View.OnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                binding.playButton.setText("Play")
                //remove the callbacks to the Runnable when the MediaPlayer stops
                mHandler.removeCallbacks(mUpdateSeekbar)
            } else {
                mediaPlayer.start()
                binding.playButton.setText("Pause")
                //start the Runnable when the MediaPlayer starts playing
                mHandler.postDelayed(mUpdateSeekbar, 1000)
            }
            isPlaying = !isPlaying
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer.release()
            mHandler.removeCallbacks(mUpdateSeekbar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}