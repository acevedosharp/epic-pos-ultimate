package xyz.acevedosharp.persistence_layer.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence_layer.entities.LoteDB
import xyz.acevedosharp.persistence_layer.entities.ProductoDB

@Repository
interface LoteRepo: JpaRepository<LoteDB, Int> {

    fun findAllByProductoEquals(productoDB: ProductoDB): List<LoteDB>
}
