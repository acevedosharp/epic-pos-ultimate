package xyz.acevedosharp.ui_models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import xyz.acevedosharp.persistence.entities.ProductoDB

class UncommittedItemVenta(producto: ProductoDB, cantidad: Int) {
    val productoProperty = SimpleObjectProperty(this, "producto", producto)
    var producto by productoProperty

    val cantidadProperty = SimpleIntegerProperty(this, "cantidad", cantidad)
    var cantidad by cantidadProperty
}

class UncommittedIVModel: ItemViewModel<UncommittedItemVenta>() {
    val producto = bind(UncommittedItemVenta::productoProperty)
    val cantidad = bind(UncommittedItemVenta::cantidadProperty)
}
