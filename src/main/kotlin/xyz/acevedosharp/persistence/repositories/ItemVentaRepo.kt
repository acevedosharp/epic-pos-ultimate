package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.ProductoDB
import java.sql.Timestamp

@Repository
interface ItemVentaRepo: JpaRepository<ItemVentaDB, Int> {
    // oldest item venta
    fun findFirstByProductoEqualsOrderByItemVentaIdAsc(producto: ProductoDB): ItemVentaDB?

    // newest item venta
    fun findFirstByProductoEqualsOrderByItemVentaIdDesc(producto: ProductoDB): ItemVentaDB?

    fun findAllByProductoAndFechaHoraAfter(producto: ProductoDB, after: Timestamp): List<ItemVentaDB>

    fun findAllByProductoEqualsAndFechaHoraBetween(productoDB: ProductoDB, start: Timestamp, end: Timestamp): List<ItemVentaDB>

    @Query(nativeQuery = true, value = """
        select iv.producto as product, sum(iv.cantidad) as quantity, sum(iv.precio_venta_sin_iva * iv.cantidad) as salePriceWithoutTax, sum(iv.precio_venta_con_iva * iv.cantidad) as salePriceWithTax from item_venta as iv
        where iv.fecha_hora between ?1 and ?2
        group by iv.producto
    """)
    fun findAllAggregatedByProductoAndFechaHoraBetween(start: Timestamp, end: Timestamp): List<IAggregatedSaleItem>

    interface IAggregatedSaleItem {
        fun getProduct(): Int
        fun getQuantity(): Int
        fun getSalePriceWithoutTax(): Double
        fun getSalePriceWithTax(): Double
    }
}
