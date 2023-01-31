package com.crayosa.surveil.fragments.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crayosa.surveil.datamodels.Lecture
import com.crayosa.surveil.repository.FirebaseRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LecturesViewModel(app : Application,val id : String, val uid : String)  : AndroidViewModel(app) {
    private val _lectureList = MutableLiveData<MutableList<Lecture>>()
    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin : LiveData<Boolean>
        get() = _isAdmin
    val lectureList : LiveData<MutableList<Lecture>>
        get() = _lectureList

    init {
        viewModelScope.launch {
            FirebaseRepository(Firebase.firestore)
                .getLectures(id).collectLatest {
                    _lectureList.value = it
                }
        }
        viewModelScope.launch {
            FirebaseRepository(Firebase.firestore)
                .isAdmin(id, uid).collectLatest {
                    _isAdmin.value = it
                }
        }
    }
}