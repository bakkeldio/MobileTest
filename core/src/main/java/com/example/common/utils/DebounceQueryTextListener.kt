package com.example.common.utils

import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.*

class DebounceQueryTextListener(
    private val onDebouncingQueryTextChange: (String) -> Unit
) : SearchView.OnQueryTextListener {
    var debouncePeriod: Long = 500

    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var job: Job? = null
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        job?.cancel()
        job = coroutineScope.launch {
            newText?.let {
                delay(debouncePeriod)
                onDebouncingQueryTextChange(it)
            }
        }
        return false
    }
}