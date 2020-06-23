package com.acevedosharp.persistence_layer.repository_services.interfaces

interface BatchSaving<T> {
    fun addAll(items: List<T>): List<T>
}