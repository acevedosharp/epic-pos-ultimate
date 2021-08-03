package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import javafx.collections.FXCollections
import xyz.acevedosharp.ui_models.Producto
import javafx.collections.ObservableList
import tornadofx.Controller
import org.springframework.data.repository.findByIdOrNull
import xyz.acevedosharp.persistence.entities.ProductoDB
import xyz.acevedosharp.persistence.repositories.ProductoRepo

class ProductoController(productoRepo: ProductoRepo? = null) : Controller(), UpdateSnapshot {

    private val productoRepo = productoRepo
        ?: find<CustomApplicationContextWrapper>().context.getBean(ProductoRepo::class.java)

    private val productos: ObservableList<ProductoDB> = FXCollections.observableArrayList()

    fun getProductosWithUpdate(): ObservableList<ProductoDB> {
        updateSnapshot()
        return productos
    }

    fun getProductosClean(): ObservableList<ProductoDB> {
        return productos
    }

    fun findById(id: Int) = productoRepo.findByIdOrNull(id)
    fun findByCodigo(barCode: String) = productoRepo.findByCodigo(barCode)

    fun save(producto: Producto) {
        productoRepo.save(
            ProductoDB(
                producto.id,
                producto.codigo,
                producto.descLarga,
                producto.descCorta,
                producto.existencias,
                producto.precioVenta,
                producto.precioCompraEfectivo,
                producto.margen,
                producto.familia,
                producto.alertaExistencias,
                producto.iva,
                producto.precioCompra
            )
        )
        updateSnapshot()
    }

    fun saveAllIgnoreRoundingForPedido(productos: List<ProductoDB>) {
        // rounding already taken care in PedidoController
        productoRepo.saveAll(productos)
        updateSnapshot()
    }

    fun isCodigoAvailable(codigo: String): Boolean {
        return productoRepo.existsByCodigo(codigo)
    }

    fun existsOtherWithCodigo(codigo: String, id: Int): Boolean {
        return productoRepo.existsByCodigo(codigo) && (productoRepo.findByCodigo(codigo).productoId != id)
    }

    fun isDescLargaAvailable(descLarga: String): Boolean {
        return productoRepo.existsByDescripcionLarga(descLarga)
    }

    fun existsOtherWithDescLarga(descLarga: String, id: Int): Boolean {
        return productoRepo.existsByDescripcionLarga(descLarga) && (productoRepo.findByDescripcionLarga(descLarga).productoId != id)
    }

    fun isDescCortaAvailable(descCorta: String): Boolean {
        return productoRepo.existsByDescripcionCorta(descCorta)
    }

    fun existsOtherWithDescCorta(descCorta: String, id: Int): Boolean {
        return productoRepo.existsByDescripcionCorta(descCorta) && (productoRepo.findByDescripcionCorta(descCorta).productoId != id)
    }

    override fun updateSnapshot() {
        productos.setAll(productoRepo.findAll())
    }
}
