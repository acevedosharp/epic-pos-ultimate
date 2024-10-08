package xyz.acevedosharp.controllers

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.transaction.annotation.Transactional
import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.UncommittedItemVenta
import xyz.acevedosharp.ui_models.Venta
import tornadofx.Controller
import xyz.acevedosharp.GlobalHelper
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.VentaDB
import xyz.acevedosharp.persistence.repositories.*
import java.sql.Timestamp

open class VentaController : Controller(), UpdateSnapshot {
    private val notificationsController = find<NotificationsController>()

    private val ventaRepo = find<CustomApplicationContextWrapper>().context.getBean(VentaRepo::class.java)
    private val itemVentaRepo = find<CustomApplicationContextWrapper>().context.getBean(ItemVentaRepo::class.java)
    private val productoRepo = find<CustomApplicationContextWrapper>().context.getBean(ProductoRepo::class.java)

    private val ventas: ObservableList<VentaDB> = FXCollections.observableArrayList()

    fun getVentasFromTo(start: Timestamp, end: Timestamp) = ventaRepo.findAllByFechaHoraBetween(start, end)

    fun getVentasWithUpdate(): ObservableList<VentaDB> {
        updateSnapshot()
        return ventas
    }

    fun getVentasClean(): ObservableList<VentaDB> {
        return ventas
    }

    @Transactional
    open fun add(venta: Venta, items: List<UncommittedItemVenta>): VentaDB {
        val preRes = ventaRepo.save(
            VentaDB(
                null,
                Timestamp.valueOf(venta.fechaHora),
                venta.totalSinIva,
                venta.pagoRecibido,
                venta.empleado,
                venta.cliente,
                setOf(),
                venta.totalConIva
            )
        )

        updateSnapshot()

        val itemsVenta = itemVentaRepo.saveAll(items.map { uncommittedItemVenta ->

            val (_, ivaAmount, sellPrice) = GlobalHelper.calculateSellPriceBrokenDown(
                uncommittedItemVenta.producto.precioCompra,
                uncommittedItemVenta.producto.margen,
                uncommittedItemVenta.producto.iva
            )

            ItemVentaDB(
                null,
                Timestamp.valueOf(venta.fechaHora),
                uncommittedItemVenta.cantidad,
                uncommittedItemVenta.producto.precioVenta,
                uncommittedItemVenta.producto,
                preRes,
                preRes.cliente,
                sellPrice - ivaAmount,
                uncommittedItemVenta.producto.iva,
                uncommittedItemVenta.producto.margen
            )
        })

        val productosWithNewExistencias = itemsVenta.map { currentItemVenta ->
            val producto = currentItemVenta.producto

            producto.existencias -= currentItemVenta.cantidad

            return@map producto
        }

        productoRepo.saveAll(productosWithNewExistencias)

        itemsVenta.forEach {
            notificationsController.doInventoryCheck(it.producto)
        }

        // return saved VentaDB for printing
        return preRes.apply {
            this.items = itemsVenta.toSet()
        }
    }

    override fun updateSnapshot() {
        ventas.setAll(ventaRepo.findAll())
    }
}
