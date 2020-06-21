package com.acevedosharp.controllers

import com.acevedosharp.CustomApplicationContextWrapper
import com.acevedosharp.entities.EmpleadoDB
import com.acevedosharp.persistence_layer.repository_services.EmpleadoService
import com.acevedosharp.ui_models.Empleado
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller

class EmpleadoController: Controller() {
    private val empleadoService =
        find<CustomApplicationContextWrapper>().context.getBean<EmpleadoService>(EmpleadoService::class.java)

    val empleados: ObservableList<Empleado> = FXCollections.observableArrayList<Empleado>(
        empleadoService.all().map {
            Empleado(
                it.empleadoId,
                it.nombre,
                it.telefono
            )
        }
    )

    fun add(empleado: Empleado) {
        val res = empleadoService.add(
            EmpleadoDB(
                null,
                empleado.nombre,
                empleado.telefono
            )
        )
        empleados.add(empleado.apply { id = res.empleadoId })
    }

    fun edit(empleado: Empleado) {
        val res = empleadoService.edit(
            EmpleadoDB(
                empleado.id,
                empleado.nombre,
                empleado.telefono
            )
        )

        empleado.apply {
            nombre = res.nombre
            telefono = res.telefono
        }
    }


    fun isNombreAvailable(nombre: String): Boolean = empleadoService.repo.existsByNombre(nombre)
    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return empleadoService.repo.existsByNombre(nombre) && (empleadoService.repo.findByNombre(nombre).empleadoId != id)
    }

    fun isTelefonoAvailable(telefono: String): Boolean = empleadoService.repo.existsByTelefono(telefono)
    fun existsOtherWithTelefono(telefono: String, id: Int): Boolean {
        return empleadoService.repo.existsByTelefono(telefono) && (empleadoService.repo.findByTelefono(telefono).empleadoId != id)
    }
}