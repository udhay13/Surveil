package com.crayosa.surveil

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.crayosa.surveil.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        val startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
                if(it.resultCode == Activity.RESULT_OK){
                    auth.addAuthStateListener { fatuh ->
                        if(fatuh.currentUser != null){
                            loadFragment()
                        }
                    }
                }
            }

        if (auth.currentUser != null) {
            loadFragment()
        }else{
            Log.d("DATA","not valid")
            startForResult.launch(Intent(baseContext,LoginActivity::class.java))
        }



    }
    private fun loadFragment(){
        val auth = FirebaseAuth.getInstance()
        Firebase.firestore.collection("users").document(auth.currentUser!!.uid).let{document ->
            document.get().addOnSuccessListener { snapshot ->
                if(!snapshot.exists()){
                    document.set(mapOf("name" to auth.currentUser!!.displayName))
                }
            }
        }

        findViewById<View>(R.id.main_frag).findNavController().setGraph(R.navigation.nav)
        setupActionBarWithNavController(findNavController(R.id.main_frag))
    }

}