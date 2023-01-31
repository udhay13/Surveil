package com.crayosa.surveil.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.crayosa.surveil.R
import com.crayosa.surveil.adapters.ProgressListAdapter
import com.crayosa.surveil.databinding.FragmentReportBinding
import com.crayosa.surveil.datamodels.Progress
import com.crayosa.surveil.repository.FirebaseRepository
import com.crayosa.surveil.views.ReportChildView
import com.crayosa.surveil.views.ReportHeadView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReportFragment : Fragment() {
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
            // Inflate the layout for this fragment
            val binding = DataBindingUtil.inflate<FragmentReportBinding>(
                inflater, R.layout.fragment_report, container, false
            )
            val args : ReportFragmentArgs by navArgs()

            lifecycleScope.launch {
              FirebaseRepository(Firebase.firestore).getProgressList(args.cID, args.lID).collectLatest {
                  val completedList = mutableListOf<Progress>()
                  val inProgressList = mutableListOf<Progress>()
                  val notStartedList = mutableListOf<Progress>()
                  for(progress in it){
                      if(progress.completion < 2){
                          notStartedList.add(progress)
                      }
                      else if(progress.completion > 95){
                          completedList.add(progress)
                      }
                      else inProgressList.add(progress)
                  }
                  binding.progressList.addView(ReportHeadView("Completed",completedList.size))
                  for (p in  completedList)
                      binding.progressList.addView(ReportChildView(p))
                  binding.progressList.addView(ReportHeadView("In Progress",inProgressList.size))
                  for (p in  inProgressList)
                      binding.progressList.addView(ReportChildView(p))
                  binding.progressList.addView(ReportHeadView("Not Started",notStartedList.size))
                  for (p in  notStartedList)
                      binding.progressList.addView(ReportChildView(p))

              }

            }

            return binding.root
    }
}