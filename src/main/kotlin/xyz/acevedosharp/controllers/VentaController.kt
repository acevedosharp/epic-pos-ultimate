package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.entities.ItemVentaDB
import xyz.acevedosharp.entities.VentaDB
import xyz.acevedosharp.persistence_layer.repository_services.*
import xyz.acevedosharp.ui_models.UncommittedItemVenta
import xyz.acevedosharp.ui_models.Venta
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import java.sql.Timestamp

class VentaController: Controller(), UpdateSnapshot {
    private val ventaService = find<CustomApplicationContextWrapper>().context.getBean(VentaService::class.java)
    private val itemVentaService = find<CustomApplicationContextWrapper>().context.getBean(ItemVentaService::class.java)
    private val productoService = find<CustomApplicationContextWrapper>().context.getBean(ProductoService::class.java)
    private val empleadoService = find<CustomApplicationContextWrapper>().context.getBean(EmpleadoService::class.java)
    private val clienteService = find<CustomApplicationContextWrapper>().context.getBean(ClienteService::class.java)

    private val empleadoController = find<EmpleadoController>()
    private val clienteController = find<ClienteController>()
    private val productoController = find<ProductoController>()

    val ventas: ObservableList<Venta> = FXCollections.observableArrayList()

    init {
        updateSnapshot()
    }

    fun add(venta: Venta, items: List<UncommittedItemVenta>): VentaDB {
        val preRes = ventaService.add(
            VentaDB(
                null,
                Timestamp.valueOf(venta.fechaHora),
                venta.precioTotal,
                venta.pagoRecibido,
                empleadoService.repo.findByIdOrNull(venta.empleado.id),
                clienteService.repo.findByIdOrNull(venta.cliente.id),
                setOf<ItemVentaDB>()
            )
        )

        updateSnapshot()

        val iRes = itemVentaService.addAll(items.map {
            ItemVentaDB(
                null,
                it.cantidad,
                it.producto.precioVenta,
                productoService.repo.findByIdOrNull(it.producto.id),
                preRes
            )
        })

        iRes.forEach { el ->
            productoController.productos.find { it.id == el.producto.productoId }!!.apply { existencias -= el.cantidad }
        }

        preRes.items = iRes.toSet()

        return preRes
    }

    override fun updateSnapshot() {
        ventas.setAll(
            ventaService.all().map { dbObject: VentaDB ->
                Venta(
                    dbObject.ventaId,
                    dbObject.fechaHora.toLocalDateTime(),
                    dbObject.precioTotal,
                    dbObject.pagoRecibido,
                    empleadoController.empleados.first { it.id == dbObject.empleado.empleadoId },
                    clienteController.clientes.first { it.id == dbObject.cliente.clienteId }
                )
            }
        )
    }
}
