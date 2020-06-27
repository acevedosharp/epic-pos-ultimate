package com.acevedosharp.ui_models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.time.LocalDateTime

class Venta(var id: Int?, fechaHora: LocalDateTime, precioTotal: Int, pagoRecibido: Int, empleado: Empleado, cliente: Cliente) {
    val fechaHoraProperty = SimpleObjectProperty<LocalDateTime>(this, "fechaHora", fechaHora)
    var fechaHora by fechaHoraProperty

    val precioTotalProperty = SimpleIntegerProperty(this, "precioTotal", precioTotal)
    var precioTotal by precioTotalProperty

    val pagoRecibidoProperty = SimpleIntegerProperty(this, "pagoRecibido", pagoRecibido)
    var pagoRecibido by pagoRecibidoProperty

    val empleadoProperty = SimpleObjectProperty<Empleado>(this, "empleado", empleado)
    var empleado by empleadoProperty

    val clienteProperty = SimpleObjectProperty<Cliente>(this, "cliente", cliente)
    var cliente by clienteProperty
}

class VentaModel: ItemViewModel<Venta>() {
    val id           = bind(Venta::id)
    val fechaHora    = bind(Venta::fechaHoraProperty)
    val precioTotal  = bind(Venta::precioTotalProperty)
    val pagoRecibido = bind(Venta::pagoRecibidoProperty)
    val empleado     = bind(Venta::empleadoProperty)
    val cliente      = bind(Venta::clienteProperty)
}