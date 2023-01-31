package com.crayosa.surveil.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.crayosa.surveil.R
import com.crayosa.surveil.adapters.MembersListAdapter
import com.crayosa.surveil.databinding.FragmentMembersBinding
import com.crayosa.surveil.datamodels.ClassRoom
import com.crayosa.surveil.datamodels.Members
import com.crayosa.surveil.fragments.viewmodels.MembersViewModel


class MembersFragment : Fragment() {

    private val args : MembersFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentMembersBinding>(inflater,R.layout.fragment_members, container, false)
        val viewModel : MembersViewModel by viewModels{MembersViewModelFactory(
            requireActivity().application, args.classroom
        )}
        val membersListAdapter = MembersListAdapter()

        viewModel.members.observe(viewLifecycleOwner){
            membersListAdapter.submitList(buildList(it))
        }

        binding.membersListView.adapter = membersListAdapter

        return binding.root
    }



    companion object{
        const val HEADER_ADMIN = "##HEAD_ADMIN##"
        const val HEADER_STUDENT = "##HEAD_STUDENT##"
    }

    private fun buildList(list : List<Members>) : List<String>{
        val result = mutableListOf(HEADER_ADMIN)
        val lastPart = mutableListOf(HEADER_STUDENT)
        for(item in list){
            if(item.role == 0L){
                result.add(item.name)
            }else{
                lastPart.add(item.name)
            }
        }

        return result.plus(lastPart)
    }

}

class MembersViewModelFactory(val app : Application, val classroom : ClassRoom) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MembersViewModel::class.java)){
            return MembersViewModel(app, classroom) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }

}