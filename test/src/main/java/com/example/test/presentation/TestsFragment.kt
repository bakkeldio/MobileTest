package com.example.test.presentation

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.common.presentation.ResourceState
import com.example.common.utils.addItemDecorationWithoutLastItem
import com.example.test.databinding.FragmentTestsBinding
import com.example.common.domain.test.model.TestDomainModel
import com.example.common.utils.DebounceQueryTextListener
import com.example.test.presentation.adapter.TestsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
internal class TestsFragment : Fragment(), TestsAdapter.ItemClickListener {


    private val viewModel by viewModels<TestsViewModel>()
    private var _binding: FragmentTestsBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val testsAdapter by lazy {
        TestsAdapter(this)
    }

    private val args by navArgs<TestsFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllTests(args.groupId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTestsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.testsRv.apply {
            adapter = testsAdapter
            addItemDecorationWithoutLastItem()
        }
        binding.addTestBtn.isVisible = sharedPreferences.getBoolean("isUserAdmin", false)

        binding.searchView.setOnQueryTextListener(DebounceQueryTextListener{
            if (binding.searchView.hasFocus()) {
                viewModel.searchThroughTests(query = it, groupId = args.groupId)
            }
        })

        viewModel.apply {
            testsState.observe(viewLifecycleOwner, { state ->
                binding.progressBar.root.isVisible = state is ResourceState.Loading
                when(state) {
                    is ResourceState.Success -> {
                        testsAdapter.submitList(state.data)
                    }
                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    is ResourceState.Empty -> {
                    }
                    else -> Unit
                }
            })
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(model: TestDomainModel) {

    }
}