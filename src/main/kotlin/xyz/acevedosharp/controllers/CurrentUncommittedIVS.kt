package xyz.acevedosharp.controllers

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import xyz.acevedosharp.views.shared_components.ItemVentaComponent
import java.text.NumberFormat

class CurrentUncommittedIVS : Controller() {
    val ivs: ObservableList<ItemVentaComponent> = FXCollections.observableArrayList()

    val total = SimpleIntegerProperty(0)

    val totalDisplay = SimpleStringProperty("0")

    init {
        println("Created new IVS container")
        ivs.onChange {
            recalculateTotal()
        }
    }

    fun removeByCodigo(barCode: String) {
        ivs.removeIf { it.producto.codigo == barCode }
    }

    fun flush() {
        ivs.clear()
    }

    fun recalculateTotal() {
        total.value = ivs.sumBy { it.producto.precioVenta.toInt() * it.cantidad.value }

        totalDisplay.value = NumberFormat.getIntegerInstance().format(total.value)
    }
}
