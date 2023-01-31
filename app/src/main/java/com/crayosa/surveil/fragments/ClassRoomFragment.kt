package com.crayosa.surveil.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.FragmentClassRoomBinding


class ClassRoomFragment : Fragment() {

    val args : ClassRoomFragmentArgs by navArgs()
    private lateinit var binding: FragmentClassRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_class_room, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.class_info,menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var selected = 0

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.lecture ->{
                    if (selected != 0){
                        selected = 0
                        requireView().findViewById<View>(R.id.class_frag).findNavController()
                            .navigate(
                                MembersFragmentDirections.actionMembersFragmentToLecturesFragment(
                                    args.classroom
                                )
                            )
                    }
                    true
                }
                R.id.people ->{
                    if(selected != 1){
                        selected = 1
                        requireView().findViewById<View>(R.id.class_frag).findNavController()
                            .navigate(
                                LecturesFragmentDirections.actionLecturesFragmentToMembersFragment(
                                    args.classroom
                                )
                            )
                    }
                    true
                }
                else -> false
            }
        }
        requireView().findViewById<View>(R.id.class_frag).findNavController().setGraph(R.navigation.class_nav, Bundle().apply {
            putParcelable("classroom", args.classroom)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.class_info -> {
                requireView().findNavController().navigate(ClassRoomFragmentDirections.
                actionClassRoomFragmentToClassRoomInfoFragment(args.classroom))
            }
        }
        return false
    }
}