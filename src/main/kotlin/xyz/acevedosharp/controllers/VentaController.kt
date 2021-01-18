package xyz.acevedosharp.controllers

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.UncommittedItemVenta
import xyz.acevedosharp.ui_models.Venta
import tornadofx.Controller
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.VentaDB
import xyz.acevedosharp.persistence.repositories.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VentaController: Controller(), UpdateSnapshot {
    private val ventaRepo = find<CustomApplicationContextWrapper>().context.getBean(VentaRepo::class.java)
    private val itemVentaRepo = find<CustomApplicationContextWrapper>().context.getBean(ItemVentaRepo::class.java)

    private val ventas: ObservableList<VentaDB> = FXCollections.observableArrayList()

    fun getVentasWithUpdate(): ObservableList<VentaDB> {
        updateSnapshot()
        return ventas
    }

    fun getVentasClean(): ObservableList<VentaDB> {
        return ventas
    }

    fun add(venta: Venta, items: List<UncommittedItemVenta>){
        val preRes = ventaRepo.save(
            VentaDB(
                null,
                Timestamp.valueOf(venta.fechaHora),
                venta.precioTotal,
                venta.pagoRecibido,
                venta.empleado,
                venta.cliente,
                setOf()
            )
        )

        updateSnapshot()

        itemVentaRepo.saveAll(items.map { uncommittedItemVenta ->
            ItemVentaDB(
                null,
                Timestamp.valueOf(venta.fechaHora),
                uncommittedItemVenta.cantidad,
                uncommittedItemVenta.producto.precioVenta,
                uncommittedItemVenta.producto,
                preRes
            )
        })
    }

    override fun updateSnapshot() {
        println("Triggered update snapshot for Venta once at ${DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now())}")
        ventas.setAll(ventaRepo.findAll())
    }
}
