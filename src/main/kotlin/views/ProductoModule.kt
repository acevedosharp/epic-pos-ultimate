package views

import controllers.ProductoController
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TableView
import javafx.stage.Modality
import models.Producto
import models.ProductoModel
import styles.MainStylesheet
import tornadofx.*

class ProductosView : View("Módulo de productos") {

    val productoController = find<ProductoController>()
    val model: ProductoModel by inject()
    val existsSelection = SimpleBooleanProperty(false)
    var table: TableView<Producto> by singleAssign()

    override val root = borderpane {
        useMaxSize = true

        top {
            hbox(4, Pos.CENTER_LEFT) {
                button("Nuevo Producto") {
                    addClass(MainStylesheet.greenButton)
                    action {
//                        NewProductoFormView().openWindow(owner = currentWindow, modality = Modality.WINDOW_MODAL)
                        openInternalWindow<NewProductoFormView>(closeButton = false, modal = true)
                    }
                }
                button("Editar selección") {
                    enableWhen(existsSelection)
                    addClass(MainStylesheet.blueButton)
                    action {
                        openInternalWindow<EditProductoFormView>(closeButton = false, modal = true)
                    }
                }
                button("Eliminar selección") {
                    enableWhen(existsSelection)
                    addClass(MainStylesheet.redButton)
                    action {
                        openInternalWindow<ConfirmDeleteDialog>(closeButton = false, modal = true, params = mapOf("selectedProducto" to table.selectedItem))
                    }
                }
            }
        }

        left {
            add(SideNavigation::class)
        }

        center {
            table = tableview(productoController.productos) {
                column("Codigo", Producto::codigoProperty)
                column("Desc. Larga", Producto::descLargaProperty)
                column("Desc. Corta", Producto::descCortaProperty)
                column("Precio Venta", Producto::precioVentaProperty)
                column("Existencias", Producto::existenciasProperty)
                bindSelected(model)
                selectionModel.selectedItemProperty().onChange {
                    existsSelection.value = it != null
                }
                smartResize()
            }
        }
    }
}

class NewProductoFormView : Fragment() {

    // New Model so that it is always empty
    val codigo = SimpleStringProperty()
    val descLarga = SimpleStringProperty()
    val descCorta = SimpleStringProperty()
    val precioVenta = SimpleIntegerProperty()
    val existencias = SimpleIntegerProperty()

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Nuevo Producto") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.greenLabel)
        }
        form {
            fieldset {
                field("Código") {
                    textfield(codigo)
                }
                field("Descripción larga") {
                    textfield(descLarga)
                }
                field("Descripción corta") {
                    textfield(descCorta)
                }
                field("Precio de venta") {
                    textfield(precioVenta)
                }
                field("Existencias") {
                    textfield(existencias)
                }
                hbox(spacing = 10, alignment = Pos.CENTER) {
                    button("Añadir") {
                        addClass(MainStylesheet.greenButton)
                        action {
                            find<ProductoController>().productos.add(
                                Producto(
                                    codigo.value,
                                    descLarga.value,
                                    descCorta.value,
                                    precioVenta.value.toInt(),
                                    existencias.value.toInt()
                                )
                            )
                            close()
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.redButton)
                        action { close() }
                    }
                }
            }
        }
    }

}

class EditProductoFormView : View() {
    // New Model so that it is always empty
    val model: ProductoModel by inject()

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Editar Producto") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.blueLabel)
        }
        form {
            fieldset {
                field("Código") {
                    textfield(model.codigo)
                }
                field("Descripción larga") {
                    textfield(model.descLarga)
                }
                field("Descripción corta") {
                    textfield(model.descCorta)
                }
                field("Precio de venta") {
                    textfield(model.precioVenta)
                }
                field("Existencias") {
                    textfield(model.existencias)
                }
                hbox(spacing = 10, alignment = Pos.CENTER) {
                    button("Confirmar") {
                        addClass(MainStylesheet.greenButton)
                        action {
                            model.commit()
                            close()
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.redButton)
                        action {
                            model.rollback()
                            close()
                        }
                    }
                }
            }
        }
    }
}

class ConfirmDeleteDialog : View() {
    val productoController: ProductoController by inject()

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("¿Está seguro de eliminar la selección?") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.redLabel)
        }
        label("Esta acción no se puede deshacer.").style {
            padding = box(vertical = 30.px, horizontal = 5.px)
        }
        hbox(spacing = 10, alignment = Pos.CENTER) {
            button("Sí") {
                addClass(MainStylesheet.greenButton)
                addClass(MainStylesheet.expandedButton)
                action {
                    productoController.productos.remove(params["selectedProducto"])
                    close()
                }
            }
            button("No") {
                addClass(MainStylesheet.redButton)
                addClass(MainStylesheet.expandedButton)
                action { close() }
            }
        }
    }
}