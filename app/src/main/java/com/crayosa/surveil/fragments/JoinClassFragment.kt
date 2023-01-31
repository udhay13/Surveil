package com.crayosa.surveil.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.FragmentJoinClassBinding
import com.crayosa.surveil.datamodels.Users
import com.crayosa.surveil.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class JoinClassFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val auth = FirebaseAuth.getInstance()
        val binding = DataBindingUtil.inflate<FragmentJoinClassBinding>(
            inflater,R.layout.fragment_join_class,container,false
        )
        binding.joinButton.setOnClickListener {
            lifecycleScope.launch {
                FirebaseRepository(Firebase.firestore)
                    .joinClassRoom(binding.classCodeField.text.toString(),
                        Users(auth.currentUser!!.uid, auth.currentUser!!.displayName!!,emptyList()))
            }
        }
        return binding.root
    }

}