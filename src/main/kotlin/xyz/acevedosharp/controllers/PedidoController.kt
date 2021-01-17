package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.persistence_layer.repository_services.*
import xyz.acevedosharp.ui_models.Lote
import xyz.acevedosharp.ui_models.Pedido
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import xyz.acevedosharp.persistence_layer.entities.LoteDB
import xyz.acevedosharp.persistence_layer.entities.PedidoDB
import xyz.acevedosharp.persistence_layer.entities.ProductoDB
import java.sql.Timestamp

class PedidoController : Controller(), UpdateSnapshot {

    private val pedidoService = find<CustomApplicationContextWrapper>().context.getBean(PedidoService::class.java)
    private val loteService = find<CustomApplicationContextWrapper>().context.getBean(LoteService::class.java)
    private val proveedorService = find<CustomApplicationContextWrapper>().context.getBean(ProveedorService::class.java)
    private val empleadoService = find<CustomApplicationContextWrapper>().context.getBean(EmpleadoService::class.java)
    private val productoService = find<CustomApplicationContextWrapper>().context.getBean(ProductoService::class.java)

    private val proveedorController = find<ProveedorController>()
    private val empleadoController = find<EmpleadoController>()
    private val productoController = find<ProductoController>()

    val pedidos: ObservableList<Pedido> = FXCollections.observableArrayList()

    init {
        updateSnapshot()
    }

    fun add(pedido: Pedido, lotes: List<Lote>) {
        println("Now saving Pedido...")
        val preRes = pedidoService.add(
            PedidoDB(
                null,
                Timestamp.valueOf(pedido.fechaHora),
                proveedorService.repo.findByIdOrNull(pedido.proveedor.id)!!,
                empleadoService.repo.findByIdOrNull(pedido.empleado.id)!!,
                setOf()
            )
        )
        println("Successfully saved Pedido!")
        updateSnapshot()

        println("Now saving lotes...")
        val lotesPersist = loteService.addAll(lotes.map {
            LoteDB(
                null,
                it.cantidad,
                it.precioCompra,
                productoService.repo.findByIdOrNull(it.producto.id)!!,
                preRes
            )
        })

        lotesPersist.forEach { currentLote ->
            if (currentLote.producto.margen > 0.0) {
                val expensiveLote = loteService.findMostExpensiveLoteOfProducto(currentLote.producto)

                if (expensiveLote != null) {
                    val rawLoteSellPrice = currentLote.precioCompra / (1 - currentLote.producto.margen)
                    val roundedLoteSellPrice = rawLoteSellPrice + (50 - (rawLoteSellPrice % 50)) // round to upper 50
                    val originalSellPrice = currentLote.producto.precioVenta

                    if (roundedLoteSellPrice > originalSellPrice) {
                        val newProduct = currentLote.producto.apply {
                            precioVenta = roundedLoteSellPrice
                        }

                        println("The price of ${newProduct.descripcionCorta} has changed from $originalSellPrice to $$roundedLoteSellPrice")

                        productoService.add(newProduct)
                    }
                }
            }
        }
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

    fun findMostExpensiveLoteOfProducto(productoDB: ProductoDB) = loteService.findMostExpensiveLoteOfProducto(productoDB)

}
