package com.edu.test.presentation.createTest

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.edu.common.utils.showToast
import com.edu.test.R
import com.edu.test.data.datamanager.TestCreationHandler
import com.edu.test.databinding.FragmentChooseDateBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class ChooseDateTimeBottomSheet(private val listener: (BottomSheetDialogFragment) -> Unit) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentChooseDateBinding? = null
    private val binding: FragmentChooseDateBinding get() = _binding!!

    companion object {
        const val dateFormat = "yyyy-MM-dd"
    }

    private val calendar = Calendar.getInstance(Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dateTest.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(resources.getString(R.string.choose_the_date_and_time_for_test))
                .setCalendarConstraints(
                    CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
                        .build()
                )
                .build()
            datePicker.addOnPositiveButtonClickListener {
                val simpleFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
                calendar.time = Date(it)
                binding.dateTest.setText(simpleFormat.format(calendar.time))
            }
            datePicker.show(parentFragmentManager, "")
        }
        binding.timeTest.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                calendar[Calendar.HOUR_OF_DAY] = timePicker.hour
                calendar[Calendar.MINUTE] = timePicker.minute
                binding.timeTest.setText("${timePicker.hour} : ${timePicker.minute}")
            }
            timePicker.show(parentFragmentManager, "")
        }

        binding.testNameEditText.addTextChangedListener {
            it?.let {
                binding.testNameInputLayout.error = null
            }
        }

        binding.minusBtn.setOnClickListener {
            binding.time.setText((binding.time.text.toString().toInt() - 1).toString())
        }

        binding.plusBtn.setOnClickListener {
            binding.time.setText((binding.time.text.toString().toInt() + 1).toString())
        }
        binding.uploadTestBtn.setOnClickListener {
            if (binding.testNameEditText.text.isNullOrEmpty()) {
                binding.testNameInputLayout.error = resources.getString(R.string.enter_test_name)
            }
            if (binding.dateTest.text.isNullOrEmpty() || binding.timeTest.text.isNullOrEmpty()) {
                showToast(resources.getString(R.string.date_and_time_are_required))
                return@setOnClickListener
            }
            if (binding.time.text.isNullOrEmpty()) {
                showToast(resources.getString(R.string.enter_duration_for_test))
                return@setOnClickListener
            }
            TestCreationHandler.saveNewTest(
                binding.testNameEditText.text.toString(),
                calendar.time,
                binding.time.text.toString().toInt()
            )
            listener.invoke(
                this
            )
        }
    }
}