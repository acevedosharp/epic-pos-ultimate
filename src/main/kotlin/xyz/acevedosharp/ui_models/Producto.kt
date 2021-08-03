package xyz.acevedosharp.ui_models

import tornadofx.*
import javafx.beans.property.*
import xyz.acevedosharp.persistence.entities.FamiliaDB

class Producto(
    var id: Int?,
    codigo: String,
    descLarga: String,
    descCorta: String,
    precioVenta: Int,
    precioCompraEfectivo: Double,
    existencias: Int,
    margen: Double,
    familia: FamiliaDB,
    alertaExistencias: Int,
    iva: Int,
    precioCompra: Double
) {
    val codigoProperty = SimpleStringProperty(this, "codigo", codigo)
    var codigo: String by codigoProperty

    val descLargaProperty = SimpleStringProperty(this, "descLarga", descLarga)
    var descLarga: String by descLargaProperty

    val descCortaProperty = SimpleStringProperty(this, "descCorta", descCorta)
    var descCorta: String by descCortaProperty

    val precioVentaProperty = SimpleIntegerProperty(this, "precioVenta", precioVenta)
    var precioVenta: Int by precioVentaProperty

    val precioCompraEfectivoProperty = SimpleDoubleProperty(this, "precioCompraEfectivo", precioCompraEfectivo)
    var precioCompraEfectivo: Double by precioCompraEfectivoProperty

    val existenciasProperty = SimpleIntegerProperty(this, "existencias", existencias)
    var existencias: Int by existenciasProperty

    val margenProperty = SimpleDoubleProperty(this, "margen", margen)
    var margen: Double by margenProperty

    val familiaProperty = SimpleObjectProperty(this, "familia", familia)
    var familia: FamiliaDB by familiaProperty

    val alertaExistenciasProperty = SimpleIntegerProperty(this, "alertaExistencias", alertaExistencias)
    var alertaExistencias: Int by alertaExistenciasProperty

    val ivaProperty = SimpleIntegerProperty(this, "iva", iva)
    var iva: Int by ivaProperty

    val precioCompraProperty = SimpleDoubleProperty(this, "precioCompra", precioCompra)
    var precioCompra: Double by precioCompraProperty

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
    val alertaExistencias =    bind(Producto::alertaExistenciasProperty)
    val iva =                  bind(Producto::iva)
    val precioCompra =         bind(Producto::precioCompra)
}
