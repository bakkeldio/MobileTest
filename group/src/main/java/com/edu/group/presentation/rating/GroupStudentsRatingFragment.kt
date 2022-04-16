package com.edu.group.presentation.rating

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.edu.common.presentation.BaseFragment
import com.edu.common.utils.addItemDecorationWithoutLastItem
import com.edu.group.R
import com.edu.group.databinding.FragmentGroupStudentsRatingBinding
import com.edu.group.presentation.adapter.GroupStudentsRatingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupStudentsRatingFragment :
    BaseFragment<GroupStudentsRatingViewModel, FragmentGroupStudentsRatingBinding>(
        R.layout.fragment_group_students_rating,
        FragmentGroupStudentsRatingBinding::bind
    ) {

    override val viewModel: GroupStudentsRatingViewModel by viewModels()

    private val adapter: GroupStudentsRatingAdapter by lazy {
        GroupStudentsRatingAdapter()
    }

    private val args by navArgs<GroupStudentsRatingFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getStudentsWithRating(args.groupId)
    }

    override fun setupUI() {
        binding.toolbar.setupWithNavController(findNavController())
        binding.studentsRatingRV.adapter = adapter
        binding.studentsRatingRV.addItemDecorationWithoutLastItem()
    }

    override fun setupVM() {
        super.setupVM()
        viewModel.studentsWithRating.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun progressLoader(show: Boolean) {
        binding.progress.root.isVisible = show
    }


}