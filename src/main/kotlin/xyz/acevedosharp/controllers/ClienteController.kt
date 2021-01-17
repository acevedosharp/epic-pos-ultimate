package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.persistence_layer.repository_services.ClienteService
import xyz.acevedosharp.ui_models.Cliente
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller
import xyz.acevedosharp.InternetConnection
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence_layer.entities.ClienteDB
import xyz.acevedosharp.views.NoInternetConnectionErrorDialog

class ClienteController : Controller(), UpdateSnapshot {
    private val clienteService = find<CustomApplicationContextWrapper>().context.getBean(ClienteService::class.java)

    val clientes: ObservableList<Cliente> = FXCollections.observableArrayList()

    init {
        updateSnapshot()
    }

    fun add(cliente: Cliente) {
        clienteService.add(
            ClienteDB(
                null,
                cliente.nombre,
                cliente.telefono,
                cliente.direccion
            )
        )
        updateSnapshot()
    }

    fun edit(cliente: Cliente) {
        clienteService.edit(
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
        return clienteService.repo.existsByNombre(nombre)
    }
    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return clienteService.repo.existsByNombre(nombre) && (clienteService.repo.findByNombre(nombre).clienteId != id)
    }

    fun isTelefonoAvailable(telefono: String): Boolean {
        return clienteService.repo.existsByTelefono(telefono)
    }
    fun existsOtherWithTelefono(telefono: String, id: Int): Boolean {
        return clienteService.repo.existsByTelefono(telefono) && (clienteService.repo.findByTelefono(telefono).clienteId != id)
    }

    override fun updateSnapshot() {
        clientes.setAll(
            clienteService.all().map {
                Cliente(
                    it.clienteId,
                    it.nombre,
                    it.telefono,
                    it.direccion
                )
            }
        )
    }
}
