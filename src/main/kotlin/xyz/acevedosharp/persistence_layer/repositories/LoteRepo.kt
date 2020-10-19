package xyz.acevedosharp.persistence_layer.repositories

import xyz.acevedosharp.entities.LoteDB
import xyz.acevedosharp.entities.ProductoDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoteRepo: JpaRepository<LoteDB, Int> {

    fun findAllByProductoEquals(productoDB: ProductoDB): List<LoteDB>
}