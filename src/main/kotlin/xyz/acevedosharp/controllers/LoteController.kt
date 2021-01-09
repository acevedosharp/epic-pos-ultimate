package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.persistence_layer.repository_services.LoteService
import xyz.acevedosharp.persistence_layer.repository_services.ProductoService
import xyz.acevedosharp.ui_models.Lote
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import xyz.acevedosharp.persistence_layer.entities.LoteDB

class LoteController : Controller() {
    private val loteService = find<CustomApplicationContextWrapper>().context.getBean(LoteService::class.java)
    private val productoService = find<CustomApplicationContextWrapper>().context.getBean(ProductoService::class.java)
    private val productoController = find<ProductoController>()

    fun lotesFromProducto(id: Int): List<Lote> = loteService.repo.findAllByProductoEquals(productoService.repo.findByIdOrNull(id)!!).map { dbObject: LoteDB ->
            Lote(
                dbObject.loteId,
                dbObject.cantidad,
                dbObject.precioCompra,
                productoController.productos.first { it.id == dbObject.producto.productoId }
            )
        }

    val lotes: ObservableList<Lote> = FXCollections.observableArrayList(
        loteService.all().map { dbObject: LoteDB ->
            Lote(
                dbObject.loteId,
                dbObject.cantidad,
                dbObject.precioCompra,
                productoController.productos.first { it.id == dbObject.producto.productoId }
            )
        }
    )
}
