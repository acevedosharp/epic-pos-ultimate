package models
import tornadofx.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class Producto(codigo: String, descLarga: String, descCorta: String, precioVenta: Int, existencias: Int) {
    val codigoProperty = SimpleStringProperty(this, "codigo", codigo)
    var codigo by codigoProperty

    val descLargaProperty = SimpleStringProperty(this, "descLarga", descLarga)
    var descLarga by descLargaProperty

    val descCortaProperty = SimpleStringProperty(this, "descCorta", descCorta)
    var descCorta by descCortaProperty

    val precioVentaProperty = SimpleIntegerProperty(this, "precioVenta", precioVenta)
    var precioVenta by precioVentaProperty

    val existenciasProperty = SimpleIntegerProperty(this, "existencias", existencias)
    var existencias by existenciasProperty
}

class ProductoModel: ItemViewModel<Producto>() {
    val codigo = bind(Producto::codigoProperty)
    val descLarga = bind(Producto::descLargaProperty)
    val descCorta = bind(Producto::descCortaProperty)
    val precioVenta = bind(Producto::precioVentaProperty)
    val existencias = bind(Producto::existenciasProperty)
}
