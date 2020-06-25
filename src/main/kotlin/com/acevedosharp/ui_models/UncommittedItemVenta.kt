package com.acevedosharp.ui_models

import javafx.beans.property.SimpleIntegerProperty

class UncommittedItemVenta(val producto: Producto, cantidad: Int) {
    val cantidad = SimpleIntegerProperty(cantidad)
}