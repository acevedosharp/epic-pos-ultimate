package com.acevedosharp.controllers

import com.acevedosharp.CustomApplicationContextWrapper
import com.acevedosharp.entities.ProveedorDB
import com.acevedosharp.persistence_layer.repository_services.ProveedorService
import javafx.collections.FXCollections
import com.acevedosharp.ui_models.Proveedor
import javafx.collections.ObservableList
import tornadofx.Controller

class ProveedorController: Controller() {
    private val proveedorService =
        find<CustomApplicationContextWrapper>().context.getBean<ProveedorService>(ProveedorService::class.java)

    val proveedores: ObservableList<Proveedor> = FXCollections.observableArrayList<Proveedor>(
        proveedorService.all().map {
            Proveedor(
                it.proveedorId,
                it.nombre,
                it.telefono,
                it.direccion,
                it.correo
            )
        }
    )

    fun add(proveedor: Proveedor) {
        val res = proveedorService.add(
            ProveedorDB(
                null,
                proveedor.nombre,
                proveedor.telefono,
                proveedor.correo,
                proveedor.direccion
            )
        )
        proveedores.add(proveedor.apply { id = res.proveedorId })
    }

    fun edit(proveedor: Proveedor) {
        val res = proveedorService.edit(
            ProveedorDB(
                proveedor.id,
                proveedor.nombre,
                proveedor.telefono,
                proveedor.direccion,
                proveedor.correo
            )
        )

        proveedor.apply {
            nombre = res.nombre
            telefono = res.telefono
            direccion = res.direccion
            correo = res.correo
        }
    }


    fun isNombreAvailable(nombre: String): Boolean = proveedorService.repo.existsByNombre(nombre)
    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return proveedorService.repo.existsByNombre(nombre) && (proveedorService.repo.findByNombre(nombre).proveedorId != id)
    }

    fun isTelefonoAvailable(telefono: String): Boolean = proveedorService.repo.existsByTelefono(telefono)
    fun existsOtherWithTelefono(telefono: String, id: Int): Boolean {
        return proveedorService.repo.existsByTelefono(telefono) && (proveedorService.repo.findByTelefono(telefono).proveedorId != id)
    }

    fun isCorreoAvailable(correo: String): Boolean = proveedorService.repo.existsByCorreo(correo)
    fun existsOtherWithCorreo(correo: String, id: Int): Boolean {
        return proveedorService.repo.existsByCorreo(correo) && (proveedorService.repo.findByCorreo(correo).proveedorId != id)
    }
}