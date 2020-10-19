package xyz.acevedosharp.ui_models

import tornadofx.*
import javafx.beans.property.SimpleStringProperty

class Proveedor(var id: Int?, nombre: String, telefono: String, direccion: String?, correo: String?) {
    val nombreProperty = SimpleStringProperty(this, "nombre", nombre)
    var nombre by nombreProperty

    val telefonoProperty = SimpleStringProperty(this, "telefono", telefono)
    var telefono by telefonoProperty

    val direccionProperty = SimpleStringProperty(this, "direccion", direccion)
    var direccion by direccionProperty

    val correoProperty = SimpleStringProperty(this, "correo", correo)
    var correo by correoProperty

    override fun toString(): String = nombre
}

class ProveedorModel: ItemViewModel<Proveedor>() {
    val id =        bind(Proveedor::id)
    val nombre =    bind(Proveedor::nombreProperty)
    val telefono =  bind(Proveedor::telefonoProperty)
    val direccion = bind(Proveedor::direccionProperty)
    val correo =    bind(Proveedor::correoProperty)
}