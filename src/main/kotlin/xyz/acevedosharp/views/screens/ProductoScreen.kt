@file:Suppress("UNCHECKED_CAST")

package xyz.acevedosharp.views.screens

import javafx.beans.property.*
import javafx.collections.FXCollections
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
import xyz.acevedosharp.GlobalHelper
import xyz.acevedosharp.GlobalHelper.round
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence.entities.FamiliaDB
import xyz.acevedosharp.persistence.entities.ProductoDB
import xyz.acevedosharp.ui_models.UncommittedItemVenta
import xyz.acevedosharp.views.dialogs.CodigoNotRecognizedDialog
import xyz.acevedosharp.views.shared_components.ItemVentaComponent

class ProductoView : View("Epic POS - Productos") {
    private val productoController = find<ProductoController>()
    private val familiaController = find<FamiliaController>()

    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private var table: TableView<ProductoDB> by singleAssign()
    private val searchByCodigo = SimpleStringProperty("")
    private val searchByDescripcion = SimpleStringProperty("")
    private val searchByFamilia = SimpleObjectProperty<FamiliaDB>()

    init {
        productoController.getProductosClean().onChange {
            searchByCodigo.value = ""
            searchByDescripcion.value = ""
            searchByFamilia.value = null
        }

        searchByCodigo.onChange { searchString ->
            searchByDescripcion.set("")
            searchByFamilia.set(null)
            if (!searchString.isNullOrBlank()) {
                table.items = productoController.getProductosClean(codigoQuery = searchString)
            } else {
                table.items = productoController.getProductosClean()
            }
        }

        searchByDescripcion.onChange {
            table.items = productoController.getProductosClean(
                descripcionQuery = searchByDescripcion.value,
                familiaQuery = searchByFamilia.value
            )
        }

        searchByFamilia.onChange {
            table.items = productoController.getProductosClean(
                descripcionQuery = searchByDescripcion.value,
                familiaQuery = searchByFamilia.value
            )
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(PRODUCTOS, this@ProductoView))
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
                                params = mapOf("owner" to this@ProductoView)
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
                                    "owner" to this@ProductoView
                                )
                            )
                        }
                    }
                    rectangle(width = 10, height = 0)
                    button {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        prefWidth = 50.0
                        prefHeight = 50.0
                        graphic = imageview("images/history.png") {
                            fitWidth = 40.0
                            fitHeight = 40.0
                        }

                        action {
                            openInternalWindow<ProductoSaleHistoryModal>(
                                closeButton = false,
                                modal = true,
                                params = mapOf(
                                    "product" to productoController.findById(selectedId.value.toInt())!!,
                                    "owner" to this@ProductoView
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
                        column("P. Compra", ProductoDB::precioCompra)
                        column("Margen", ProductoDB::margen)
                        column("Iva", ProductoDB::iva)
                        column("Existencias", ProductoDB::existencias)
                        column("Alerta", ProductoDB::alertaExistencias)
                        column("Familia", ProductoDB::familia)
                        smartResize()

                        placeholder = label("No hay productos")

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

    private val marginString = SimpleStringProperty("")
    private val ivaString = SimpleStringProperty("")

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
            this.iva.value = producto.iva
            this.precioCompra.value = producto.precioCompra
        }

    init {
        GlobalHelper.runLaterMinimumDelay {
            firstTextField.requestFocus()
        }

        fun updateSellPrice() {
            val (marginAmount, ivaAmount, sellPrice) = GlobalHelper.calculateSellPriceBrokenDown(
                basePrice = model.precioCompra.value,
                margin = model.margen.value,
                iva = model.iva.value
            )

            marginString.set("Margen: $${marginAmount.round(2)}")
            ivaString.set("Iva: $${ivaAmount.round(2)}")

            model.precioVenta.set(sellPrice)
        }

        if (formType == EDIT)
            updateSellPrice()

        model.precioCompra.onChange { updateSellPrice() }
        model.iva.onChange { updateSellPrice() }
        model.margen.onChange { updateSellPrice() }
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
            rectangle(width = 0, height = 24)
            fieldset {
                field("Código") {
                    firstTextField = textfield(model.codigo) {
                        validator(trigger = ValidationTrigger.OnChange()) {
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
                    textfield(model.descLarga).validator(trigger = ValidationTrigger.OnChange()) {
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
                            validator(trigger = ValidationTrigger.OnChange()) {
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
                field("Alerta Existencias") {
                    if (formType == CREATE) model.alertaExistencias.value = 0
                    spinner(
                        property = model.alertaExistencias,
                        initialValue = 0,
                        min = Int.MIN_VALUE,
                        max = Int.MAX_VALUE,
                        amountToStepBy = 1,
                        editable = true
                    )
                }
                field("Precio de compra") {
                    if (formType == CREATE) model.precioCompra.value = 50.0
                    spinner(
                        property = model.precioCompra,
                        initialValue = 50.0,
                        min = 0.0,
                        max = Double.MAX_VALUE,
                        amountToStepBy = 50.0,
                        editable = true
                    )
                }
                if (model.precioCompraEfectivo.value != 0.0)
                    label("Precio último pedido (unidad): $${model.precioCompraEfectivo.value}").style {
                        fontSize = 20.px
                        textFill = Color.GREEN
                    }
                hbox(10, Pos.CENTER_LEFT) {
                    vbox(5) {
                        field("Margen (%)") {
                            spinner(
                                property = model.margen as Property<Double>,
                                initialValue = 0.0,
                                min = 0.0,
                                max = 99.9,
                                amountToStepBy = 0.1,
                                editable = true
                            ).validator {
                                if (it == 0.0) error("El margen de ganancia no puede ser 0.")
                                else null
                            }
                        }
                        label(marginString).style {
                            fontSize = 20.px
                            textFill = Color.GREEN
                        }
                    }
                    rectangle(width = 10, height = 0)
                    vbox(5) {
                        hbox(0, Pos.CENTER_LEFT) {
                            label("Iva (%)")
                            rectangle(width = 3, height = 0)
                            spinner(
                                property = model.iva,
                                initialValue = 0,
                                min = 0,
                                max = 50,
                                amountToStepBy = 1,
                                editable = true
                            )
                        }
                        label(ivaString).style {
                            fontSize = 20.px
                            textFill = Color.GREEN
                        }
                    }
                }
                field("Precio de venta") {
                    if (formType == CREATE) model.precioVenta.value = 50.0
                    textfield(model.precioVenta.asString()) {
                        isEditable = false
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
                                    params = mapOf("owner" to this@BaseProductoFormView)
                                )
                            }
                        }
                    }
                }
                rectangle(width = 0, height = 24)
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(
                            MainStylesheet.coolBaseButton,
                            MainStylesheet.greenButton,
                            MainStylesheet.expandedButton
                        )
                        action {
                            model.commit {
                                productoController.save(
                                    Producto(
                                        if (formType == CREATE) null else model.id.value,
                                        model.codigo.value,
                                        model.descLarga.value,
                                        model.descCorta.value,
                                        model.precioVenta.value,
                                        if (formType == CREATE) 0.0 else model.precioCompraEfectivo.value.toDouble(),
                                        model.existencias.value.toInt(),
                                        model.margen.value.toDouble(),
                                        model.familia.value,
                                        model.alertaExistencias.value.toInt(),
                                        model.iva.value.toInt(),
                                        model.precioCompra.value.toDouble()
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
        Joe.currentView.setValue(this@NewProductoFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}

class EditProductoFormView : Fragment() {
    override val root = BaseProductoFormView(EDIT, params["id"] as Int).root

    override fun onDock() {
        Joe.currentView.setValue(this@EditProductoFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
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

    private lateinit var searchByDescripcionField: TextField

    private val currentUncommittedIVS = params["cuivs"] as PuntoDeVentaView.CurrentUncommittedIVS
    private val disableCodigoSearch = params["disableCodigoSearch"] as Boolean?
    private val papi: PuntoDeVentaView = params["owner"] as PuntoDeVentaView

    init {
        Joe.currentView.setValue(this)

        searchByCodigo.onChange { searchString ->
            searchByDescripcion.set("")
            searchByFamilia.set(null)
            if (!searchString.isNullOrBlank()) {
                table.items = productoController.getProductosClean(codigoQuery = searchString)
            } else {
                table.items = productoController.getProductosClean()
            }
        }

        searchByDescripcion.onChange {
            table.items = productoController.getProductosClean(
                descripcionQuery = searchByDescripcion.value,
                familiaQuery = searchByFamilia.value
            )
        }

        searchByFamilia.onChange {
            table.items = productoController.getProductosClean(
                descripcionQuery = searchByDescripcion.value,
                familiaQuery = searchByFamilia.value
            )
        }

        GlobalHelper.runLaterMinimumDelay {
            searchByDescripcionField.requestFocus()
        }
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 1600.0
        prefHeight = 800.0
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
                            if (disableCodigoSearch == false) {
                                vbox {
                                    label("Buscar por código").apply { addClass(MainStylesheet.searchLabel) }
                                    textfield(searchByCodigo)

                                    prefWidth = 250.0
                                }
                            }

                            vbox {
                                label("Buscar por descripción").apply { addClass(MainStylesheet.searchLabel) }
                                searchByDescripcionField = textfield(searchByDescripcion)

                                prefWidth = if (disableCodigoSearch == false) 250.0 else 500.0
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
                        table = tableview(productoController.getProductosClean()) {
                            column("Código", ProductoDB::codigo).pctWidth(10)
                            column("Desc. Larga", ProductoDB::descripcionLarga).remainingWidth()
                            column("Desc. Corta", ProductoDB::descripcionCorta).pctWidth(20)
                            column("P. Venta", ProductoDB::precioVenta)
                            column("P. Compra", ProductoDB::precioCompraEfectivo)
                            column("Margen", ProductoDB::margen)
                            column("Existencias", ProductoDB::existencias)
                            column("Familia", ProductoDB::familia)
                            smartResize()

                            placeholder = label("No hay productos")

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
                            val producto = productoController.findById(selectedId.value)!!
                            if (producto.codigo in currentUncommittedIVS.ivs.map { it.producto.codigo }) {
                                val res = currentUncommittedIVS.ivs.find { it.producto.codigo == producto.codigo }!!
                                res.cantidad.set(res.cantidad.value + 1)
                            } else if (productoController.getProductosClean(codigoQuery = producto.codigo).size > 0) {
                                currentUncommittedIVS.ivs.add(
                                    ItemVentaComponent(
                                        UncommittedItemVenta(
                                            producto,
                                            1
                                        ),
                                        currentUncommittedIVS,
                                        papi
                                    )
                                )
                            } else {
                                openInternalWindow<CodigoNotRecognizedDialog>(
                                    params = mapOf(
                                        "owner" to this@SelectProductoDialog
                                    )
                                )
                            }
                            papi.addAlwaysFocusListener()
                            close()
                        }
                    }
                }
                button("Cancelar") {
                    addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                    action {
                        papi.addAlwaysFocusListener()
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        Joe.currentView.setValue(this@SelectProductoDialog)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}

class ProductoSaleHistoryModal : Fragment() {
    class HistoryPoint(val time: String, val value: String)

    private val productoController = find<ProductoController>()
    private val producto = params["product"] as ProductoDB

    private val historyType = SimpleStringProperty("")
    private val goBackNUnits = SimpleIntegerProperty(0)
    private val timeUnitText = SimpleStringProperty("")

    private val historyPoints = FXCollections.observableArrayList<HistoryPoint>()

    private var table: TableView<HistoryPoint> by singleAssign()

    init {
        historyType.onChange {
            if (!it.isNullOrBlank())
                timeUnitText.set(if (it == "Mensual") "meses" else "días")
        }
    }

    override val root = vbox(spacing = 0, alignment = Pos.TOP_CENTER) {
        useMaxSize = true
        prefWidth = 1200.0
        prefHeight = 800.0
        label("Historial de ventas de: ${producto.descripcionCorta}") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.blueLabel)
        }
        hbox(alignment = Pos.CENTER) {
            hgrow = Priority.ALWAYS
            form {
                fieldset {
                    hbox(spacing = 10, alignment = Pos.CENTER) {
                        label("Tipo").style { fontSize = 28.px }
                        combobox(historyType, listOf("Diario", "Mensual")).style { fontSize = 28.px }

                        hbox(spacing = 10, alignment = Pos.CENTER) {
                            hiddenWhen { historyType.isEmpty }

                            label("Devolverse").style { fontSize = 28.px }
                            spinner(
                                property = goBackNUnits,
                                initialValue = 0,
                                min = 0,
                                max = Int.MAX_VALUE,
                                amountToStepBy = 1,
                                editable = true
                            ).style {
                                prefWidth = 200.px
                                fontSize = 28.px
                            }

                            label(timeUnitText).style { fontSize = 28.px }
                        }
                    }
                    rectangle(width = 0, height = 24)
                    hbox(spacing = 80, alignment = Pos.CENTER) {
                        button("Consultar") {
                            addClass(
                                MainStylesheet.coolBaseButton,
                                MainStylesheet.greenButton,
                                MainStylesheet.expandedButton
                            )
                            action {
                                historyPoints.setAll(
                                    productoController.getHistory(
                                        producto,
                                        historyType.value,
                                        goBackNUnits.value
                                    )
                                )
                            }
                        }
                        button("Cerrar") {
                            addClass(
                                MainStylesheet.coolBaseButton,
                                MainStylesheet.redButton,
                                MainStylesheet.expandedButton
                            )
                            action {
                                close()
                            }
                        }
                    }
                }
            }
        }
        table = tableview(historyPoints) {
            readonlyColumn("Tiempo", HistoryPoint::time)
            readonlyColumn("Unidades vendidas", HistoryPoint::value)
            smartResize()

            placeholder = label("Selecciona un tipo de historial")

            hgrow = Priority.ALWAYS
        }
        paddingAll = 6
        style {
            backgroundColor += Color.WHITE
        }
    }

    override fun onDock() {
        Joe.currentView.setValue(this@ProductoSaleHistoryModal)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}