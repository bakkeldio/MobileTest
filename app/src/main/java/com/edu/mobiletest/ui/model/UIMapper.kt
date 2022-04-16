package com.edu.mobiletest.ui.model

interface UIMapper<T, R> {
    fun mapDomainModelToUI(domainModel: T): R
}