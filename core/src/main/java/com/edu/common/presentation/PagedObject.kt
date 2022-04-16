package com.edu.common.presentation

class PagedObject {
    var currentPage = 1
    var hasNextPage = true

    fun <T> updatePage(pagination: Pagination<T>){
        hasNextPage = pagination.hasNextPage
        if (!hasNextPage){
            return
        }
        currentPage++
    }

}