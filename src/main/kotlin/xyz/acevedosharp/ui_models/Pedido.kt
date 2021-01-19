package xyz.acevedosharp.ui_models

import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import xyz.acevedosharp.persistence.entities.EmpleadoDB
import xyz.acevedosharp.persistence.entities.ProveedorDB
import java.time.LocalDateTime

class Pedido(var id: Int?, fechaHora: LocalDateTime, proveedor: ProveedorDB, empleado: EmpleadoDB) {
    val fechaHoraProperty = SimpleObjectProperty(this, "fechaHora", fechaHora)
    var fechaHora: LocalDateTime by fechaHoraProperty

    val proveedorProperty = SimpleObjectProperty(this, "proveedor", proveedor)
    var proveedor: ProveedorDB by proveedorProperty

    val empleadoProperty = SimpleObjectProperty(this, "empleado", empleado)
    var empleado: EmpleadoDB by empleadoProperty
}

class PedidoModel: ItemViewModel<Pedido>() {
    val id        = bind(Pedido::id)
    val fechaHora = bind(Pedido::fechaHoraProperty)
    val proveedor = bind(Pedido::proveedorProperty)
    val empleado  = bind(Pedido::empleadoProperty)
}
