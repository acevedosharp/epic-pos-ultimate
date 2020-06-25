package com.acevedosharp.views.modules

import com.acevedosharp.controllers.FamiliaController
import com.acevedosharp.controllers.ProductoController
import com.acevedosharp.ui_models.Familia
import com.acevedosharp.ui_models.Producto
import com.acevedosharp.ui_models.ProductoModel
import com.acevedosharp.views.MainStylesheet
import com.acevedosharp.views.shared_components.SideNavigation
import com.acevedosharp.views.UnknownErrorDialog
import com.acevedosharp.views.helpers.CurrentModule.PRODUCTOS
import com.acevedosharp.views.helpers.FormType
import com.acevedosharp.views.helpers.FormType.CREATE
import com.acevedosharp.views.helpers.FormType.EDIT
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class ProductoView : View("Módulo de productos") {

    private val productoController = find<ProductoController>()
    private val familiaController = find<FamiliaController>()

    private val model: ProductoModel by inject()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByCodigo = SimpleStringProperty()
    private val searchByDescripcion = SimpleStringProperty()
    private var table: TableView<Producto> by singleAssign()
    private val view = this

    init {
        searchByCodigo.onChange {
            searchByDescripcion.value = ""
            table.items = productoController.productos.filter {
                it.codigo.toLowerCase().contains(searchByCodigo.value.toLowerCase())
            }.asObservable()
        }
        searchByDescripcion.onChange {
            searchByCodigo.value = ""
            table.items = productoController.productos.filter {
                it.descLarga.toLowerCase().contains(searchByDescripcion.value.toLowerCase()) ||
                it.descCorta.toLowerCase().contains(searchByDescripcion.value.toLowerCase())
            }.asObservable()
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(PRODUCTOS, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Producto") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewProductoFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditProductoFormView>(
                                closeButton = false,
                                modal = true
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
                        column("Código", Producto::codigoProperty).pctWidth(10)
                        column("Desc. Larga", Producto::descLargaProperty).remainingWidth()
                        column("Desc. Corta", Producto::descCortaProperty).pctWidth(20)
                        column("P. Venta", Producto::precioVentaProperty)
                        column("Existencias", Producto::existenciasProperty)
                        column("Familia", Producto::familiaProperty)

                        smartResize()

                        bindSelected(model)
                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                            model.id.value = it?.id
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

class BaseProductoFormView(formType: FormType) : Fragment() {

    private val productoController = find<ProductoController>()
    private val familiaController = find<FamiliaController>()

    private val model = if (formType == CREATE) ProductoModel() else find(ProductoModel::class)

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 800.0
        label(if (formType == CREATE) "Nuevo Producto" else "Editar Producto") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(if (formType == CREATE) MainStylesheet.greenLabel else MainStylesheet.blueLabel)
        }
        form {
            fieldset {
                field("Código") {
                    textfield(model.codigo).validator {
                        when {
                            if (formType == CREATE) productoController.isCodigoAvailable(it.toString())
                            else productoController.existsOtherWithCodigo(it.toString(), model.id.value)
                            -> error("Código no disponible")
                            it.isNullOrBlank() -> error("Código requerido")
                            it.length > 20 -> error("Máximo 20 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Descripción larga") {
                    textfield(model.descLarga).validator {
                        when {
                            if (formType == CREATE) productoController.isDescLargaAvailable(it.toString())
                            else productoController.existsOtherWithDescLarga(it.toString(), model.id.value)
                            -> error("Descripción larga no disponible")
                            it.isNullOrBlank() -> error("Descripción larga requerida")
                            it.length > 50 -> error("Máximo 50 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Descripción corta") {
                    textfield(model.descCorta).validator {
                        when {
                            if (formType == CREATE) productoController.isDescCortaAvailable(it.toString())
                            else productoController.existsOtherWithDescCorta(it.toString(), model.id.value)
                            -> error("Descripción corta no disponible")
                            it.isNullOrBlank() -> error("Descripción corta requerida")
                            it.length > 25 -> error("Máximo 25 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Precio de venta") {
                    if (formType == CREATE) model.precioVenta.value = 50
                    spinner(
                        property = model.precioVenta,
                        initialValue = 50,
                        min = 50,
                        max = Int.MAX_VALUE,
                        amountToStepBy = 500,
                        editable = true
                    )
                }
                field("Existencias") {
                    if (formType == CREATE) model.existencias.value = 0
                    spinner(
                        property = model.existencias,
                        initialValue = 0,
                        min = 0,
                        max = Int.MAX_VALUE,
                        amountToStepBy = 1,
                        editable = true
                    )
                }
                field("Familia") {
                    hbox(10, Pos.CENTER_LEFT) {
                        combobox<Familia>(model.familia, familiaController.familias).apply {
                            prefWidth = 300.0
                            makeAutocompletable(false)
                        }
                        button("+") {
                            addClass(MainStylesheet.addButton, MainStylesheet.greenButton)
                            action { openInternalWindow<NewFamiliaFormView>(closeButton = false, modal = true) }
                        }
                    }
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                        action {
                            if (formType == CREATE) {
                                try {
                                    model.commit {
                                        productoController.add(
                                            Producto(
                                                null,
                                                model.codigo.value,
                                                model.descLarga.value,
                                                model.descCorta.value,
                                                model.precioVenta.value.toInt(),
                                                model.existencias.value.toInt(),
                                                model.familia.value
                                            )
                                        )
                                        close()
                                    }
                                } catch (e: Exception) {
                                    openInternalWindow(UnknownErrorDialog())
                                    e.printStackTrace()
                                }
                            } else {
                                try {
                                    model.commit {
                                        productoController.edit(model.item)
                                        close()
                                    }
                                } catch (e: Exception) {
                                    openInternalWindow(UnknownErrorDialog())
                                   e.printStackTrace()
                                }
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                        action {
                            if (formType == CREATE) {
                                close()
                            } else {
                                model.rollback()
                                close()
                            }
                        }
                    }
                }
            }
        }
    }
}

// 1. These com.acevedosharp.views need to be accesible from anywhere so that they can be used in other modules for convenience.
class NewProductoFormView: Fragment() {
    override val root = BaseProductoFormView(CREATE).root
}

class EditProductoFormView: Fragment() {
    override val root = BaseProductoFormView(EDIT).root
}