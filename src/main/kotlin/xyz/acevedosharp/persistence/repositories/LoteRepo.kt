package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.LoteDB
import xyz.acevedosharp.persistence.entities.ProductoDB

@Repository
interface LoteRepo : JpaRepository<LoteDB, Int> {
    fun findAllByProductoEquals(productoDB: ProductoDB): List<LoteDB>

    @Query(
        value = "select * from epic.lote where producto = :prod order by precio_compra desc limit 1",
        nativeQuery = true
    )
    fun findMostExpensiveLoteOfProducto(@Param("prod") productoDB: ProductoDB): LoteDB?
}
