package com.acevedosharp.persistence_layer.repository_services

interface BaseRepoService<T> {
    fun all(): List<T>
    fun add(item: T): T
    fun edit(item: T): T
}