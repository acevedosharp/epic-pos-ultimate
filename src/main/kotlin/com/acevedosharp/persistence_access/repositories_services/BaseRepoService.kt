package com.acevedosharp.persistence_access.repositories_services

interface BaseRepoService<T> {
    fun all(): List<T>
    fun add(item: T): T
    fun edit(item: T): T
}