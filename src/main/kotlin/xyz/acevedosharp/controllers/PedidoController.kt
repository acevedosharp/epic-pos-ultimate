package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.entities.LoteDB
import xyz.acevedosharp.entities.PedidoDB
import xyz.acevedosharp.persistence_layer.repository_services.*
import xyz.acevedosharp.ui_models.Lote
import xyz.acevedosharp.ui_models.Pedido
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import java.sql.Timestamp

class PedidoController: Controller(), UpdateSnapshot {

    private val pedidoService = find<CustomApplicationContextWrapper>().context.getBean(PedidoService::class.java)
    private val loteService = find<CustomApplicationContextWrapper>().context.getBean(LoteService::class.java)
    private val proveedorService = find<CustomApplicationContextWrapper>().context.getBean(ProveedorService::class.java)
    private val empleadoService = find<CustomApplicationContextWrapper>().context.getBean(EmpleadoService::class.java)
    private val productoService = find<CustomApplicationContextWrapper>().context.getBean(ProductoService::class.java)

    private val proveedorController = find<ProveedorController>()
    private val empleadoController = find<EmpleadoController>()
    private val productoController = find<ProductoController>()

    val pedidos: ObservableList<Pedido> = FXCollections.observableArrayList()

    fun add(pedido: Pedido, lotes: List<Lote>) {
        println("Now saving Pedido...")
        val preRes = pedidoService.add(
            PedidoDB(
                null,
                Timestamp.valueOf(pedido.fechaHora),
                proveedorService.repo.findByIdOrNull(pedido.proveedor.id),
                empleadoService.repo.findByIdOrNull(pedido.empleado.id),
                setOf<LoteDB>()
            )
        )
        println("Successfully saved Pedido!")
        updateSnapshot()

        println("Now saving lotes...")
        val iRes = loteService.addAll(lotes.map {
            LoteDB(
                null,
                it.cantidad,
                it.precioCompra,
                productoService.repo.findByIdOrNull(it.producto.id),
                preRes
            )
        })
        println("Successfully saved Lotes!")

        println("Adding to existences...")
        iRes.forEach { el ->
            productoController.productos.find { it.id == el.producto.productoId }!!.apply { existencias += el.cantidad }
        }
        println("Added to existences!")
    }

    override fun updateSnapshot() {
        pedidos.setAll(
            pedidoService.all().map { dbObject: PedidoDB ->
                Pedido(
                    dbObject.pedidoId,
                    dbObject.fechaHora.toLocalDateTime(),
                    proveedorController.proveedores.first { it.id == dbObject.proveedor.proveedorId },
                    empleadoController.empleados.first { it.id == dbObject.empleado.empleadoId }
                )
            }
        )
    }

}
