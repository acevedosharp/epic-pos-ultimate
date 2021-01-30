@file:Suppress("UNCHECKED_CAST")

package xyz.acevedosharp.views.screens

import javafx.beans.property.*
import xyz.acevedosharp.controllers.FamiliaController
import xyz.acevedosharp.controllers.ProductoController
import xyz.acevedosharp.ui_models.Producto
import xyz.acevedosharp.ui_models.ProductoModel
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.shared_components.SideNavigation
import xyz.acevedosharp.views.helpers.CurrentModule.PRODUCTOS
import xyz.acevedosharp.views.helpers.FormType
import xyz.acevedosharp.views.helpers.FormType.CREATE
import xyz.acevedosharp.views.helpers.FormType.EDIT
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.Duration
import tornadofx.*
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence.entities.FamiliaDB
import xyz.acevedosharp.persistence.entities.ProductoDB

class ProductoView : View("Módulo de productos") {

    private val productoController = find<ProductoController>()
    private val familiaController = find<FamiliaController>()

    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private var table: TableView<ProductoDB> by singleAssign()
    private val searchByCodigo = SimpleStringProperty("")
    private val searchByDescripcion = SimpleStringProperty("")
    private val searchByFamilia = SimpleObjectProperty<FamiliaDB>()
    private val view = this

    init {
        Joe.currentView = view

        productoController.getProductosClean().onChange {
            searchByCodigo.value = ""
            searchByDescripcion.value = ""
            searchByFamilia.value = null
        }

        searchByCodigo.onChange { searchString ->
            if (searchString != null) {
                table.items = productoController.getProductosClean().filter {
                    it.codigo.toLowerCase().contains(searchString.toLowerCase())
                }.asObservable()
            }
        }

        searchByDescripcion.onChange { searchString ->
            if (searchString != null) {
                table.items = productoController.getProductosClean().filter {
                    it.descripcionLarga.toLowerCase().contains(searchString.toLowerCase()) ||
                            it.descripcionCorta.toLowerCase().contains(searchString.toLowerCase())
                }.asObservable()
            }
        }

        searchByFamilia.onChange { searchFamilia ->
            if (searchFamilia != null) {
                table.items = productoController.getProductosClean().filter {
                    it.familia.familiaId == searchFamilia.familiaId
                }.toObservable()
            } else {
                table.items = productoController.getProductosClean()
            }
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
                            openInternalWindow<NewProductoFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("owner" to view)
                            )
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditProductoFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf(
                                    "id" to selectedId.value,
                                    "owner" to view
                                )
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

                        vbox {
                            label("Buscar por familia").apply { addClass(MainStylesheet.searchLabel) }
                            combobox<FamiliaDB>(searchByFamilia, familiaController.getFamiliasWithUpdate()).apply {
                                prefWidth = 300.0
                                makeAutocompletable(false)
                            }

                            prefWidth = 250.0
                        }
                        button("Quitar filtro") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton)
                            action { searchByFamilia.value = null }
                        }
                    }
                }
            }

            center {
                hbox {
                    table = tableview(productoController.getProductosWithUpdate()) {
                        column("Código", ProductoDB::codigo).pctWidth(10)
                        column("Desc. Larga", ProductoDB::descripcionLarga).remainingWidth()
                        column("Desc. Corta", ProductoDB::descripcionCorta).pctWidth(20)
                        column("P. Venta", ProductoDB::precioVenta)
                        column("P. Compra", ProductoDB::precioCompraEfectivo)
                        column("Margen", ProductoDB::margen)
                        column("Existencias", ProductoDB::existencias)
                        column("Familia", ProductoDB::familia)

                        smartResize()

                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                            if (it != null) {
                                selectedId.set(it.productoId!!)
                            }
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

class BaseProductoFormView(formType: FormType, id: Int?) : Fragment() {

    private val productoController = find<ProductoController>()
    private val familiaController = find<FamiliaController>()

    private var firstTextField: TextField by singleAssign()

    private val model = if (formType == CREATE)
        ProductoModel()
    else
        ProductoModel().apply {
            val producto = productoController.findById(id!!)!!

            this.id.value = producto.productoId
            this.codigo.value = producto.codigo
            this.descLarga.value = producto.descripcionLarga
            this.descCorta.value = producto.descripcionCorta
            this.precioVenta.value = producto.precioVenta
            this.precioCompraEfectivo.value = producto.precioCompraEfectivo
            this.existencias.value = producto.existencias
            this.margen.value = producto.margen
            this.familia.value = producto.familia
        }

    init {
        runLater(Duration.millis(200.0)) {
            firstTextField.requestFocus()
        }
    }

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
                    firstTextField = textfield(model.codigo) {
                        validator(trigger = ValidationTrigger.OnBlur) {
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
                }
                field("Descripción larga") {
                    textfield(model.descLarga).validator(trigger = ValidationTrigger.OnBlur) {
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
                    hbox(10, Pos.CENTER_LEFT) {
                        textfield(model.descCorta) {
                            prefWidth = 400.0
                            validator(trigger = ValidationTrigger.OnBlur) {
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
                        button("Repetir") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.grayButton)
                            action {
                                model.descCorta.value = model.descLarga.value
                            }
                        }
                    }
                }
                field("Precio de venta") {
                    if (formType == CREATE) model.precioVenta.value = 50.0
                    spinner(
                        property = model.precioVenta as Property<Double>,
                        initialValue = 0.0,
                        min = 0.0,
                        max = Double.MAX_VALUE,
                        amountToStepBy = 500.0,
                        editable = !(formType == EDIT && model.margen.value != 0.0)
                    )
                }
                field("Existencias") {
                    if (formType == CREATE) model.existencias.value = 0
                    spinner(
                        property = model.existencias,
                        initialValue = 0,
                        min = Int.MIN_VALUE,
                        max = Int.MAX_VALUE,
                        amountToStepBy = 1,
                        editable = true
                    )
                }
                field("Margen (%)") {
                    hbox(10, Pos.CENTER_LEFT) {
                        spinner(
                            property = model.margen as Property<Double>,
                            initialValue = 0.0,
                            min = 0.0,
                            max = 100.0,
                            amountToStepBy = 0.1,
                            editable = true
                        ).validator {
                            if (it == 0.0) error("El margen de ganancia no puede ser 0.")
                            else null
                        }
                    }
                }
                field("Familia") {
                    hbox(10, Pos.CENTER_LEFT) {
                        combobox<FamiliaDB>(model.familia, familiaController.getFamiliasWithUpdate()).apply {
                            prefWidth = 300.0
                            makeAutocompletable(false)

                            validator {
                                if (it == null) error("Selecciona una familia")
                                else null
                            }
                        }
                        button("+") {
                            addClass(MainStylesheet.addButton, MainStylesheet.greenButton)
                            action {
                                openInternalWindow<NewFamiliaFormView>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf("owner" to this)
                                )
                            }
                        }
                    }
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                        action {
                            model.commit {
                                productoController.save(
                                    Producto(
                                        if (formType == CREATE) null else model.id.value,
                                        model.codigo.value,
                                        model.descLarga.value,
                                        model.descCorta.value,
                                        model.precioVenta.value.toDouble(),
                                        if (formType == CREATE) 0.0 else model.precioCompraEfectivo.value.toDouble(),
                                        model.existencias.value.toInt(),
                                        model.margen.value.toDouble(),
                                        model.familia.value
                                    )
                                )
                                close()
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                        action {
                            close()
                        }
                    }
                }
            }
        }
    }
}

class NewProductoFormView : Fragment() {
    override val root = BaseProductoFormView(CREATE, null).root

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}

class EditProductoFormView : Fragment() {
    override val root = BaseProductoFormView(EDIT, params["id"] as Int).root

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}

class SelectProductoDialog : Fragment() {

    private val productoController = find<ProductoController>()
    private val familiaController = find<FamiliaController>()

    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private var table: TableView<ProductoDB> by singleAssign()
    private val searchByCodigo = SimpleStringProperty("")
    private val searchByDescripcion = SimpleStringProperty("")
    private val searchByFamilia = SimpleObjectProperty<FamiliaDB>()

    private val parentProductoProperty = params["productoProperty"] as Property<ProductoDB>

    init {
        Joe.currentView = this

        productoController.getProductosClean().onChange {
            searchByCodigo.value = ""
            searchByDescripcion.value = ""
            searchByFamilia.value = null
        }

        searchByCodigo.onChange { searchString ->
            if (searchString != null) {
                table.items = productoController.getProductosClean().filter {
                    it.codigo.toLowerCase().contains(searchString.toLowerCase())
                }.asObservable()
            }
        }

        searchByDescripcion.onChange { searchString ->
            if (searchString != null) {
                table.items = productoController.getProductosClean().filter {
                    it.descripcionLarga.toLowerCase().contains(searchString.toLowerCase()) ||
                            it.descripcionCorta.toLowerCase().contains(searchString.toLowerCase())
                }.asObservable()
            }
        }

        searchByFamilia.onChange { searchFamilia ->
            if (searchFamilia != null) {
                table.items = productoController.getProductosClean().filter {
                    it.familia.familiaId == searchFamilia.familiaId
                }.toObservable()
            } else {
                table.items = productoController.getProductosClean()
            }
        }
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 1600.0
        label("Seleccionar Producto") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.greenLabel)
        }
        form {
            borderpane {
                setPrefSize(1600.0, 800.0)
                top {
                    hbox {
                        addClass(MainStylesheet.topBar)
                        paddingBottom = 4
                        useMaxWidth = true
                        button("Nuevo Producto") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                            action {
                                openInternalWindow<NewProductoFormView>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf("owner" to this@SelectProductoDialog)
                                )
                            }
                        }
                        button("Editar selección") {
                            enableWhen(existsSelection)
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                            action {
                                openInternalWindow<EditProductoFormView>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf(
                                        "id" to selectedId.value,
                                        "owner" to this@SelectProductoDialog
                                    )
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

                            vbox {
                                label("Buscar por familia").apply { addClass(MainStylesheet.searchLabel) }
                                combobox<FamiliaDB>(searchByFamilia, familiaController.getFamiliasWithUpdate()).apply {
                                    prefWidth = 300.0
                                    makeAutocompletable(false)
                                }

                                prefWidth = 250.0
                            }
                            button("Quitar filtro") {
                                addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton)
                                action { searchByFamilia.value = null }
                            }
                        }
                    }
                }

                center {
                    hbox {
                        table = tableview(productoController.getProductosWithUpdate()) {
                            column("Código", ProductoDB::codigo).pctWidth(10)
                            column("Desc. Larga", ProductoDB::descripcionLarga).remainingWidth()
                            column("Desc. Corta", ProductoDB::descripcionCorta).pctWidth(20)
                            column("P. Venta", ProductoDB::precioVenta)
                            column("P. Compra", ProductoDB::precioCompraEfectivo)
                            column("Margen", ProductoDB::margen)
                            column("Existencias", ProductoDB::existencias)
                            column("Familia", ProductoDB::familia)

                            smartResize()

                            selectionModel.selectedItemProperty().onChange {
                                existsSelection.value = it != null
                                if (it != null) {
                                    selectedId.set(it.productoId!!)
                                }
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
            hbox(spacing = 80, alignment = Pos.CENTER) {
                button("Aceptar") {
                    addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                    action {
                        if (selectedId.value != 0) {
                            parentProductoProperty.value = productoController.findById(selectedId.value)
                            close()
                        }
                    }
                }
                button("Cancelar") {
                    addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                    action {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}

