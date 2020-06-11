package views

import controllers.ProductoController
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.stage.Modality
import javafx.util.StringConverter
import javafx.util.converter.IntegerStringConverter
import models.Producto
import models.ProductoModel
import styles.MainStylesheet
import tornadofx.*

class ProductosView : View("Módulo de productos") {

    val productoController = find<ProductoController>()
    val model: ProductoModel by inject()
    val existsSelection = SimpleBooleanProperty(false)
    val searchByCodigo = SimpleStringProperty()
    val searchByDescripcion = SimpleStringProperty()
    var table: TableView<Producto> by singleAssign()

    init {
        searchByCodigo.onChange {
            searchByDescripcion.value = ""
            table.items = FXCollections.observableArrayList(productoController.productos.filter { it.codigo.toLowerCase().contains(searchByCodigo.value.toLowerCase()) })
        }
        searchByDescripcion.onChange {
            searchByCodigo.value = ""
            table.items = FXCollections.observableArrayList(productoController.productos.filter {
                it.descLarga.toLowerCase().contains(searchByDescripcion.value.toLowerCase()) || it.descCorta.toLowerCase().contains(searchByDescripcion.value.toLowerCase())
            })
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation::class)
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Producto") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewProductoFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditProductoFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Eliminar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.redButton)
                        action {
                            openInternalWindow<ConfirmDeleteDialog>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("selectedProducto" to table.selectedItem)
                            )
                        }
                    }
                    rectangle(width = 10, height = 0)
                    line(0, 0, 0, 35).style {
                        stroke = c(255, 255, 255, 0.25)
                    }
                    rectangle(width = 10, height = 0)
                    hbox(spacing = 10, alignment = Pos.CENTER) {
                        vbox {
                            label("Buscar por código").apply { addClass(MainStylesheet.searchLabel) }
                            textfield(searchByCodigo)

                            prefWidth = 250.0
                        }
                        vbox {
                            label("Buscar por descripción").apply { addClass(MainStylesheet.searchLabel) }
                            textfield(searchByDescripcion)

                            prefWidth = 250.0
                        }
                    }
                }

            }

            center {
                hbox {
                    table = tableview(productoController.productos) {
                        column("Codigo", Producto::codigoProperty)
                        column("Desc. Larga", Producto::descLargaProperty).remainingWidth()
                        column("Desc. Corta", Producto::descCortaProperty).pctWidth(20)
                        column("Precio Venta", Producto::precioVentaProperty)
                        column("Existencias", Producto::existenciasProperty)
                        column("Ver pedidos", Producto::verPedidosButton).style {
                            alignment = Pos.CENTER
                            textAlignment = TextAlignment.CENTER
                            tileAlignment = Pos.CENTER
                        }

                        smartResize()

                        bindSelected(model)
                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                        }

                        style {

                        }

                        hgrow = Priority.ALWAYS
                    }
                    paddingAll = 6
                    style {
                        backgroundColor += Color.WHITE
                    }
                }
            }

            style {
                backgroundColor += Color.WHITE
            }
        }
    }
}

// 1. These views need to be accesible from anywhere so that they can be used in other modules for convenience.
// 2. I'm also aware of how clunky this seems, the fact that so much code is repeated. However the solution is, in this case,
// worse than the symptom because while both forms are identical, the Spinner initialization isn't and the simpler
// solution (this) wins for me.
class NewProductoFormView : Fragment() {

    // New Model so that it is always empty
    private val model = ProductoModel()

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
                    textfield(model.codigo).validator {
                        if (it.isNullOrBlank()) error("Código requerido")
                        else if (it.length > 10) error("Máximo 10 caracteres (${it.length})")
                        else null
                    }
                }
                field("Descripción larga") {
                    textfield(model.descLarga).validator {
                        if (it.isNullOrBlank()) error("Descripción larga requerida")
                        else if (it.length > 50) error("Máximo 50 caracteres (${it.length})")
                        else null
                    }
                }
                field("Descripción corta") {
                    textfield(model.descCorta).validator {
                        if (it.isNullOrBlank()) error("Descripción corta requerida")
                        else if (it.length > 25) error("Máximo 25 caracteres (${it.length})")
                        else null
                    }
                }
                field("Precio de venta") {
                    model.precioVenta.value = 50
                    spinner(property = model.precioVenta, initialValue = 50, min = 50, max = Int.MAX_VALUE, amountToStepBy = 500, editable = true)
                }
                field("Existencias") {
                    model.existencias.value = 0
                    spinner(property = model.existencias, initialValue = 0, min = 0, max = Int.MAX_VALUE, amountToStepBy = 1, editable = true)
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Añadir") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.greenButton)
                        addClass(MainStylesheet.expandedButton)
                        action {
                            model.commit {
                                find<ProductoController>().productos.add(
                                    Producto(
                                        model.codigo.value,
                                        model.descLarga.value,
                                        model.descCorta.value,
                                        model.precioVenta.value.toInt(),
                                        model.existencias.value.toInt()
                                    )
                                )
                                close()
                            }

                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.redButton)
                        addClass(MainStylesheet.expandedButton)
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
                    textfield(model.codigo).validator {
                        if (it.isNullOrBlank()) error("Código requerido")
                        else if (it.length > 10) error("Máximo 10 caracteres (${it.length})")
                        else null
                    }
                }
                field("Descripción larga") {
                    textfield(model.descLarga).validator {
                        if (it.isNullOrBlank()) error("Descripción larga requerida")
                        else if (it.length > 50) error("Máximo 50 caracteres (${it.length})")
                        else null
                    }
                }
                field("Descripción corta") {
                    textfield(model.descCorta).validator {
                        if (it.isNullOrBlank()) error("Descripción corta requerida")
                        else if (it.length > 25) error("Máximo 25 caracteres (${it.length})")
                        else null
                    }
                }
                field("Precio de venta") {
                    spinner(property = model.precioVenta, min = 50, max = Int.MAX_VALUE, amountToStepBy = 500, editable = true)
                }
                field("Existencias") {
                    spinner(property = model.existencias, min = 0, max = Int.MAX_VALUE, amountToStepBy = 1, editable = true)
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Confirmar") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.greenButton)
                        addClass(MainStylesheet.expandedButton)
                        action {
                            model.commit()
                            close()
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.redButton)
                        addClass(MainStylesheet.expandedButton)
                        action {
                            model.rollback()
                            close()
                        }
                    }
                }
            }
        }
    }

    init {
        println(model.precioVenta.value)
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
        label("Esta acción no se puede deshacer. ¿Confirmar?").style {
            padding = box(vertical = 30.px, horizontal = 5.px)
        }
        hbox(spacing = 80, alignment = Pos.CENTER) {
            button("Sí") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.greenButton)
                addClass(MainStylesheet.expandedButton)
                action {
                    productoController.productos.remove(params["selectedProducto"])
                    close()
                }
            }
            button("No") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.redButton)
                addClass(MainStylesheet.expandedButton)
                action { close() }
            }
        }
    }
}