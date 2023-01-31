package com.crayosa.surveil.fragments.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crayosa.surveil.datamodels.ClassRoom
import com.crayosa.surveil.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragViewModel(app : Application) : AndroidViewModel(app) {
    private var _classList = MutableLiveData<MutableList<ClassRoom?>>()
    private val auth = FirebaseAuth.getInstance()
    val classList : LiveData<MutableList<ClassRoom?>>
        get() = _classList

    init {
        reload()
    }

    private fun reload(){
        viewModelScope.launch {
            FirebaseRepository(Firebase.firestore)
                .getEnrolledRooms(auth.currentUser!!.uid).collectLatest {
                    _classList.value = it
                }
        }
    }
}