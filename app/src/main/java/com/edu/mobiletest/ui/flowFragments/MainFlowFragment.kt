package com.edu.mobiletest.ui.flowFragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.viewBinding
import com.edu.mobiletest.R
import com.edu.mobiletest.databinding.FragmentMainFlowBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFlowFragment : Fragment(R.layout.fragment_main_flow) {


    private val navController by lazy {
        (childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }
    private val viewModel by hiltNavGraphViewModels<MainFlowViewModel>(R.id.mainFlowFragment)

    @Inject
    lateinit var imageLoader: IImageLoader

    private val binding by viewBinding(FragmentMainFlowBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProfileImageUrl()

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.itemIconTintList = null

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible =
                destination.id == R.id.groupListFragment || destination.id == R.id.nav_profile || destination.id == R.id.chatFragment
        }
        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->

            imageLoader.loadProfileImageCircleShapeAndSignature(
                url,
                Calendar.getInstance().timeInMillis
            ) {
                binding.bottomNavigationView.menu.findItem(R.id.nav_profile).icon = it
            }
        }
    }

}