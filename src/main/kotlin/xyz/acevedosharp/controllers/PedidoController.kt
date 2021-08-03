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
import xyz.acevedosharp.persistence.entities.ProductoDB
import xyz.acevedosharp.persistence.repositories.*
import xyz.acevedosharp.ui_models.Notification
import xyz.acevedosharp.ui_models.NotificationType
import java.sql.Timestamp
import java.util.*

open class PedidoController : Controller(), UpdateSnapshot {

    private val pedidoRepo = find<CustomApplicationContextWrapper>().context.getBean(PedidoRepo::class.java)
    private val loteRepo = find<CustomApplicationContextWrapper>().context.getBean(LoteRepo::class.java)
    private val productoController = find<ProductoController>()
    private val notificationsController = find<NotificationsController>()

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

        val priceChangeNotifications = mutableListOf<Notification>()

        // update prices and existencias of producto
        val productosWithNewPriceAndQuantity = lotesPersist.map { currentLote ->
            val producto = ProductoDB(
                currentLote.producto.productoId,
                currentLote.producto.codigo,
                currentLote.producto.descripcionLarga,
                currentLote.producto.descripcionCorta,
                currentLote.producto.existencias,
                currentLote.producto.precioVenta,
                currentLote.producto.precioCompraEfectivo,
                currentLote.producto.margen,
                currentLote.producto.familia,
                currentLote.producto.alertaExistencias,
                currentLote.producto.iva,
                currentLote.producto.precioCompra
            )

            producto.existencias += currentLote.cantidad

            if (currentLote.precioCompra > producto.precioCompraEfectivo) {
                priceChangeNotifications.add(
                    Notification(
                        UUID.randomUUID(),
                        NotificationType.BUY_PRICE_INCREASED,
                        "P. de compra del último lote de ${producto.descripcionCorta} ha " +
                                "subido de $${producto.precioCompraEfectivo} a ${currentLote.precioCompra}"
                    )
                )
                producto.precioCompraEfectivo = currentLote.precioCompra
            }

            return@map producto
        }

        notificationsController.pushNotifications(priceChangeNotifications)

        productoController.saveAllIgnoreRoundingForPedido(productosWithNewPriceAndQuantity)
    }

    override fun updateSnapshot() {
        pedidos.setAll(pedidoRepo.findAll())
    }
}
