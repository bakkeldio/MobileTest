package com.edu.mobiletest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.edu.common.presentation.model.NetworkStatus
import com.edu.common.utils.NetworkStatusHelper
import com.edu.mobiletest.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    @Inject
    lateinit var auth: FirebaseAuth

    private var fromUnAvailable = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment).navController
        chooseStartDestination()

        navigateToChatFragment(navController, intent.getStringExtra("USER_ID"))

        NetworkStatusHelper(this).observe(this) { status ->
            when (status) {
                is NetworkStatus.UnAvailable -> {
                    fromUnAvailable = true
                    Toast.makeText(this, "Интернет соединение пропало", Toast.LENGTH_LONG).show()
                }
                is NetworkStatus.Available -> {
                    if (fromUnAvailable) {
                        Toast.makeText(this, "Интернет соединение восстановлено", Toast.LENGTH_LONG)
                            .show()
                        fromUnAvailable = false
                    }
                }
            }
        }
    }

    private fun chooseStartDestination() {
        val graph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        if (auth.currentUser == null) {
            graph.setStartDestination(R.id.signInFlowFragment)
        } else {
            graph.setStartDestination(R.id.mainFlowFragment)
        }
        navController.graph = graph
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToChatFragment(
            findNavController(R.id.nav_host_fragment_main),
            intent?.getStringExtra("USER_ID")
        )
    }

    private fun navigateToChatFragment(navController: NavController, userId: String?) {
        userId?.let {
            navController.navigate(R.id.navigation_chat, bundleOf("USER_ID" to it))
        }
    }

}