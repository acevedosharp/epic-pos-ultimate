package com.acevedosharp.ui_models

import tornadofx.*
import javafx.beans.property.SimpleStringProperty

class Cliente(var id: Int?, nombre: String, telefono: String, direccion: String?) {
    val nombreProperty = SimpleStringProperty(this, "nombre", nombre)
    var nombre by nombreProperty

    val telefonoProperty = SimpleStringProperty(this, "telefono", telefono)
    var telefono by telefonoProperty

    val direccionProperty = SimpleStringProperty(this, "direccion", direccion)
    var direccion by direccionProperty

    override fun toString(): String = nombre
}

class ClienteModel: ItemViewModel<Cliente>() {
    val id =        bind(Cliente::id)
    val nombre =    bind(Cliente::nombreProperty)
    val telefono =  bind(Cliente::telefonoProperty)
    val direccion = bind(Cliente::direccionProperty)
}