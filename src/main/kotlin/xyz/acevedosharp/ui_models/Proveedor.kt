package xyz.acevedosharp.ui_models

import tornadofx.*
import javafx.beans.property.SimpleStringProperty

class Proveedor(var id: Int?, nombre: String, telefono: String, direccion: String?, correo: String?, nit: String) {
    val nombreProperty = SimpleStringProperty(this, "nombre", nombre)
    var nombre: String by nombreProperty

    val telefonoProperty = SimpleStringProperty(this, "telefono", telefono)
    var telefono: String by telefonoProperty

    val direccionProperty = SimpleStringProperty(this, "direccion", direccion)
    var direccion: String? by direccionProperty

    val correoProperty = SimpleStringProperty(this, "correo", correo)
    var correo: String? by correoProperty

    val nitProperty = SimpleStringProperty(this, "nit", nit)
    var nit: String by nitProperty

    override fun toString(): String = nombre
}

class ProveedorModel: ItemViewModel<Proveedor>() {
    val id =        bind(Proveedor::id)
    val nombre =    bind(Proveedor::nombreProperty)
    val telefono =  bind(Proveedor::telefonoProperty)
    val direccion = bind(Proveedor::direccionProperty)
    val correo =    bind(Proveedor::correoProperty)
    val nit =       bind(Proveedor::nit)
}
