package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import javafx.collections.FXCollections
import xyz.acevedosharp.ui_models.Proveedor
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import xyz.acevedosharp.GlobalHelper
import xyz.acevedosharp.persistence.entities.ProveedorDB
import xyz.acevedosharp.persistence.repositories.ProveedorRepo

class ProveedorController : Controller(), UpdateSnapshot {
    private val proveedorRepo = find<CustomApplicationContextWrapper>().context.getBean(ProveedorRepo::class.java)

    private val proveedores: ObservableList<ProveedorDB> = FXCollections.observableArrayList()

    fun getProveedoresWithUpdate(): ObservableList<ProveedorDB> {
        updateSnapshot()
        return proveedores
    }

    fun getProveedoresClean(): ObservableList<ProveedorDB> {
        return proveedores
    }

    fun findById(id: Int) = proveedorRepo.findByIdOrNull(id)

    fun save(proveedor: Proveedor) {
        proveedorRepo.save(
            ProveedorDB(
                proveedor.id,
                proveedor.nombre,
                proveedor.telefono,
                GlobalHelper.nullableStringEnforcer(proveedor.correo),
                GlobalHelper.nullableStringEnforcer(proveedor.direccion),
                proveedor.nit
            )
        )
        updateSnapshot()
    }

    fun isNombreAvailable(nombre: String): Boolean {
        return proveedorRepo.existsByNombre(nombre)
    }

    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return proveedorRepo.existsByNombre(nombre) && (proveedorRepo.findByNombre(nombre).proveedorId != id)
    }

    fun isNitAvailable(nit: String): Boolean {
        return proveedorRepo.existsByNit(nit)
    }

    fun existsOtherWithNit(nit: String, id: Int): Boolean {
        return proveedorRepo.existsByNit(nit) && (proveedorRepo.findByNit(nit).proveedorId != id)
    }

    fun isTelefonoAvailable(telefono: String): Boolean {
        return proveedorRepo.existsByTelefono(telefono)
    }

    fun existsOtherWithTelefono(telefono: String, id: Int): Boolean {
        return proveedorRepo.existsByTelefono(telefono) && (proveedorRepo.findByTelefono(telefono).proveedorId != id)
    }

    fun isCorreoAvailable(correo: String): Boolean {
        return proveedorRepo.existsByCorreo(correo)
    }

    fun existsOtherWithCorreo(correo: String, id: Int): Boolean {
        return proveedorRepo.existsByCorreo(correo) && (proveedorRepo.findByCorreo(correo).proveedorId != id)
    }

    override fun updateSnapshot() {
        proveedores.setAll(proveedorRepo.findAll())
    }
}
