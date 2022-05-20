package com.edu.mobiletestadmin.presentation.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.edu.mobiletestadmin.databinding.ItemViewPagerUserBinding
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import com.edu.mobiletestadmin.utils.addDividerItemDecoration

class UsersPagerAdapter(
    private val studentsAdapter: UsersAdapter,
    private val teachersAdapter: UsersAdapter,
    private val selectionListener: SelectionListener? = null,
    private val bundle: Bundle? = null,
    private val noTeachersMessage: Int,
    private val noStudentsMessage: Int

) : RecyclerView.Adapter<UsersPagerAdapter.UserPageVH>() {

    companion object {
        const val PAGES_COUNT = 2
        const val STUDENTS = 0
        const val TEACHERS = 1
    }


    private var studentsTracker: SelectionTracker<String>? = null
    private var teachersTracker: SelectionTracker<String>? = null

    inner class UserPageVH(private val binding: ItemViewPagerUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            position: Int
        ) {

            binding.viewPagerRv.adapter = when (position) {
                STUDENTS -> studentsAdapter
                TEACHERS -> teachersAdapter
                else -> null
            }
            binding.viewPagerRv.addDividerItemDecoration()

            when (position) {
                STUDENTS -> {
                    studentsTracker = buildSelectionTracker(binding, position, studentsAdapter)
                    studentsAdapter.tracker = studentsTracker
                }
                TEACHERS -> {
                    teachersTracker = buildSelectionTracker(binding, position, teachersAdapter)
                    teachersAdapter.tracker = teachersTracker
                }
            }

            studentsTracker?.onRestoreInstanceState(bundle)
            teachersTracker?.onRestoreInstanceState(bundle)

            studentsTracker?.addObserver(object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    studentsTracker?.let {
                        selectionListener?.getStudentsSelection(it.selection.toList())
                    }
                }
            })

            teachersTracker?.addObserver(object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    teachersTracker?.let {
                        selectionListener?.getTeachersSelection(it.selection.toList())
                    }
                }
            })


            if (tabEmptyState[position] == true) {
                reactToDataSetSize(
                    PageDataState.EMPTY,
                    if (position == STUDENTS) UserTypeEnum.STUDENT else UserTypeEnum.TEACHER
                )
            } else {
                reactToDataSetSize(
                    PageDataState.NOT_EMPTY,
                    if (position == STUDENTS) UserTypeEnum.STUDENT else UserTypeEnum.TEACHER
                )
            }
        }

        fun reactToDataSetSize(
            pageDataState: PageDataState,
            usersType: UserTypeEnum
        ) {
            when (pageDataState) {
                PageDataState.EMPTY -> {
                    binding.message.isVisible = true
                    if (usersType == UserTypeEnum.STUDENT) {
                        binding.message.text =
                            binding.root.resources.getString(noStudentsMessage)
                    } else {
                        binding.message.text =
                            binding.root.resources.getString(noTeachersMessage)
                    }
                }
                PageDataState.NOT_EMPTY -> {
                    binding.message.isVisible = false
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPageVH {

        val viewHolder = UserPageVH(
            ItemViewPagerUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holderMap[viewType] = viewHolder
        return viewHolder
    }

    override fun onBindViewHolder(holder: UserPageVH, position: Int) {
        holder.bind(position)
    }


    private val holderMap: HashMap<Int, UserPageVH> = hashMapOf()


    private val tabEmptyState = hashMapOf(STUDENTS to false, TEACHERS to false)

    fun updateEmptyState(dataState: PageDataState, usersType: UserTypeEnum) {
        tabEmptyState[UserTypeEnum.getPositionByType(usersType)] = dataState == PageDataState.EMPTY
        holderMap[UserTypeEnum.getPositionByType(usersType)]?.reactToDataSetSize(
            dataState,
            usersType
        )
    }

    fun getStudentSelection(): List<String> {
        return studentsTracker?.selection?.toList() ?: emptyList()
    }

    fun getTeachersSelection(): List<String> {
        return teachersTracker?.selection?.toList() ?: emptyList()
    }

    fun buildSelectionTracker(
        binding: ItemViewPagerUserBinding,
        position: Int,
        adapter: UsersAdapter
    ) = SelectionTracker.Builder(
        //1
        "selectionItem$position",
        //2
        binding.viewPagerRv,
        //3
        ItemsKeyProvider(
            adapter
        ),
        ItemsDetailsLookup(binding.viewPagerRv),
        //4
        StorageStrategy.createStringStorage()
    ).withSelectionPredicate(
        //5
        SelectionPredicates.createSelectAnything()
    ).build()

    fun saveInstanceState(outState: Bundle) {
        studentsTracker?.onSaveInstanceState(outState)
        teachersTracker?.onSaveInstanceState(outState)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> STUDENTS
            else -> TEACHERS
        }
    }

    override fun getItemCount() = PAGES_COUNT

    enum class PageDataState {
        EMPTY,
        NOT_EMPTY
    }

    interface SelectionListener {
        fun getStudentsSelection(selectionList: List<String>)
        fun getTeachersSelection(selectionList: List<String>)
    }
}