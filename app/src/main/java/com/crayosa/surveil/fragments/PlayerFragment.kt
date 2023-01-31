package com.crayosa.surveil.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.FragmentPlayerBinding
import com.crayosa.surveil.databinding.LayoutPlayerControlBinding
import com.crayosa.surveil.datamodels.Progress
import com.crayosa.surveil.repository.FirebaseRepository
import com.crayosa.surveil.utils.YouTubeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.TimeUtilities
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBarListener


class PlayerFragment : Fragment() {
    private var duration : Float = 0f
    private var youTubePlayer : YouTubePlayer? = null
    private var state : PlayerConstants.PlayerState? = null
    val args : PlayerFragmentArgs by navArgs()
    private lateinit var binding: FragmentPlayerBinding
    private val viewModel : PlayerViewModel by viewModels{PLayerViewModelFactory(
        requireActivity().application
    )}

    private fun togglePlayer(){
        if(state != null){
            if(state == PlayerConstants.PlayerState.PLAYING) youTubePlayer?.pause()
            else if(state == PlayerConstants.PlayerState.PAUSED) youTubePlayer?.play()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_player, container, false
        )



        var curPos = 0
        val player = DataBindingUtil.inflate<LayoutPlayerControlBinding>(
            inflater, R.layout.layout_player_control, container, false
        )
        val fadeViewHelper = FadeViewHelper(player.controlsContainer).apply {
            animationDuration = FadeViewHelper.DEFAULT_ANIMATION_DURATION
            fadeOutDelay = 3000L
        }
        val tracker = YouTubePlayerTracker()
        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        lifecycle.addObserver(binding.youTubePlayerView)

        val listener = object: AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                this@PlayerFragment.youTubePlayer = youTubePlayer
                youTubePlayer.loadVideo(
                    YouTubeUtils.getId(args.lecture.url),
                    0.0f
                )
                player.uiControlPlayback.visibility = View.VISIBLE
                youTubePlayer.addListener(fadeViewHelper)
                youTubePlayer.addListener(tracker)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                player.uiControlPlayback.setImageResource(when(state){
                    PlayerConstants.PlayerState.PLAYING -> R.drawable.ic_baseline_pause_24
                    else -> R.drawable.ic_baseline_play_arrow_24
                })
                this@PlayerFragment.state = state
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                viewModel.setSeconds(second)
                curPos = second.toInt()
                player.youtubePlayerSeekbar.videoCurrentTimeTextView.text = TimeUtilities.formatTime(second)
                player.youtubePlayerSeekbar.seekBar.progress = second.toInt()
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                super.onVideoDuration(youTubePlayer, duration)
                this@PlayerFragment.duration = duration
                if(args.progress != null){
                    viewModel.setSeconds(args.progress!!.completion * duration / 100)
                }
                player.youtubePlayerSeekbar.seekBar.max = duration.toInt()
                player.youtubePlayerSeekbar.videoDurationTextView.text = TimeUtilities.formatTime(duration)
            }

        }
        binding.youTubePlayerView.setCustomPlayerUi(player.root)

        player.controlLayout.setOnClickListener{
            fadeViewHelper.toggleVisibility()
        }

        player.uiControlPlayback.setOnClickListener {
            togglePlayer()
        }

        binding.youTubePlayerView.initialize(listener, IFramePlayerOptions.Builder()
            .controls(0)
            .build()
        )

        player.youtubePlayerSeekbar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener{
            override fun seekTo(time: Float) {
                youTubePlayer!!.seekTo(if(viewModel.getWatchTime() > time) time else {
                    player.youtubePlayerSeekbar.seekBar.progress = curPos
                    curPos.toFloat()
                })
            }

        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        val user = FirebaseAuth.getInstance().currentUser!!
        FirebaseRepository(Firebase.firestore)
            .addProgress(args.classroom.id!!, args.lecture.id!!,
                Progress(user.uid, user.displayName!!, viewModel.getWatchTime()/duration*100)
            )
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onDestroyView() {
        super.onDestroyView()
        binding.youTubePlayerView.release()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (requireActivity() as AppCompatActivity).supportActionBar!!.show()
    }
}

@Suppress("UNCHECKED_CAST")
class PLayerViewModelFactory(private val app : Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlayerViewModel::class.java)){
            return PlayerViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }

}
class PlayerViewModel(app : Application) : AndroidViewModel(app){
    private var watchedSeconds = 0.0f
    fun setSeconds(seconds : Float){
        if(seconds > watchedSeconds){
            watchedSeconds = seconds
        }
    }
    fun getWatchTime() : Float = watchedSeconds
}