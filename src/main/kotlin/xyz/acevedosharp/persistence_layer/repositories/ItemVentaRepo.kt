package xyz.acevedosharp.persistence_layer.repositories

import xyz.acevedosharp.entities.ItemVentaDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.entities.ProductoDB
import java.sql.Timestamp

@Repository
interface ItemVentaRepo: JpaRepository<ItemVentaDB, Int> {
    fun findAllByProductoEqualsAndFechaHoraBetween(productoDB: ProductoDB, start: Timestamp, end: Timestamp): List<ItemVentaDB>
}
