package xyz.acevedosharp.ui_models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue
import xyz.acevedosharp.persistence.entities.ProductoDB

class Lote(var id: Int?, cantidad: Int, precioCompra: Int, producto: ProductoDB) {
    val cantidadProperty = SimpleIntegerProperty(this, "cantidad", cantidad)
    var cantidad: Int by cantidadProperty

    val precioCompraProperty = SimpleIntegerProperty(this, "precioCompra", precioCompra)
    var precioCompra: Int by precioCompraProperty

    val productoProperty = SimpleObjectProperty(this, "producto", producto)
    var producto: ProductoDB by productoProperty

    fun productoDescCorta(): String = producto.descripcionCorta
    fun productoDescLarga(): String = producto.descripcionLarga
}

class LoteModel: ItemViewModel<Lote>() {
    val id =           bind(Lote::id)
    val cantidad =     bind(Lote::cantidadProperty)
    val precioCompra = bind(Lote::precioCompraProperty)
    val producto =     bind(Lote::productoProperty)
}
