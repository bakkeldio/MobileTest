package com.example.common.domain

interface ApiMapper<E, D> {

    fun mapToDomain(apiEntity: E): D
}