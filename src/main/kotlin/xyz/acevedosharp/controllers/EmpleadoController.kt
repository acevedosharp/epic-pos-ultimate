package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.Empleado
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import xyz.acevedosharp.persistence.entities.EmpleadoDB
import xyz.acevedosharp.persistence.repositories.EmpleadoRepo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EmpleadoController : Controller(), UpdateSnapshot {
    private val empleadoRepo = find<CustomApplicationContextWrapper>().context.getBean(EmpleadoRepo::class.java)

    private val empleados: ObservableList<EmpleadoDB> = FXCollections.observableArrayList()

    fun getEmpleadosWithUpdate(): ObservableList<EmpleadoDB> {
        updateSnapshot()
        return empleados
    }

    fun getEmpleadosClean(): ObservableList<EmpleadoDB> {
        return empleados
    }

    fun findById(id: Int) = empleadoRepo.findByIdOrNull(id)

    fun save(empleado: Empleado) {
        empleadoRepo.save(
            EmpleadoDB(
                empleado.id,
                empleado.nombre,
                empleado.telefono
            )
        )
        updateSnapshot()
    }

    fun isNombreAvailable(nombre: String): Boolean {
        return empleadoRepo.existsByNombre(nombre)
    }

    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return empleadoRepo.existsByNombre(nombre) && (empleadoRepo.findByNombre(nombre).empleadoId != id)
    }

    fun isTelefonoAvailable(telefono: String): Boolean {
        return empleadoRepo.existsByTelefono(telefono)
    }

    fun existsOtherWithTelefono(telefono: String, id: Int): Boolean {
        return empleadoRepo.existsByTelefono(telefono) && (empleadoRepo.findByTelefono(telefono).empleadoId != id)
    }

    override fun updateSnapshot() {
        empleados.setAll(empleadoRepo.findAll())
    }
}
