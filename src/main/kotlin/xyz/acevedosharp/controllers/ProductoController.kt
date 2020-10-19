package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.entities.ProductoDB
import xyz.acevedosharp.persistence_layer.repository_services.FamiliaService
import javafx.collections.FXCollections
import xyz.acevedosharp.ui_models.Producto
import xyz.acevedosharp.persistence_layer.repository_services.ProductoService
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller

class ProductoController : Controller() {

    private val productoService =
        find<CustomApplicationContextWrapper>().context.getBean<ProductoService>(ProductoService::class.java)

    private val familiaService =
        find<CustomApplicationContextWrapper>().context.getBean<FamiliaService>(FamiliaService::class.java)

    private val familiaController = find<FamiliaController>()

    val productos: ObservableList<Producto> = FXCollections.observableArrayList<Producto>(
        productoService.all().map { dbObject: ProductoDB ->
            Producto(
                dbObject.productoId,
                dbObject.codigo,
                dbObject.descripcionLarga,
                dbObject.descripcionCorta,
                dbObject.precioVenta,
                dbObject.existencias,
                if (dbObject.familia == null) null else familiaController.familias.firstOrNull { it.id == dbObject.familia.familiaId }
            )
        }
    )

    fun add(producto: Producto) {
        val res = productoService.add(
            ProductoDB(
                null,
                producto.codigo,
                producto.descLarga,
                producto.descCorta,
                producto.existencias,
                producto.precioVenta,
                if (producto.familia == null) null else familiaService.repo.findByIdOrNull(producto.familia.id)
            )
        )
        productos.add(producto.apply { id = res.productoId })
    }

    fun edit(producto: Producto) {
        val res = productoService.edit(
            ProductoDB(
                producto.id,
                producto.codigo,
                producto.descLarga,
                producto.descCorta,
                producto.existencias,
                producto.precioVenta,
                familiaService.repo.findById(producto.familia.id!!).get()
            )
        )

        producto.apply {
            codigo = res.codigo
            descLarga = res.descripcionLarga
            descCorta = res.descripcionCorta
            existencias = res.existencias
            precioVenta = res.precioVenta
        }
    }


    fun isCodigoAvailable(codigo: String): Boolean = productoService.repo.existsByCodigo(codigo)
    fun existsOtherWithCodigo(codigo: String, id: Int): Boolean {
        return productoService.repo.existsByCodigo(codigo) && (productoService.repo.findByCodigo(codigo).productoId != id)
    }

    fun isDescLargaAvailable(descLarga: String): Boolean = productoService.repo.existsByDescripcionLarga(descLarga)
    fun existsOtherWithDescLarga(descLarga: String, id: Int): Boolean {
        return productoService.repo.existsByDescripcionLarga(descLarga) && (productoService.repo.findByDescripcionLarga(descLarga).productoId != id)
    }

    fun isDescCortaAvailable(descCorta: String): Boolean = productoService.repo.existsByDescripcionCorta(descCorta)
    fun existsOtherWithDescCorta(descCorta: String, id: Int): Boolean {
        return productoService.repo.existsByDescripcionCorta(descCorta) && (productoService.repo.findByDescripcionCorta(descCorta).productoId != id)
    }
}