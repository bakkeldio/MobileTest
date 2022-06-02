package com.edu.test.presentation.createTest

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.edu.common.presentation.BaseFragment
import com.edu.common.utils.getDateString
import com.edu.common.utils.getHourMinute
import com.edu.common.utils.showToast
import com.edu.test.R
import com.edu.test.databinding.FragmentCreateTestBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateTestFragment : BaseFragment<CreateTestViewModel, FragmentCreateTestBinding>(
    R.layout.fragment_create_test,
    FragmentCreateTestBinding::bind
) {

    override val viewModel by hiltNavGraphViewModels<CreateTestViewModel>(R.id.navigation_test)

    private val calendar = Calendar.getInstance(Locale.getDefault())

    private val args by navArgs<CreateTestFragmentArgs>()

    private var testId: String? = null

    private var groupUid: String? = null

    private val fields = hashMapOf<Int, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testId = args.testId
        groupUid = args.groupUid
    }

    override fun setupUI() {

        testId?.let { viewModel.getTestInfo(it) }

        if (args.testId == null) {
            binding.createTest.isVisible = true
        }

        binding.toolbar.setupWithNavController(findNavController())

        binding.toolbar.setOnMenuItemClickListener { menu ->
            if (menu.itemId == R.id.remove_item) {
                viewModel.deleteTestFromDb()
                findNavController().popBackStack()
                true
            } else {
                false
            }
        }
        binding.navigateToQuestionsBtn.setOnClickListener {
            findNavController().navigate(
                CreateTestFragmentDirections.fromCreateTestFragmentToCreateQuestionsFragment(
                    args.groupUid,
                    testId!!
                )
            )
        }

        binding.createTest.setOnClickListener {
            if (fieldsValid()) {
                if (testId != null) {
                    viewModel.updateTestInDb(
                        testId!!, binding.testNameEditText.text.toString(),
                        calendar.time,
                        binding.time.text.toString().toInt(),
                        groupUid!!
                    )
                } else {
                    val uid = UUID.randomUUID().toString()
                    viewModel.saveTestInDb(
                        args.groupUid,
                        uid,
                        binding.testNameEditText.text.toString(),
                        calendar.time,
                        binding.time.text.toString().toInt()
                    )
                }
            }
        }

        binding.dateTest.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(resources.getString(R.string.choose_the_date_and_time_for_test))
                .setCalendarConstraints(
                    CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
                        .build()
                )
                .build()
            datePicker.addOnPositiveButtonClickListener {
                calendar.time = Date(it)
                binding.dateTest.setText(calendar.time.getDateString())
            }
            datePicker.show(parentFragmentManager, "")
        }
        binding.timeTest.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                calendar[Calendar.HOUR_OF_DAY] = timePicker.hour
                calendar[Calendar.MINUTE] = timePicker.minute
                binding.timeTest.setText(calendar.time.getHourMinute())
            }
            timePicker.show(parentFragmentManager, "")
        }


        viewModel.savedTest.observe(viewLifecycleOwner) { testDomain ->
            testDomain?.let {
                testId = it.uid
                groupUid = it.groupId
                calendar.time = it.date
                binding.createTest.text = resources.getString(R.string.update)
                binding.navigateToQuestionsBtn.isVisible = true
                binding.testNameEditText.setText(it.name)
                binding.timeTest.setText(it.date.getHourMinute())
                binding.dateTest.setText(it.date.getDateString())
                binding.time.setText(it.duration.toString())
            }
        }

        attachListenerToFields()
    }

    private fun fieldsValid(): Boolean {
        var valid = true
        if (binding.testNameEditText.text.isNullOrEmpty()) {
            valid = false
            binding.testNameInputLayout.isErrorEnabled = true
            binding.testNameInputLayout.error = resources.getString(R.string.enter_test_name)
        }
        if (binding.dateTest.text.isNullOrEmpty()) {
            valid = false
            binding.dateTestOut.isErrorEnabled = true
            binding.dateTestOut.error = resources.getString(R.string.enter_date)
        }

        if (binding.timeTest.text.isNullOrEmpty()) {
            valid = false
            binding.timeTestOut.isErrorEnabled = true
            binding.timeTestOut.error = resources.getString(R.string.enter_time)
        }

        if (binding.time.text.isNullOrEmpty()) {
            valid = false
            showToast(resources.getString(R.string.enter_duration_for_test))
        }
        return valid
    }


    private fun attachListenerToFields() {

        binding.timeTest.doAfterTextChanged {
            it?.toString()?.let { time ->
                val hourMinute = viewModel.savedTest.value?.date?.getHourMinute()
                fields[binding.timeTest.id] = hourMinute == time
                binding.navigateToQuestionsBtn.isVisible = fieldsValuesHaveNotBeenChanged()
                binding.createTest.isVisible = !binding.navigateToQuestionsBtn.isVisible
                binding.timeTestOut.error = null
                binding.timeTestOut.isErrorEnabled = false
            }
        }

        binding.testNameEditText.doAfterTextChanged { testTitle ->
            testTitle?.toString()?.let { name ->
                val testName = viewModel.savedTest.value?.name
                fields[binding.testNameEditText.id] = testName == name
                binding.navigateToQuestionsBtn.isVisible = fieldsValuesHaveNotBeenChanged()
                binding.createTest.isVisible = !binding.navigateToQuestionsBtn.isVisible
                binding.testNameInputLayout.error = null
                binding.testNameInputLayout.isErrorEnabled = false
            }
        }

        binding.time.doAfterTextChanged { duration ->
            duration?.toString()?.toIntOrNull()?.let { time ->
                val testDuration = viewModel.savedTest.value?.duration
                fields[binding.time.id] = testDuration == time
                binding.navigateToQuestionsBtn.isVisible = fieldsValuesHaveNotBeenChanged()
                binding.createTest.isVisible = !binding.navigateToQuestionsBtn.isVisible
            }

        }
        binding.dateTest.doAfterTextChanged {
            it?.toString()?.let { date ->
                val dateString = viewModel.savedTest.value?.date?.getDateString()
                fields[binding.dateTest.id] = dateString == date
                binding.navigateToQuestionsBtn.isVisible = fieldsValuesHaveNotBeenChanged()
                binding.createTest.isVisible = !binding.navigateToQuestionsBtn.isVisible
                binding.dateTestOut.error = null
                binding.dateTestOut.isErrorEnabled = false
            }
        }
    }

    private fun fieldsValuesHaveNotBeenChanged(): Boolean {
        var nothingChanged = true
        fields.map {
            nothingChanged = nothingChanged && it.value
        }
        return nothingChanged
    }
}