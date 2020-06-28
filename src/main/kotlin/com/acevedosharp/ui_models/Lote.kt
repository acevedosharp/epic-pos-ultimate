package com.acevedosharp.ui_models

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class Lote(var id: Int?, cantidad: Int, precioCompra: Double, producto: Producto) {
    val cantidadProperty = SimpleIntegerProperty(this, "cantidad", cantidad)
    var cantidad by cantidadProperty

    val precioCompraProperty = SimpleDoubleProperty(this, "precioCompra", precioCompra)
    var precioCompra by precioCompraProperty

    val productoProperty = SimpleObjectProperty<Producto>(this, "producto", producto)
    var producto by productoProperty

    fun productoDescCorta(): String = producto.descCorta
}

class LoteModel: ItemViewModel<Lote>() {
    val id =           bind(Lote::id)
    val cantidad =     bind(Lote::cantidadProperty)
    val precioCompra = bind(Lote::precioCompraProperty)
    val producto =     bind(Lote::productoProperty)
}