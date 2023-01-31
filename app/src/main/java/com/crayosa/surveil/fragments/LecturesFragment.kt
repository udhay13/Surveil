package com.crayosa.surveil.fragments

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.crayosa.surveil.R
import com.crayosa.surveil.adapters.LecturesListAdapter
import com.crayosa.surveil.databinding.FragmentLecturesBinding
import com.crayosa.surveil.datamodels.ClassRoom
import com.crayosa.surveil.datamodels.Lecture
import com.crayosa.surveil.datamodels.Progress
import com.crayosa.surveil.fragments.viewmodels.LecturesViewModel
import com.crayosa.surveil.listener.OnItemClickListener
import com.crayosa.surveil.repository.FirebaseRepository
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LecturesFragment : Fragment() {
    private val args : LecturesFragmentArgs by navArgs()
    private val user = FirebaseAuth.getInstance().currentUser!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentLecturesBinding>(inflater,R.layout.fragment_lectures, container, false)
        var admin = false

        val viewModel : LecturesViewModel by viewModels{LecturesVMFactory(
            requireActivity().application,
            args.classroom.id!!,
            user.uid
        )}
        val adapter = LecturesListAdapter(object : OnItemClickListener(){
            override fun onClick(lecture: Lecture,position : Int) {

                if(admin){
                    requireActivity().findViewById<View>(R.id.main_frag)
                        .findNavController().navigate(ClassRoomFragmentDirections.actionClassRoomFragmentToReportFragment(
                            args.classroom.id!!, lecture.id!!
                        ))
                }
                else {
                    ProgressDialogFragment(args.classroom, lecture)
                        .show(childFragmentManager, "DialogFragment")
                }
            }
        })
        viewModel.lectureList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
        viewModel.isAdmin.observe(viewLifecycleOwner){
            binding.addLecture.visibility = when(it){true->View.VISIBLE false->View.GONE}
            admin = it
        }

        binding.lectureList.adapter = adapter
        binding.addLecture.setOnClickListener {
            binding.root.findNavController().navigate(LecturesFragmentDirections.actionLecturesFragmentToAddLectureFragment(args.classroom))
        }
        return binding.root
    }
}

class LecturesVMFactory(val app : Application, val id: String, private val uid : String) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LecturesViewModel::class.java))
            return LecturesViewModel(app, id, uid) as T
        throw IllegalArgumentException("Unknown ViewModel")
    }
}

class ProgressDialogFragment(val classRoom  : ClassRoom, val lecture : Lecture) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var progress : Progress? = null
        val dialog = AlertDialog.Builder(context)
            .setTitle("Completion")
            .setView(R.layout.layout_progress_item)
            .setPositiveButton("Play"){ _, _ ->
                requireActivity().findViewById<View>(
                    R.id.main_frag
                ).findNavController().navigate(
                    ClassRoomFragmentDirections.actionClassRoomFragmentToPlayerFragment(
                        lecture, classRoom, progress
                    )
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create()

        lifecycleScope.launch {
            val user = FirebaseAuth.getInstance()
                .currentUser!!
            FirebaseRepository(Firebase.firestore)
                .getProgress(classRoom.id!!, lecture.id!!, FirebaseAuth.getInstance()
                    .currentUser!!.uid, user.displayName!!)
                .collectLatest {
                    progress = it
                    dialog.findViewById<TextView>(R.id.progress_display_name).text = it.name
                    dialog.findViewById<LinearProgressIndicator>(R.id.progress_bar).progress = it.completion.toInt()
                }
        }


        return dialog
    }
}
