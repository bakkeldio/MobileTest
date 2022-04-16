package com.edu.common.presentation

class Pagination<T>(
    val items: List<T> = emptyList(),
    val hasNextPage: Boolean = false
){
    companion object{
        const val DEFAULT_PAGE_SIZE = 10
    }
}