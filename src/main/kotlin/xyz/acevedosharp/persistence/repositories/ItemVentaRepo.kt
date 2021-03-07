package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.ProductoDB
import java.sql.Timestamp

@Repository
interface ItemVentaRepo: JpaRepository<ItemVentaDB, Int> {
    // oldest item venta
    fun findFirstByProductoEqualsOrderByItemVentaIdAsc(producto: ProductoDB): ItemVentaDB?

    // newest item venta
    fun findFirstByProductoEqualsOrderByItemVentaIdDesc(producto: ProductoDB): ItemVentaDB?

    fun findAllByProductoEqualsAndFechaHoraBetween(productoDB: ProductoDB, start: Timestamp, end: Timestamp): List<ItemVentaDB>

    fun findAllByProductoEqualsAndFechaHoraBetweenAndClienteEquals(productoDB: ProductoDB, start: Timestamp, end: Timestamp, cliente: ClienteDB): List<ItemVentaDB>
}
