package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.Cliente
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.persistence.repositories.ClienteRepo

class ClienteController : Controller(), UpdateSnapshot {
    private val clienteRepo = find<CustomApplicationContextWrapper>().context.getBean(ClienteRepo::class.java)

    private val clientes: ObservableList<ClienteDB> = FXCollections.observableArrayList()

    fun getClientesWithUpdate(): ObservableList<ClienteDB> {
        updateSnapshot()
        return clientes
    }

    fun getClientesClean(): ObservableList<ClienteDB> {
        return clientes
    }

    fun findById(id: Int) = clienteRepo.findByIdOrNull(id)

    fun save(cliente: Cliente) {
        clienteRepo.save(
            ClienteDB(
                cliente.id,
                cliente.nombre,
                cliente.telefono,
                cliente.direccion
            )
        )
        updateSnapshot()
    }


    fun isNombreAvailable(nombre: String): Boolean {
        return clienteRepo.existsByNombre(nombre)
    }

    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return clienteRepo.existsByNombre(nombre) && (clienteRepo.findByNombre(nombre).clienteId != id)
    }

    fun isTelefonoAvailable(telefono: String): Boolean {
        return clienteRepo.existsByTelefono(telefono)
    }

    fun existsOtherWithTelefono(telefono: String, id: Int): Boolean {
        return clienteRepo.existsByTelefono(telefono) && (clienteRepo.findByTelefono(telefono).clienteId != id)
    }

    override fun updateSnapshot() {
        clientes.setAll(clienteRepo.findAll())
    }
}
