package xyz.acevedosharp.ui_models

import tornadofx.*
import javafx.beans.property.*

class Producto(var id: Int?, codigo: String, descLarga: String, descCorta: String, precioVenta: Double, precioCompraEfectivo: Double, existencias: Int, margen: Double, familia: Familia?) {
    val codigoProperty = SimpleStringProperty(this, "codigo", codigo)
    var codigo by codigoProperty

    val descLargaProperty = SimpleStringProperty(this, "descLarga", descLarga)
    var descLarga by descLargaProperty

    val descCortaProperty = SimpleStringProperty(this, "descCorta", descCorta)
    var descCorta by descCortaProperty

    val precioVentaProperty = SimpleDoubleProperty(this, "precioVenta", precioVenta)
    var precioVenta by precioVentaProperty

    val precioCompraEfectivoProperty = SimpleDoubleProperty(this, "precioCompraEfectivo", precioCompraEfectivo)
    var precioCompraEfectivo by precioCompraEfectivoProperty

    val existenciasProperty = SimpleIntegerProperty(this, "existencias", existencias)
    var existencias by existenciasProperty

    val margenProperty = SimpleDoubleProperty(this, "margen", margen)
    var margen by margenProperty

    val familiaProperty = SimpleObjectProperty<Familia>(this, "familia", familia)
    var familia by familiaProperty

    override fun toString(): String = descCorta
}

class ProductoModel: ItemViewModel<Producto>() {
    val id =                   bind(Producto::id)
    val codigo =               bind(Producto::codigoProperty)
    val descLarga =            bind(Producto::descLargaProperty)
    val descCorta =            bind(Producto::descCortaProperty)
    val precioVenta =          bind(Producto::precioVentaProperty)
    val precioCompraEfectivo = bind(Producto::precioCompraEfectivoProperty)
    val existencias =          bind(Producto::existenciasProperty)
    val margen =               bind(Producto::margenProperty)
    val familia =              bind(Producto::familiaProperty)
}
