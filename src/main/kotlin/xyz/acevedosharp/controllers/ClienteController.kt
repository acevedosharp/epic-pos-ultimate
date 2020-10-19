package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.entities.ClienteDB
import xyz.acevedosharp.persistence_layer.repository_services.ClienteService
import xyz.acevedosharp.ui_models.Cliente
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller

class ClienteController: Controller() {
    private val clienteService =
        find<CustomApplicationContextWrapper>().context.getBean<ClienteService>(ClienteService::class.java)

    val clientes: ObservableList<Cliente> = FXCollections.observableArrayList<Cliente>(
        clienteService.all().map {
            Cliente(
                it.clienteId,
                it.nombre,
                it.telefono,
                it.direccion
            )
        }
    )

    fun add(cliente: Cliente) {
        val res = clienteService.add(
            ClienteDB(
                null,
                cliente.nombre,
                cliente.telefono,
                cliente.direccion
            )
        )
        clientes.add(cliente.apply { id = res.clienteId })
    }

    fun edit(cliente: Cliente) {
        val res = clienteService.edit(
            ClienteDB(
                cliente.id,
                cliente.nombre,
                cliente.telefono,
                cliente.direccion
            )
        )

        cliente.apply {
            nombre = res.nombre
            telefono = res.telefono
            direccion = res.direccion
        }
    }


    fun isNombreAvailable(nombre: String): Boolean = clienteService.repo.existsByNombre(nombre)
    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return clienteService.repo.existsByNombre(nombre) && (clienteService.repo.findByNombre(nombre).clienteId != id)
    }

    fun isTelefonoAvailable(telefono: String): Boolean = clienteService.repo.existsByTelefono(telefono)
    fun existsOtherWithTelefono(telefono: String, id: Int): Boolean {
        return clienteService.repo.existsByTelefono(telefono) && (clienteService.repo.findByTelefono(telefono).clienteId != id)
    }
}