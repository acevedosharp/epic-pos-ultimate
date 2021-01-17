package xyz.acevedosharp.views.screens

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.controllers.*
import xyz.acevedosharp.persistence_layer.repository_services.PedidoService
import xyz.acevedosharp.ui_models.*
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.UnknownErrorDialog
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
import javafx.scene.text.FontWeight
import org.springframework.data.repository.findByIdOrNull
import tornadofx.*
import tornadofx.control.DateTimePicker
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence_layer.entities.PedidoDB
import xyz.acevedosharp.persistence_layer.repository_services.LoteService
import xyz.acevedosharp.persistence_layer.repository_services.ProductoService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PedidoView : View("Módulo de pedidos") {

    private val pedidoController = find<PedidoController>()
    private val proveedorController = find<ProveedorController>()
    val pedidoService = find<CustomApplicationContextWrapper>().context.getBean(PedidoService::class.java)

    private val items: ObservableList<Node> = FXCollections.observableArrayList(
        pedidoController.pedidos.sortedByDescending { it.fechaHora }.map { PedidoDisplay(it, this).root }
    )
    private val pedidosSnapshot = pedidoController.pedidos
    private var provComboBox by singleAssign<ComboBox<Proveedor>>()
    private val searchByProveedor = SimpleObjectProperty<Proveedor>()
    private val view = this

    init {
        Joe.currentView = view

        pedidosSnapshot.onChange {
            items.setAll(pedidoController.pedidos.sortedByDescending { it.fechaHora }.map { PedidoDisplay(it, this).root })
        }
        searchByProveedor.onChange { selectedItem ->
            if (selectedItem == null)
                items.setAll(pedidoController.pedidos.sortedBy { it.fechaHora }.map { PedidoDisplay(it, this).root })
            else
                items.setAll(pedidoController.pedidos.filter { it.proveedor == selectedItem }.sortedBy { it.fechaHora }
                    .map { PedidoDisplay(it, this).root })
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(PEDIDOS, view))
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
                                params = mapOf("owner" to view)
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
                            provComboBox = combobox(searchByProveedor, proveedorController.proveedores) {
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
    private val view = this

    private val model = PedidoModel()

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 1000.0
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
                        combobox<Proveedor>(model.proveedor, proveedorController.proveedores).apply {
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
                                    params = mapOf("owner" to this)
                                )
                            }
                        }
                    }
                }
                field("Empleado") {
                    hbox(spacing = 8) {
                        combobox<Empleado>(model.empleado, empleadoController.empleados).apply {
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
                                    params = mapOf("owner" to this)
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
                                    params = mapOf("owner" to view)
                                )
                            }
                        }
                        tableview(currentUncommittedLotes.lotes) {
                            column("Producto", Lote::productoProperty).remainingWidth()
                            column("Cantidad", Lote::cantidadProperty)
                            column("P. Compra", Lote::precioCompraProperty)

                            smartResize()
                            paddingAll = 5
                            hgrow = Priority.ALWAYS
                        }
                    }
                }

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
    private val productoController = find<ProductoController>()

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
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
                    val arr = pedido.lotes
                    arr.forEach { println("${it.producto.descripcionCorta}: ${it.cantidad}") }
                    tableview(pedido.lotes.map { Lote(it.loteId, it.cantidad, it.precioCompra, productoController.productos.first { prod -> it.producto.productoId == prod.id }) }.asObservable()) {
                        column("Producto", Lote::productoProperty).remainingWidth()
                        column("Cantidad", Lote::cantidadProperty)
                        column("P. Compra", Lote::precioCompraProperty)

                        smartResize()
                        paddingAll = 5
                        hgrow = Priority.ALWAYS
                    }
                }

                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(
                            MainStylesheet.coolBaseButton,
                            MainStylesheet.greenButton,
                            MainStylesheet.expandedButton
                        )
                        action { close() }
                    }
                }
            }
        }
    }
}

class AddLoteView : Fragment() {

    private val productoController = find<ProductoController>()
    private val currentUncommittedLotes = find<CurrentUncommittedLotes>()
    private val pedidoController = find<PedidoController>()
    private val model = LoteModel()
    private val productoService = find<CustomApplicationContextWrapper>().context.getBean(ProductoService::class.java)

    private val maxPriceDisplay = SimpleStringProperty("Selecciona un producto")

    init {
        model.producto.onChange {
            val currentProduct = productoService.repo.findByIdOrNull(it!!.id)

            if (currentProduct != null) {
                val expensiveLote = pedidoController.findMostExpensiveLoteOfProducto(currentProduct)
                if (expensiveLote == null)
                    maxPriceDisplay.value = "No hay pedidos anteriores del producto"
                else {
                    val res = expensiveLote.precioCompra.toString()

                    val split = res.split(".")
                    val integerPart = split[0]
                    val decimalPart = split[1]

                    if (integerPart.length > 3) {
                        val beforeSpace = integerPart.substring(0, integerPart.length - 3)
                        val afterSpace = integerPart.substring(integerPart.length - 3, integerPart.length)
                        maxPriceDisplay.value = "$$beforeSpace,$afterSpace.$decimalPart"
                    } else {
                        maxPriceDisplay.value = "$$res"
                    }
                }
            } else {
                maxPriceDisplay.value = "Selecciona un producto"
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

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 1000.0
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
                            label("Costo efectivo anterior: ").style {
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
                        combobox<Producto>(model.producto, productoController.productos).apply {
                            prefWidth = 300.0
                            makeAutocompletable(false)
                        }.validator {
                            when (it) {
                                null -> error("Producto requerido")
                                else -> null
                            }
                        }
                        button("Nuevo Producto") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                            action {
                                openInternalWindow<NewProductoFormView>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf("owner" to this)
                                )
                            }
                        }
                    }
                }
            }

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
                                    model.cantidad.value.toInt(),
                                    model.precioCompra.value.toDouble(),
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
