package com.edu.common.data.mapper

interface Mapper {

    interface ToDomainMapper<in E, out D> {
        fun mapToDomain(model: E, uid: String = ""): D
    }

    interface FromDomainMapper<in E, out D> {
        fun mapFromDomain(model: E): D
    }
}