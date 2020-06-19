package com.acevedosharp.controllers

import com.acevedosharp.CustomApplicationContextWrapper
import com.acevedosharp.entities.ProductoDB
import javafx.collections.FXCollections
import com.acevedosharp.ui_models.Producto
import com.acevedosharp.persistence_layer.repository_services.ProductoService
import tornadofx.Controller

class ProductoController : Controller() {

    private val productoService =
        find<CustomApplicationContextWrapper>().context.getBean<ProductoService>(ProductoService::class.java)

    val productos = FXCollections.observableArrayList<Producto>(
        productoService.all().map {
            Producto(it.productoId, it.codigo, it.descripcionLarga, it.descripcionCorta, it.precioVenta, it.existencias)
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
                null
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
                null
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