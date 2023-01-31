package com.crayosa.surveil.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.FragmentCreateClassRoomBinding
import com.crayosa.surveil.datamodels.ClassRoom
import com.crayosa.surveil.datamodels.Users
import com.crayosa.surveil.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateClassRoom : Fragment() {
    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentCreateClassRoomBinding>(
            layoutInflater, R.layout.fragment_create_class_room, container, false
        )
        val user = FirebaseAuth.getInstance().currentUser!!

        val radioButtons = mutableListOf(binding.rc1, binding.rc2, binding.rc3, binding.radioMaleHero, binding.radioFemaleHero)
        for (radioButton in radioButtons){
            radioButton.setOnClickListener {
                when(it.id){
                    R.id.rc1 -> binding.classPreview.classroomCard.backgroundTintList = resources.getColorStateList(R.color.classroom_card_1)
                    R.id.rc2 -> binding.classPreview.classroomCard.backgroundTintList = resources.getColorStateList(R.color.classroom_card_2)
                    R.id.rc3 -> binding.classPreview.classroomCard.backgroundTintList = resources.getColorStateList(R.color.classroom_card_3)
                    R.id.radio_male_hero -> binding.classPreview.heroImage.setImageResource(R.drawable.classes_male_image)
                    R.id.radio_female_hero -> binding.classPreview.heroImage.setImageResource(R.drawable.classes_female_image)

                }
            }
        }

        binding.submitClass.setOnClickListener {
            FirebaseRepository(Firebase.firestore)
                .addClassRoom(
                    ClassRoom(null, binding.roomName.text.toString(), binding.roomSectionName
                        .text.toString(),binding.roomFacultyName.text.toString(),
                        when(binding.colorGroup.checkedRadioButtonId){
                            R.id.rc1 -> "#ffc700"
                            R.id.rc2 -> "#a079f3"
                            R.id.rc3 -> "#9f9ca4"
                        else -> "#ffc700"
                    },when(binding.genderGroup.checkedRadioButtonId){
                        R.id.radio_male_hero -> "MALE"
                            R.id.radio_female_hero -> "FEMALE"
                        else -> "MALE"
                    }),
                    Users(user.uid, user.displayName!!, emptyList()
                ))
            requireView().findNavController().navigateUp()

        }

        val watcher1 = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.classPreview.className.text = s
            }
            override fun afterTextChanged(s: Editable?) {}

        }
        val watcher2 = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.classPreview.courseName.text = s
            }
            override fun afterTextChanged(s: Editable?) {}

        }
        val watcher3 = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.classPreview.courseStaffName.text = s
            }
            override fun afterTextChanged(s: Editable?) {}

        }

        binding.roomName.addTextChangedListener(watcher1)
        binding.roomSectionName.addTextChangedListener(watcher2)
        binding.roomFacultyName.addTextChangedListener(watcher3)

        return binding.root
    }
}