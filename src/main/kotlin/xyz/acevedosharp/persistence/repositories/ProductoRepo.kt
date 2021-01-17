package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.ProductoDB

@Repository
interface ProductoRepo: JpaRepository<ProductoDB, Int> {
    fun findByCodigo(string: String): ProductoDB
    fun findByDescripcionLarga(string: String): ProductoDB
    fun findByDescripcionCorta(string: String): ProductoDB

    fun existsByCodigo(string: String): Boolean
    fun existsByDescripcionLarga(string: String): Boolean
    fun existsByDescripcionCorta(string: String): Boolean
}
