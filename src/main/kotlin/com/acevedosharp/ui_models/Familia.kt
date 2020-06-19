package com.acevedosharp.ui_models

import tornadofx.*
import javafx.beans.property.SimpleStringProperty

class Familia(var id: Int?, nombre: String) {
    val nombreProperty = SimpleStringProperty(this, "nombre", nombre)
    var nombre by nombreProperty

    override fun toString(): String {
        return nombre
    }
}

class FamiliaModel: ItemViewModel<Familia>() {
    val id = bind(Familia::id)
    val nombre = bind(Familia::nombreProperty)
}