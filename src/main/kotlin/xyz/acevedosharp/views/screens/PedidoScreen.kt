@file:Suppress("UNCHECKED_CAST", "SENSELESS_COMPARISON")

package xyz.acevedosharp.views.screens

import xyz.acevedosharp.controllers.*
import xyz.acevedosharp.ui_models.*
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.shared_components.SideNavigation
import xyz.acevedosharp.views.helpers.CurrentModule.PEDIDOS
import xyz.acevedosharp.views.shared_components.PedidoDisplay
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.control.DateTimePicker
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence.entities.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PedidoView : View("Epic POS - Pedidos") {
    private val pedidoController = find<PedidoController>()
    private val proveedorController = find<ProveedorController>()

    private val items: ObservableList<Node> = FXCollections.observableArrayList(
        pedidoController.getPedidosWithUpdate().sortedByDescending { it.fechaHora }.map { PedidoDisplay(it.toModel(), this).root }
    )
    private var provComboBox by singleAssign<ComboBox<ProveedorDB>>()
    private val searchByProveedor = SimpleObjectProperty<ProveedorDB>()

    init {
        pedidoController.getPedidosClean().onChange {
            searchByProveedor.value = null

            items.setAll(pedidoController.getPedidosClean()
                .sortedByDescending { it.fechaHora }
                .map { PedidoDisplay(it.toModel(), this).root })
        }

        searchByProveedor.onChange { selectedProveedor ->
            if (selectedProveedor == null)
                items.setAll(pedidoController.getPedidosClean()
                    .sortedByDescending { it.fechaHora }
                    .map { PedidoDisplay(it.toModel(), this).root })
            else
                items.setAll(pedidoController.getPedidosClean()
                    .filter { it.proveedor.proveedorId == selectedProveedor.proveedorId }
                    .sortedByDescending { it.fechaHora }
                    .map { PedidoDisplay(it.toModel(), this).root })
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(PEDIDOS, this@PedidoView))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Pedido") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewPedidoFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("owner" to this@PedidoView)
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
                            label("Buscar por proveedor").apply { addClass(MainStylesheet.searchLabel) }
                            provComboBox = combobox(searchByProveedor, proveedorController.getProveedoresWithUpdate()) {
                                prefWidth = 400.0
                                makeAutocompletable(false)
                            }

                        }
                        button("Quitar filtro") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton)
                            action { provComboBox.selectionModel.clearSelection() }
                        }

                    }
                }
            }
            center {
                flowpane {
                    paddingAll = 20
                    hgap = 15.0
                    vgap = 15.0
                    Bindings.bindContent(children, items)
                }
            }
        }
    }
}

class NewPedidoFormView : Fragment() {
    private val pedidoController = find<PedidoController>()
    private val proveedorController = find<ProveedorController>()
    private val empleadoController = find<EmpleadoController>()
    private val currentUncommittedLotes = find<CurrentUncommittedLotes>()

    private val model = PedidoModel()

    override fun onDock() {
        Joe.currentView.setValue(this@NewPedidoFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 1600.0
        prefHeight = 900.0
        label("Nuevo Pedido") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.greenLabel)
        }
        form {
            fieldset {
                field("Fecha y hora") {
                    hbox(spacing = 8) {
                        add(DateTimePicker().apply {
                            dateTimeValueProperty().bindBidirectional(model.fechaHora)
                            validator(this, model.fechaHora, ValidationTrigger.OnChange()) {
                                when (it) {
                                    null -> error("Fecha y hora requeridos")
                                    else -> null
                                }
                            }
                        })
                        button("Ahora") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.grayButton)
                            action {
                                model.fechaHora.value = LocalDateTime.now()
                            }
                        }
                    }
                }
                field("Proveedor") {
                    hbox(spacing = 8) {
                        combobox<ProveedorDB>(model.proveedor, proveedorController.getProveedoresClean()).apply {
                            prefWidth = 300.0
                            makeAutocompletable(false)
                        }.validator {
                            when (it) {
                                null -> error("Proveedor requerido")
                                else -> null
                            }
                        }
                        button("Nuevo Proveedor") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                            action {
                                openInternalWindow<NewProveedorFormView>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf("owner" to this@NewPedidoFormView)
                                )
                            }
                        }
                    }
                }
                field("Empleado") {
                    hbox(spacing = 8) {
                        combobox<EmpleadoDB>(model.empleado, empleadoController.getEmpleadosWithUpdate()).apply {
                            prefWidth = 300.0
                            makeAutocompletable(false)
                        }.validator {
                            when (it) {
                                null -> error("Empleado requerido")
                                else -> null
                            }
                        }
                        button("Nuevo Empleado") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                            action {
                                openInternalWindow<NewEmpleadoFormView>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf("owner" to this@NewPedidoFormView)
                                )
                            }
                        }
                    }
                }
                field("Lotes") {
                    vbox {
                        button("Añadir Lote") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                            action {
                                openInternalWindow<AddLoteView>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf("owner" to this@NewPedidoFormView)
                                )
                            }
                        }
                        tableview(currentUncommittedLotes.lotes) {
                            column("Producto", Lote::productoProperty).remainingWidth()
                            column("Cantidad", Lote::cantidadProperty)
                            column("P. Compra", Lote::precioCompraProperty)
                            smartResize()

                            placeholder = label("No hay lotes aún")

                            paddingAll = 5
                            hgrow = Priority.ALWAYS
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
                                pedidoController.add(
                                    Pedido(
                                        null,
                                        model.fechaHora.value,
                                        model.proveedor.value,
                                        model.empleado.value
                                    ),
                                    currentUncommittedLotes.lotes
                                )
                                currentUncommittedLotes.lotes.clear()
                                close()
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                        action {
                            currentUncommittedLotes.lotes.clear()
                            close()
                        }
                    }
                }
            }
        }
    }
}

class PedidoSummaryView : Fragment() {
    private val pedido = params["pedido"] as PedidoDB

    override fun onDock() {
        Joe.currentView.setValue(this@PedidoSummaryView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 1000.0
        label("Viendo pedido") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.blueLabel)
        }
        form {
            fieldset {
                field("Fecha y hora") {
                    textfield(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(pedido.fechaHora.toLocalDateTime())).apply { isEditable = false }
                }
                field("Proveedor") {
                    textfield(pedido.proveedor.nombre).apply { isEditable = false }
                }
                field("Empleado") {
                    textfield(pedido.empleado.nombre).apply { isEditable = false }
                }
                field("Lotes") {
                    tableview(pedido.lotes.toList().asObservable()) {
                        column("Producto", LoteDB::producto).remainingWidth()
                        column("Cantidad", LoteDB::cantidad)
                        column("P. Compra", LoteDB::precioCompra)
                        smartResize()

                        placeholder = label("El pedido no tiene lotes")

                        paddingAll = 5
                        hgrow = Priority.ALWAYS
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
                            close()
                        }
                    }
                }
            }
        }
    }
}

class AddLoteView : Fragment() {
    private val productoController = find<ProductoController>()
    private val currentUncommittedLotes = find<CurrentUncommittedLotes>()
    private val model = LoteModel()

    private val maxPriceDisplay = SimpleStringProperty("Selecciona un producto")

    init {
        model.producto.onChange {
            val currentProduct = productoController.findById(it!!.productoId!!)

            if (currentProduct != null) {
                val precioCompra = currentProduct.precioCompraEfectivo
                if (precioCompra == 0.0)
                    maxPriceDisplay.value = "No hay pedidos anteriores del producto"
                else {
                    val res = precioCompra.toString()
                    maxPriceDisplay.value = "$$res"
                }
            } else {
                maxPriceDisplay.value = "Selecciona un producto"
            }
        }
    }

    override fun onDock() {
        Joe.currentView.setValue(this@AddLoteView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 1600.0
        prefHeight = 850.0
        label("Añadir Lote") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.greenLabel)
        }
        form {
            fieldset {
                field("Cantidad") {
                    model.cantidad.value = 1
                    spinner(
                        property = model.cantidad,
                        initialValue = 1,
                        min = 1,
                        max = Int.MAX_VALUE,
                        amountToStepBy = 1,
                        editable = true
                    )
                }
                field("Precio de compra") {
                    model.precioCompra.value = 50.0
                    hbox(spacing = 10) {
                        spinner(
                            property = model.precioCompra as Property<Double>,
                            initialValue = 0.0,
                            min = 0.0,
                            max = Double.MAX_VALUE,
                            amountToStepBy = 500.0,
                            editable = true
                        )

                        vbox(spacing = 2) {
                            label("Último precio de compra: ").style {
                                fontSize = 22.px
                            }
                            label(maxPriceDisplay).style {
                                fontSize = 22.px
                                textFill = Color.GREEN
                            }
                        }
                    }
                }
                field("Producto") {
                    hbox(spacing = 8) {
                        button {
                            text = "Selecciona un producto"
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.grayLabel)
                            setOnMouseClicked {
                                openInternalWindow<SelectProductoDialog>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf(
                                        "owner" to this@AddLoteView,
                                        "productoProperty" to model.producto,
                                        "disableCodigoSearch" to false
                                    )
                                )
                            }

                            model.producto.onChange {
                                if (model.producto != null)
                                    text = model.producto.value.descripcionLarga
                                else
                                    text = "Selecciona un producto"
                            }
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
                            currentUncommittedLotes.lotes.add(
                                Lote(
                                    null,
                                    model.cantidad.value,
                                    model.precioCompra.value,
                                    model.producto.value
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
