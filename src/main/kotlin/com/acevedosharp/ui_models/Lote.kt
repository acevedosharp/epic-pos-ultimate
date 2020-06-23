package com.acevedosharp.ui_models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class Lote(var id: Int?, cantidad: Int, precioCompra: Int, producto: Producto) {
    val cantidadProperty = SimpleIntegerProperty(this, "cantidad", cantidad)
    var cantidad by cantidadProperty

    val precioCompraProperty = SimpleIntegerProperty(this, "precioCompra", precioCompra)
    var precioCompra by precioCompraProperty

    val productoProperty = SimpleObjectProperty<Producto>(this, "producto", producto)
    var producto by productoProperty
}

class LoteModel: ItemViewModel<Lote>() {
    val id =           bind(Lote::id)
    val cantidad =     bind(Lote::cantidadProperty)
    val precioCompra = bind(Lote::precioCompraProperty)
    val producto =     bind(Lote::productoProperty)
}