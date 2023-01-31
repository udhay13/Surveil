package com.crayosa.surveil.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.FragmentAddLectureBinding
import com.crayosa.surveil.datamodels.Lecture
import com.crayosa.surveil.repository.FirebaseRepository
import com.crayosa.surveil.utils.YouTubeUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


class AddLectureFragment : Fragment() {

    private  var youTubePlayer : YouTubePlayer? = null
    val args : AddLectureFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentAddLectureBinding>(
            inflater, R.layout.fragment_add_lecture, container, false
        )

        binding.testVideoUrl.setOnClickListener {
            val link = binding.lectureLinkEditText.text.toString()
            youTubePlayer?.loadVideo(YouTubeUtils.getId(link),0.0f)
        }
        val listener = object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                this@AddLectureFragment.youTubePlayer = youTubePlayer
            }
        }
        binding.submitLecture.setOnClickListener {
            FirebaseRepository(Firebase.firestore)
                .addLectures(
                    Lecture(null,binding.lectureLinkEditText.text.toString(),binding.lectureTopicEditText.text.toString()),
                    args.classroom.id!!
                )
        }
        binding.defaultYouTubePlayer.initialize(listener)
        return binding.root
    }
}