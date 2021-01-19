package xyz.acevedosharp.ui_models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.persistence.entities.EmpleadoDB
import java.time.LocalDateTime

class Venta(var id: Int?, fechaHora: LocalDateTime, precioTotal: Int, pagoRecibido: Int, empleado: EmpleadoDB, cliente: ClienteDB) {
    val fechaHoraProperty = SimpleObjectProperty(this, "fechaHora", fechaHora)
    var fechaHora: LocalDateTime by fechaHoraProperty

    val precioTotalProperty = SimpleIntegerProperty(this, "precioTotal", precioTotal)
    var precioTotal: Int by precioTotalProperty

    val pagoRecibidoProperty = SimpleIntegerProperty(this, "pagoRecibido", pagoRecibido)
    var pagoRecibido: Int by pagoRecibidoProperty

    val empleadoProperty = SimpleObjectProperty(this, "empleado", empleado)
    var empleado: EmpleadoDB by empleadoProperty

    val clienteProperty = SimpleObjectProperty(this, "cliente", cliente)
    var cliente: ClienteDB by clienteProperty
}

class VentaModel: ItemViewModel<Venta>() {
    val id           = bind(Venta::id)
    val fechaHora    = bind(Venta::fechaHoraProperty)
    val precioTotal  = bind(Venta::precioTotalProperty)
    val pagoRecibido = bind(Venta::pagoRecibidoProperty)
    val empleado     = bind(Venta::empleadoProperty)
    val cliente      = bind(Venta::clienteProperty)
}
