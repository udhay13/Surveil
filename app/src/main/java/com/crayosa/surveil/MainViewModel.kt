package com.crayosa.surveil

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import java.lang.IllegalArgumentException

class MainViewModel(app : Application) : AndroidViewModel(app) {
    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn : LiveData<Boolean>
        get() = _loggedIn
    fun setLoggedIn(bool : Boolean){
        _loggedIn.value = bool
        Log.d("DATA",bool.toString())
    }
}

class MainViewModelFactory(val app: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }

}