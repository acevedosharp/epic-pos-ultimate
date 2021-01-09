package xyz.acevedosharp.persistence_layer.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence_layer.entities.ItemVentaDB
import xyz.acevedosharp.persistence_layer.entities.ProductoDB
import java.sql.Timestamp

@Repository
interface ItemVentaRepo: JpaRepository<ItemVentaDB, Int> {
    fun findAllByProductoEqualsAndFechaHoraBetween(productoDB: ProductoDB, start: Timestamp, end: Timestamp): List<ItemVentaDB>
}
