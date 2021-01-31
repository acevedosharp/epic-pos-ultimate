package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.Lote
import xyz.acevedosharp.ui_models.Pedido
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import tornadofx.Controller
import xyz.acevedosharp.persistence.entities.LoteDB
import xyz.acevedosharp.persistence.entities.PedidoDB
import xyz.acevedosharp.persistence.repositories.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class PedidoController : Controller(), UpdateSnapshot {

    private val pedidoRepo = find<CustomApplicationContextWrapper>().context.getBean(PedidoRepo::class.java)
    private val loteRepo = find<CustomApplicationContextWrapper>().context.getBean(LoteRepo::class.java)
    private val productoRepo = find<CustomApplicationContextWrapper>().context.getBean(ProductoRepo::class.java)

    private val pedidos: ObservableList<PedidoDB> = FXCollections.observableArrayList()

    fun getPedidosWithUpdate(): ObservableList<PedidoDB> {
        updateSnapshot()
        return pedidos
    }

    fun getPedidosClean(): ObservableList<PedidoDB> {
        return pedidos
    }

    fun findById(id: Int) = pedidoRepo.findByIdOrNull(id)

    @Transactional
    open fun add(pedido: Pedido, lotes: List<Lote>) {
        val preRes = pedidoRepo.save(
            PedidoDB(
                null,
                Timestamp.valueOf(pedido.fechaHora),
                pedido.proveedor,
                pedido.empleado,
                setOf()
            )
        )

        updateSnapshot()

        val lotesPersist = loteRepo.saveAll(lotes.map { lote ->
            LoteDB(
                null,
                lote.cantidad,
                lote.precioCompra,
                lote.producto,
                preRes
            )
        })

        // update prices and existencias of producto
        val productosWithNewPrice = lotesPersist.map { currentLote ->
            val producto = currentLote.producto

            producto.existencias += currentLote.cantidad

            if (producto.precioCompraEfectivo == 0 || producto.precioCompraEfectivo < currentLote.precioCompra) {
                producto.precioCompraEfectivo = currentLote.precioCompra
            }

            // update sell price
            val rawSellPrice = producto.precioCompraEfectivo / (1 - (producto.margen/100))
            val roundedSellPrice = (rawSellPrice - 1) + (50 - ((rawSellPrice - 1) % 50)) // we subtract 1 so that we don't round from eg. 4000 -> 4050.
            producto.precioVenta = roundedSellPrice.toInt()

            return@map producto
        }

        productoRepo.saveAll(productosWithNewPrice)
    }

    override fun updateSnapshot() {
        pedidos.setAll(pedidoRepo.findAll())
    }
}
