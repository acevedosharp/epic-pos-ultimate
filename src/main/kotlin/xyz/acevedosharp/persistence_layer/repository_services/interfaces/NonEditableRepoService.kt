package xyz.acevedosharp.persistence_layer.repository_services.interfaces

interface NonEditableRepoService<T> {
    fun all(): List<T>
    fun add(item: T): T
}