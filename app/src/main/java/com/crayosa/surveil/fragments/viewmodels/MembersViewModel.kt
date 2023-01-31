package com.crayosa.surveil.fragments.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crayosa.surveil.datamodels.ClassRoom
import com.crayosa.surveil.datamodels.Members
import com.crayosa.surveil.repository.FirebaseRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MembersViewModel(app : Application, val classroom : ClassRoom) : AndroidViewModel(app) {
    private val _members = MutableLiveData<List<Members>>()
    val members : LiveData<List<Members>>
        get() = _members
    init {
        viewModelScope.launch {
            FirebaseRepository(FirebaseFirestore.getInstance()).getClassRoomMembers(classroom.id!!).collectLatest {
                _members.value = it
            }
        }
    }
}