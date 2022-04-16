package com.edu.common.domain

interface ApiMapper<E, D> {

    fun mapToDomain(apiEntity: E, uid: String = ""): D
}