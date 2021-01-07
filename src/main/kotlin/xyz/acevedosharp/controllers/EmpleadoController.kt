package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.entities.EmpleadoDB
import xyz.acevedosharp.persistence_layer.repository_services.EmpleadoService
import xyz.acevedosharp.ui_models.Empleado
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller

class EmpleadoController : Controller(), UpdateSnapshot {
    private val empleadoService = find<CustomApplicationContextWrapper>().context.getBean(EmpleadoService::class.java)

    val empleados: ObservableList<Empleado> = FXCollections.observableArrayList()

    init {
        updateSnapshot()
    }

    fun add(empleado: Empleado) {
        empleadoService.add(
            EmpleadoDB(
                null,
                empleado.nombre,
                empleado.telefono
            )
        )
        updateSnapshot()
    }

    fun edit(empleado: Empleado) {
        empleadoService.edit(
            EmpleadoDB(
                empleado.id,
                empleado.nombre,
                empleado.telefono
            )
        )
        updateSnapshot()
    }


    fun isNombreAvailable(nombre: String): Boolean = empleadoService.repo.existsByNombre(nombre)
    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return empleadoService.repo.existsByNombre(nombre) && (empleadoService.repo.findByNombre(nombre).empleadoId != id)
    }

    fun isTelefonoAvailable(telefono: String): Boolean = empleadoService.repo.existsByTelefono(telefono)
    fun existsOtherWithTelefono(telefono: String, id: Int): Boolean {
        return empleadoService.repo.existsByTelefono(telefono) && (empleadoService.repo.findByTelefono(telefono).empleadoId != id)
    }

    override fun updateSnapshot() {
        empleados.setAll(
            empleadoService.all().map {
                Empleado(
                    it.empleadoId,
                    it.nombre,
                    it.telefono
                )
            }
        )
    }
}
