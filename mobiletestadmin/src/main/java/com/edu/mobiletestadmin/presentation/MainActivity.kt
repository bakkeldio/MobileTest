package com.edu.mobiletestadmin.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.edu.mobiletestadmin.NavGraphDirections
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.databinding.ActivityMainBinding
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.viewModel.MainViewModel
import com.edu.mobiletestadmin.utils.buildMaterialAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        if (viewModel.isAdminSignedIn()) {
            navGraph.setStartDestination(R.id.usersFragment)
        } else {
            navGraph.setStartDestination(R.id.loginFragment)
        }
        navController.graph = navGraph
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible =
                destination.id == R.id.groupsFragment || destination.id == R.id.usersFragment
        }

        binding.bottomNavigation.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            this.buildMaterialAlertDialog(
                resources.getString(R.string.sure_to_logout),
                positiveBtnClick = {
                    viewModel.signOut()
                }).show()
            true
        }

        viewModel.adminLogoutState.observe(this) { state ->
            when (state) {
                is ResourceState.Success -> {
                    navController.navigate(NavGraphDirections.fromMainAppFlowToLoginFragmnet())
                }
                is ResourceState.Error -> {
                    Toast.makeText(this, state.error, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }
}