package com.acevedosharp.controllers

import com.acevedosharp.CustomApplicationContextWrapper
import com.acevedosharp.entities.LoteDB
import com.acevedosharp.entities.PedidoDB
import com.acevedosharp.persistence_layer.repository_services.*
import com.acevedosharp.ui_models.Lote
import com.acevedosharp.ui_models.Pedido
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import java.sql.Timestamp

class PedidoController: Controller() {

    private val pedidoService =
        find<CustomApplicationContextWrapper>().context.getBean<PedidoService>(PedidoService::class.java)

    private val loteService =
        find<CustomApplicationContextWrapper>().context.getBean<LoteService>(LoteService::class.java)

    private val proveedorService =
        find<CustomApplicationContextWrapper>().context.getBean<ProveedorService>(ProveedorService::class.java)

    private val empleadoService =
        find<CustomApplicationContextWrapper>().context.getBean<EmpleadoService>(EmpleadoService::class.java)

    private val productoService =
        find<CustomApplicationContextWrapper>().context.getBean<ProductoService>(ProductoService::class.java)

    private val proveedorController = find<ProveedorController>()
    private val empleadoController = find<EmpleadoController>()
    private val productoController = find<ProductoController>()

    val pedidos: ObservableList<Pedido> = FXCollections.observableArrayList<Pedido>(
        pedidoService.all().map { dbObject: PedidoDB ->
            Pedido(
                dbObject.pedidoId,
                dbObject.fechaHora.toLocalDateTime(),
                proveedorController.proveedores.first { it.id == dbObject.proveedor.proveedorId },
                empleadoController.empleados.first { it.id == dbObject.empleado.empleadoId }
            )
        }
    )

    fun add(pedido: Pedido, lotes: List<Lote>) {
        val preRes = pedidoService.add(
            PedidoDB(
                null,
                Timestamp.valueOf(pedido.fechaHora),
                proveedorService.repo.findByIdOrNull(pedido.proveedor.id),
                empleadoService.repo.findByIdOrNull(pedido.empleado.id),
                setOf<LoteDB>()
            )
        )

        pedidos.add(pedido.apply { id = preRes.pedidoId })


        val iRes = loteService.addAll(lotes.map {
            LoteDB(
                null,
                it.cantidad,
                it.precioCompra,
                productoService.repo.findByIdOrNull(it.producto.id),
                preRes
            )
        })

        iRes.forEach { el ->
            productoController.productos.find { it.id == el.producto.productoId }!!.apply { existencias += el.cantidad }
        }
    }

}