package com.crayosa.surveil.fragments

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.crayosa.surveil.R
import com.crayosa.surveil.adapters.ClassRoomListAdapter
import com.crayosa.surveil.databinding.FragmentHomeBinding
import com.crayosa.surveil.datamodels.ClassRoom
import com.crayosa.surveil.fragments.viewmodels.HomeFragViewModel
import com.crayosa.surveil.listener.OnItemClickListener
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private val viewModel : HomeFragViewModel by viewModels{
        HomeFragmentViewModelFactory(requireActivity().application)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        var fabShowing = false
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        if (auth.currentUser != null) {
            val adapter = ClassRoomListAdapter(object : OnItemClickListener(){
                override fun onClick(classroom: ClassRoom) {
                    requireView().findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToClassRoomFragment(classroom))
                }
            })

            viewModel.classList.observe(viewLifecycleOwner){
                if (it.isNotEmpty()){
                    adapter.submitList(it)
                    binding.classRoomList.visibility = View.VISIBLE
                    binding.homeBg.visibility = View.GONE
                }else{
                    binding.classRoomList.visibility = View.GONE
                    binding.homeBg.visibility = View.VISIBLE
                }
            }


            binding.classRoomList.adapter = adapter
            binding.createRoom.setOnClickListener {
                when(fabShowing) {
                    true ->{
                        binding.createFab.visibility = View.GONE
                        binding.joinFab.visibility = View.GONE
                        fabShowing = false
                    }
                    false ->{
                        binding.createFab.visibility = View.VISIBLE
                        binding.joinFab.visibility = View.VISIBLE
                        fabShowing = true
                    }
                }
            }
            binding.createFab.setOnClickListener {
                requireView().findNavController()
                    .navigate(R.id.action_homeFragment_to_createClassRoom)
            }
            binding.joinFab.setOnClickListener {
                requireView().findNavController()
                    .navigate(R.id.action_homeFragment_to_joinClassFragment)
            }
        }
        return binding.root
    }

}

class HomeFragmentViewModelFactory(val app : Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeFragViewModel::class.java)){
            return HomeFragViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }

}