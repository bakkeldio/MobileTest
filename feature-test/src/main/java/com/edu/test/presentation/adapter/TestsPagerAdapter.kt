package com.edu.test.presentation.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.presentation.model.ItemForSelection
import com.edu.common.utils.DividerItemDecorator
import com.edu.test.databinding.ViewPagerItemTestBinding

class TestsPagerAdapter(
    private val allTests: TestsAdapter,
    private val passedTests: PassedTestsAdapter,
    private val unPublishedTestsAdapter: UnPublishedTestsAdapter,
    private val isUserAdmin: Boolean = false,
    private val listener: TestsAdapterListener,
    private val selectionListener: SelectionListener,
    private val bundle: Bundle? = null
) : RecyclerView.Adapter<TestsPagerAdapter.TestPageViewHolder>() {

    companion object {
        private const val FIRST = 0
        private const val SECOND = 1
        private const val PAGES_COUNT = 2
        private const val SELECTION_ID = "selectionId"
    }

    private val holderMap: HashMap<Int, TestPageViewHolder> = hashMapOf()

    private val tabEmptyState = hashMapOf(FIRST to false, SECOND to false)

    private var testsTracker: SelectionTracker<String>? = null
    private var unPublishedTestsTracker: SelectionTracker<String>? = null

    inner class TestPageViewHolder(val binding: ViewPagerItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            if (tabEmptyState[position] == true) {
                showEmptyDataState()
            } else {
                hideEmptyDataState()
            }
            binding.viewPagerRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (isUserAdmin) {
                        when (newState) {
                            RecyclerView.SCROLL_STATE_IDLE -> listener.showFloatingActionButton()
                            RecyclerView.SCROLL_STATE_DRAGGING -> listener.hideFloatingActionButton()
                        }
                    }
                }
            })
            binding.viewPagerRv.adapter = when (position) {
                FIRST -> {
                    allTests
                }
                SECOND -> {
                    if (isUserAdmin) {
                        unPublishedTestsAdapter
                    } else {
                        passedTests
                    }
                }
                else -> null
            }

            if (isUserAdmin) {
                when (position) {
                    FIRST -> {
                        testsTracker = buildSelectionTracker(binding, position)
                        allTests.tracker = testsTracker
                    }
                    SECOND -> {
                        if (isUserAdmin) {
                            unPublishedTestsTracker = buildSelectionTracker(binding, position)
                            unPublishedTestsAdapter.tracker = unPublishedTestsTracker
                        }
                    }
                }
            }

            testsTracker?.onRestoreInstanceState(bundle)
            unPublishedTestsTracker?.onRestoreInstanceState(bundle)

            testsTracker?.addObserver(object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    selectionListener.getTestsSelection(
                        testsTracker?.selection?.toList() ?: emptyList()
                    )
                }
            })

            unPublishedTestsTracker?.addObserver(object :
                SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    selectionListener.getUnPublishedTestsSelection(
                        unPublishedTestsTracker?.selection?.toList() ?: emptyList()
                    )
                }
            })

            binding.viewPagerRv.addItemDecoration(DividerItemDecorator(binding.root.context))
        }

        fun showEmptyDataState() {
            binding.groupNoData.visibility = View.VISIBLE
        }

        fun hideEmptyDataState() {
            binding.groupNoData.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestPageViewHolder {
        val viewHolder = TestPageViewHolder(
            ViewPagerItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holderMap[viewType] = viewHolder
        return viewHolder
    }

    override fun onBindViewHolder(holder: TestPageViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateEmptyState(position: Int, dataSize: Int) {
        tabEmptyState[position] = dataSize == 0
        if (dataSize == 0) {
            holderMap[position]?.showEmptyDataState()
        } else {
            holderMap[position]?.hideEmptyDataState()
        }
    }

    fun buildSelectionTracker(
        binding: ViewPagerItemTestBinding,
        position: Int
    ) = SelectionTracker.Builder(
        //1
        "$SELECTION_ID$position",
        //2
        binding.viewPagerRv,
        when (position) {
            FIRST -> allTests.getStringItemKeyProvider()
            else -> unPublishedTestsAdapter.getStringItemKeyProvider()
        },
        getItemDetailsLookUp(binding.viewPagerRv),
        //4
        StorageStrategy.createStringStorage()
    ).withSelectionPredicate(
        //5
        SelectionPredicates.createSelectAnything()
    ).build()


    fun saveSelectionState(outState: Bundle) {
        testsTracker?.onSaveInstanceState(outState)
        unPublishedTestsTracker?.onSaveInstanceState(outState)
    }

    fun getTestsSelection() = testsTracker?.selection?.map { it } ?: emptyList()

    fun getUnPublishedTestsSelection() =
        unPublishedTestsTracker?.selection?.map { it } ?: emptyList()

    override fun getItemCount(): Int {
        return PAGES_COUNT
    }

    interface TestsAdapterListener {
        fun showFloatingActionButton()
        fun hideFloatingActionButton()
    }

    interface SelectionListener {
        fun getTestsSelection(selectionList: List<String>)
        fun getUnPublishedTestsSelection(selectionList: List<String>)
    }

    private fun <T : ItemForSelection, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.getStringItemKeyProvider(): ItemKeyProvider<String> {
        return object : ItemKeyProvider<String>(SCOPE_CACHED) {
            override fun getKey(position: Int): String {
                return this@getStringItemKeyProvider.currentList[position].uid
            }

            override fun getPosition(key: String): Int {
                return this@getStringItemKeyProvider.currentList.indexOfFirst {
                    key == it.uid
                }
            }

        }
    }

    private fun getItemDetailsLookUp(recyclerView: RecyclerView): ItemDetailsLookup<String> {
        return object : ItemDetailsLookup<String>() {
            override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
                val view = recyclerView.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    return (recyclerView.getChildViewHolder(view) as BaseViewHolderForSelection).getStringDetails()
                }
                return null
            }

        }
    }
}