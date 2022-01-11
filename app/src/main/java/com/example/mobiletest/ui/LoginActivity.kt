package com.example.mobiletest.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobiletest.databinding.ActivityLoginBinding
import com.example.mobiletest.di.DaggerApplicationComponent
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DaggerApplicationComponent.builder().build().inject(this)
        if (auth.currentUser != null){

        }else{

        }
    }
}