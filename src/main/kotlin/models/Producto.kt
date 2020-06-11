package models
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import tornadofx.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import styles.MainStylesheet

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

    val verPedidosButton = ReadOnlyObjectWrapper(
        Button("+").apply {
            addClass(MainStylesheet.coolBaseButton)
            addClass(MainStylesheet.greenButton)
            action {
                println("You have pressed $descLarga")
            }
        }
    )
}

class ProductoModel: ItemViewModel<Producto>() {
    val codigo = bind(Producto::codigoProperty)
    val descLarga = bind(Producto::descLargaProperty)
    val descCorta = bind(Producto::descCortaProperty)
    val precioVenta = bind(Producto::precioVentaProperty)
    val existencias = bind(Producto::existenciasProperty)
}
