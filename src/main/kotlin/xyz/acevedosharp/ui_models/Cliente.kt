package xyz.acevedosharp.ui_models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import javafx.beans.property.SimpleStringProperty

class Cliente(val id: Int?, nombre: String, telefono: String?, direccion: String?, birthdayDay: Int, birthdayMonth: Int) {
    val nombreProperty = SimpleStringProperty(this, "nombre", nombre)
    var nombre: String by nombreProperty

    val telefonoProperty = SimpleStringProperty(this, "telefono", telefono)
    var telefono: String? by telefonoProperty

    val direccionProperty = SimpleStringProperty(this, "direccion", direccion)
    var direccion: String? by direccionProperty

    // this is null when 0, enforced at the controller level since there's no easy way of achieving the same effect
    // with simpleProperties
    val birthdayDayProperty = SimpleIntegerProperty(this, "bithdayDay", birthdayDay)
    var birthdayDay: Int by birthdayDayProperty

    // same as above
    val birthdayMonthProperty = SimpleIntegerProperty(this, "birthdayMonth", birthdayMonth)
    var birthdayMonth: Int by birthdayMonthProperty

    override fun toString(): String = nombre
}

class ClienteModel: ItemViewModel<Cliente>() {
    val id =            bind(Cliente::id)
    val nombre =        bind(Cliente::nombreProperty)
    val telefono =      bind(Cliente::telefonoProperty)
    val direccion =     bind(Cliente::direccionProperty)
    val birthdayDay =   bind(Cliente::birthdayDayProperty)
    val birthdayMonth = bind(Cliente::birthdayMonthProperty)
}
