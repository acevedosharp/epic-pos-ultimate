package com.acevedosharp.controllers

import com.acevedosharp.CustomApplicationContextWrapper
import com.acevedosharp.validation_helpers.CustomUniqueValueConstraintViolationException
import com.acevedosharp.entities.ProductoDB
import javafx.collections.FXCollections
import com.acevedosharp.models.Producto
import com.acevedosharp.persistence_access.repositories_services.ProductoService
import tornadofx.Controller

class ProductoController : Controller() {

    private val productoService =
        find<CustomApplicationContextWrapper>().context.getBean<ProductoService>(ProductoService::class.java)

    val productos = FXCollections.observableArrayList<Producto>(
        productoService.all().map {
            Producto(it.productoId, it.codigo, it.descripcionLarga, it.descripcionCorta, it.precioVenta, it.existencias)
        }
    )

    @Throws(CustomUniqueValueConstraintViolationException::class)
    fun add(producto: Producto) {
        productoService.add(
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
        productos.add(producto)
    }

    @Throws(CustomUniqueValueConstraintViolationException::class)
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
}