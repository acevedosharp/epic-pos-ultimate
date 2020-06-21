package com.acevedosharp.ui_models

import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class Empleado(var id: Int?, nombre: String, telefono: String) {
    val nombreProperty = SimpleStringProperty(this, "nombre", nombre)
    var nombre by nombreProperty

    val telefonoProperty = SimpleStringProperty(this, "telefono", telefono)
    var telefono by telefonoProperty


    override fun toString(): String = nombre
}

class EmpleadoModel: ItemViewModel<Empleado>() {
    val id =        bind(Empleado::id)
    val nombre =    bind(Empleado::nombreProperty)
    val telefono =  bind(Empleado::telefonoProperty)
}