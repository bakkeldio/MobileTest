package com.edu.mobiletest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.edu.common.presentation.model.NetworkStatus
import com.edu.common.utils.NetworkStatusHelper
import com.edu.mobiletest.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var auth: FirebaseAuth

    private var fromUnAvailable = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView = binding.bottomNavigationView
        val navController = findNavController(R.id.nav_host_fragment_main)
        navView.setupWithNavController(navController)

        navigateToChatFragment(navController, intent.getStringExtra("USER_ID"))
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible =
                destination.id == R.id.groupListFragment || destination.id == R.id.nav_profile || destination.id == R.id.chatFragment
        }

        NetworkStatusHelper(this).observe(this) { status ->
            when (status) {
                is NetworkStatus.UnAvailable -> {
                    fromUnAvailable = true
                    Toast.makeText(this, "Интернет соединение пропало", Toast.LENGTH_LONG).show()
                }
                is NetworkStatus.Available -> {
                    if (fromUnAvailable){
                        Toast.makeText(this, "Интернет соединение восстановлено", Toast.LENGTH_LONG)
                            .show()
                        fromUnAvailable = false
                    }
                }
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToChatFragment(
            findNavController(R.id.nav_host_fragment_main),
            intent?.getStringExtra("USER_ID")
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun navigateToChatFragment(navController: NavController, userId: String?) {
        userId?.let {
            navController.navigate(R.id.navigation_chat, bundleOf("USER_ID" to it))
        }
    }

}