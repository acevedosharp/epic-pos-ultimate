package xyz.acevedosharp.ui_models

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.persistence.entities.EmpleadoDB
import java.time.LocalDateTime

class Venta(var id: Int?, fechaHora: LocalDateTime, totalSinIva: Double, pagoRecibido: Int,
            empleado: EmpleadoDB, cliente: ClienteDB, totalConIva: Int) {
    val fechaHoraProperty = SimpleObjectProperty(this, "fechaHora", fechaHora)
    var fechaHora: LocalDateTime by fechaHoraProperty

    val totalSinIvaProperty = SimpleDoubleProperty(this, "totalSinIva", totalSinIva)
    var totalSinIva: Double by totalSinIvaProperty

    val pagoRecibidoProperty = SimpleIntegerProperty(this, "pagoRecibido", pagoRecibido)
    var pagoRecibido: Int by pagoRecibidoProperty

    val empleadoProperty = SimpleObjectProperty(this, "empleado", empleado)
    var empleado: EmpleadoDB by empleadoProperty

    val clienteProperty = SimpleObjectProperty(this, "cliente", cliente)
    var cliente: ClienteDB by clienteProperty

    val totalConIvaProperty = SimpleIntegerProperty(this, "totalConIva", totalConIva)
    var totalConIva: Int by totalConIvaProperty
}

class VentaModel: ItemViewModel<Venta>() {
    val id           = bind(Venta::id)
    val fechaHora    = bind(Venta::fechaHoraProperty)
    val totalSinIva  = bind(Venta::totalSinIva)
    val pagoRecibido = bind(Venta::pagoRecibidoProperty)
    val empleado     = bind(Venta::empleadoProperty)
    val cliente      = bind(Venta::clienteProperty)
    val totalConIva  = bind(Venta::totalConIva)
}
