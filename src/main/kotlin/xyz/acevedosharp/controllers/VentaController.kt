package xyz.acevedosharp.controllers

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.UncommittedItemVenta
import xyz.acevedosharp.ui_models.Venta
import tornadofx.Controller
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.VentaDB
import xyz.acevedosharp.persistence.repositories.*
import java.sql.Timestamp

class VentaController: Controller(), UpdateSnapshot {
    private val empleadoRepo = find<CustomApplicationContextWrapper>().context.getBean(EmpleadoRepo::class.java)
    private val clienteRepo = find<CustomApplicationContextWrapper>().context.getBean(ClienteRepo::class.java)
    private val ventaRepo = find<CustomApplicationContextWrapper>().context.getBean(VentaRepo::class.java)
    private val productoRepo = find<CustomApplicationContextWrapper>().context.getBean(ProductoRepo::class.java)
    private val itemVentaRepo = find<CustomApplicationContextWrapper>().context.getBean(ItemVentaRepo::class.java)

    val ventas: ObservableList<VentaDB> = FXCollections.observableArrayList()

    //init {
    //    updateSnapshot()
    //}

    fun add(venta: Venta, items: List<UncommittedItemVenta>){
        val preRes = ventaRepo.save(
            VentaDB(
                null,
                Timestamp.valueOf(venta.fechaHora),
                venta.precioTotal,
                venta.pagoRecibido,
                empleadoRepo.findByIdOrNull(venta.empleado.id)!!,
                clienteRepo.findByIdOrNull(venta.cliente.id)!!,
                setOf()
            )
        )

        updateSnapshot()

        val productosSnapshot = productoRepo.findAll()

        itemVentaRepo.saveAll(items.map { uncommittedItemVenta ->
            ItemVentaDB(
                null,
                Timestamp.valueOf(venta.fechaHora),
                uncommittedItemVenta.cantidad,
                uncommittedItemVenta.producto.precioVenta,
                productosSnapshot.find { it.productoId == uncommittedItemVenta.producto.id }!!,
                preRes
            )
        })
    }

    override fun updateSnapshot() {
        ventas.setAll(ventaRepo.findAll())
    }
}
