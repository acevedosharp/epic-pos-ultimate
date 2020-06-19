package com.acevedosharp.persistence_layer.repositories

import com.acevedosharp.entities.ProductoDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductoRepo: JpaRepository<ProductoDB, Int> {
    fun findByCodigo(string: String): ProductoDB
    fun findByDescripcionLarga(string: String): ProductoDB
    fun findByDescripcionCorta(string: String): ProductoDB

    fun existsByCodigo(string: String): Boolean
    fun existsByDescripcionLarga(string: String): Boolean
    fun existsByDescripcionCorta(string: String): Boolean
}