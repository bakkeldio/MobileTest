package com.example.group.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.example.common.navigation.Navigation
import com.example.common.presentation.ResourceState
import com.example.common.utils.showToast
import com.example.group.R
import com.example.group.databinding.FragmentGroupDetailBinding
import com.example.group.presentation.adapter.GroupTestsAdapter
import com.example.group.presentation.model.GroupData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupDetailFragment : Fragment() {


    private val viewModel by viewModels<GroupDetailViewModel>()

    private var _binding: FragmentGroupDetailBinding? = null

    private val binding: FragmentGroupDetailBinding
        get() = _binding!!

    private var groupName: String = ""
    private val args: GroupDetailFragmentArgs by navArgs()

    private val adapter: GroupTestsAdapter = GroupTestsAdapter {
        findNavController()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchGroupDetails(args.groupId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setupWithNavController(findNavController())

        viewModel.groupDetailsState.observe(viewLifecycleOwner, { state ->
            binding.progress.root.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    bindGroupDetailsData(state.data)
                }
                is ResourceState.Error -> {
                    showToast(state.message)
                }
                else -> Unit
            }
        })
        viewModel.enterGroupState.observe(viewLifecycleOwner, { state ->
            binding.progress.root.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {

                }
                is ResourceState.Error -> {
                    showToast(state.message)
                }
                else -> Unit
            }
        })
        viewModel.leaveGroupState.observe(viewLifecycleOwner, { state ->
            binding.progress.root.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {

                }
                is ResourceState.Error -> {
                    binding.enterBtn.isChecked = true
                    showToast(state.message)
                }
                else -> Unit
            }
        })

        val alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.alert_msg_leaving_group).format(groupName))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                binding.enterBtn.isChecked = true
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.agree)) { dialog, _ ->
                viewModel.leaveFromGroup(args.groupId)
                dialog.dismiss()
            }
        binding.enterBtn.setOnClickListener {
            if (!binding.enterBtn.isChecked) {
                alertDialog.show()
            } else {
                viewModel.enterToGroup(args.groupId)
            }
        }

        binding.testLayout.setOnClickListener {
            val pair = Navigation.Tests.navigateToTests(args.groupId)
            findNavController().navigate(pair.first, pair.second)
        }
        setAdapter()
    }

    private fun bindGroupDetailsData(data: GroupData) {
        groupName = data.detail.groupName
        binding.title.text = data.detail.groupName
        binding.testsCount.text = data.detail.testsCount.toString()
        binding.groupButtons.isVisible =
            data.userGroup.isNullOrEmpty() || data.userGroup == args.groupId
        binding.enterBtn.isChecked = data.userGroup == args.groupId
        adapter.submitTests(data.tests)
        binding.status.visibility = if (data.userGroup == args.groupId) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

    }

    private fun setAdapter() {
        binding.testsRv.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}