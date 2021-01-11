package xyz.acevedosharp.views.screens

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.controllers.ClienteController
import xyz.acevedosharp.controllers.EmpleadoController
import xyz.acevedosharp.controllers.ProductoController
import xyz.acevedosharp.controllers.VentaController
import xyz.acevedosharp.ui_models.*
import xyz.acevedosharp.views.CodigoNotRecognizedDialog
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.UnknownErrorDialog
import xyz.acevedosharp.views.helpers.CurrentModule
import xyz.acevedosharp.views.helpers.RecipePrintingService
import xyz.acevedosharp.views.shared_components.ItemVentaComponent
import xyz.acevedosharp.views.shared_components.SideNavigation
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import javafx.util.Duration
import tornadofx.*
import xyz.acevedosharp.Joe
import java.text.NumberFormat
import java.time.LocalDateTime
import kotlin.math.roundToInt

class PuntoDeVentaView : View("Punto de venta") {

    private val productoController = find<ProductoController>()
    private val view = this
    private lateinit var scene: Scene
    private lateinit var listener: ChangeListener<Node>

    private val uncommittedItemsAsViews: ObservableList<ItemVentaComponent> = FXCollections.observableArrayList()
    private val uncommittedItems: ObservableList<Node> = FXCollections.observableArrayList()
    private val dineroEntregado = SimpleIntegerProperty()
    private val valorTotal = SimpleDoubleProperty(0.0)
    private val valorTotalRoundedAndFormatted = SimpleStringProperty("0")
    private val currentCodigo = SimpleStringProperty()

    private lateinit var currentCodigoTextField: TextField

    init {
        Joe.currentView = view

        valorTotal.onChange { valorTotalRoundedAndFormatted.value = NumberFormat.getIntegerInstance().format(it.roundToInt()) }
        uncommittedItemsAsViews.onChange {
            uncommittedItemsAsViews.forEachIndexed { index, node: ItemVentaComponent ->
                node.indexProperty.set(index)
            }
            uncommittedItems.setAll(uncommittedItemsAsViews.map { it.root })
            recalculateTotal()
        }
        uncommittedItems.setAll(uncommittedItemsAsViews.map { it.root })

        // Let's hope the scene doesn't take longer than this to load - probably not, 650ms is a lot of time
        runLater(Duration.millis(650.0)) {
            currentCodigoTextField.requestFocus()
            scene = this.currentStage!!.scene
            listener = ChangeListener<Node> { _, _, _ ->
                if (!currentCodigoTextField.isFocused)
                    currentCodigoTextField.requestFocus()
            }
            addAlwaysFocusListener()
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(CurrentModule.PUNTO_DE_VENTA, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            style {
                backgroundColor += LinearGradient(
                    1.0,
                    0.0,
                    1.0,
                    1.0,
                    true,
                    null,
                    listOf(
                        Stop(0.0000, c(169, 193, 215)),
                        Stop(0.0075, c(169, 193, 215)),
                        Stop(1.0000, c(36, 116, 191))
                    )
                )
            }

            top {
                hbox(spacing = 10) {
                    addClass(MainStylesheet.topBar)

                    rectangle(height = 0.0, width = 8.0)
                    stackpane {
                        alignment = Pos.CENTER_LEFT
                        rectangle(width = 720, height = 70) { fill = c(255, 255, 255, 0.0) }
                        label("Producto").style {
                            prefWidth = 715.px
                            fontSize = 28.px
                            fontWeight = FontWeight.BOLD
                            textAlignment = TextAlignment.LEFT
                            textFill = Color.WHITE
                        }
                    }
                    line(0, 0, 0, 60).style {
                        stroke = c(255, 255, 255, 0.25)
                    }
                    stackpane {
                        alignment = Pos.CENTER_LEFT
                        rectangle(width = 150, height = 70) { fill = c(255, 255, 255, 0.0) }
                        label("Cantidad").style {
                            prefWidth = 145.px
                            fontSize = 28.px
                            fontWeight = FontWeight.BOLD
                            textAlignment = TextAlignment.CENTER
                            textFill = Color.WHITE
                        }
                    }
                    line(0, 0, 0, 60).style {
                        stroke = c(255, 255, 255, 0.25)
                    }
                    stackpane {
                        alignment = Pos.CENTER_LEFT
                        rectangle(width = 170, height = 70) { fill = c(255, 255, 255, 0.0) }
                        label("P. Unidad").style {
                            prefWidth = 165.px
                            fontSize = 28.px
                            fontWeight = FontWeight.BOLD
                            textAlignment = TextAlignment.CENTER
                            textFill = Color.WHITE
                        }
                    }
                    rectangle(height = 0.0, width = 104.0)
                    line(0, 0, 0, 75).style {
                        stroke = c(255, 255, 255, 0.40)
                    }
                    textfield(currentCodigo) {
                        currentCodigoTextField = this
                        prefWidth = 500.0
                        setOnAction {
                            if (currentCodigo.value in uncommittedItemsAsViews.map { it.producto.codigo }) {
                                val res = uncommittedItemsAsViews.find { it.producto.codigo == currentCodigo.value }!!
                                res.cantidad.set(res.cantidad.value + 1)
                            } else if (currentCodigo.value in productoController.productos.map { it.codigo }) {
                                uncommittedItemsAsViews.add(
                                    ItemVentaComponent(
                                        UncommittedItemVenta(
                                            productoController.productos.firstOrNull { it.codigo == currentCodigo.value }!!,
                                            1
                                        ),
                                        uncommittedItemsAsViews,
                                        uncommittedItemsAsViews.size
                                    )
                                )
                            } else {
                                openInternalWindow<CodigoNotRecognizedDialog>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf("owner" to view)
                                )
                            }
                            currentCodigo.set("")
                            recalculateTotal()
                        }
                    }.style {
                        fontSize = 32.px
                    }
                    button {
                        graphic = imageview("images/lupa.png") {
                            fitWidth = 50.0
                            fitHeight = 50.0
                        }
                        addClass(MainStylesheet.greenButton)
                        action {
                            openInternalWindow<CreateItemVentaManuallyForm>(
                                closeButton = false,
                                modal = true, params =
                                mapOf(
                                    "observableList" to uncommittedItemsAsViews,
                                    "papi" to view
                                )
                            )
                            removeAlwaysFocusListener()
                        }
                    }
                }
            }
            center {
                vbox(spacing = 10, alignment = Pos.TOP_CENTER) {

                    paddingAll = 10
                    scrollpane {
                        prefHeight = 1080.0
                        prefWidth = 1230.0
                        maxWidth = 1230.0
                        vbox(spacing = 10, alignment = Pos.TOP_CENTER) {
                            Bindings.bindContent(children, uncommittedItems)
                        }

                        isPannable = true
                        paddingAll = 8.0
                        style {
                            borderRadius += box(10.px)
                            borderWidth += box(0.px)
                            borderColor += box(c(0, 0, 0, 0.125))
                        }
                    }
                }
            }
            right {
                vbox(alignment = Pos.TOP_CENTER) {
                    paddingAll = 8.0
                    prefWidth = 474.0
                    hgrow = Priority.ALWAYS
                    text(Bindings.concat("Total: $", valorTotalRoundedAndFormatted)).style {
                        fontSize = 54.px
                    } // Dollar sign goes before the value?

                    textfield(dineroEntregado) {
                        prefWidth = 440.0; maxWidth = 440.0
                        alignment = Pos.CENTER
                        isFocusTraversable = false
                        isEditable = false
                        style {
                            backgroundColor += c(255, 255, 255, 0.5)
                            fontSize = 64.px
                            backgroundRadius += box(0.px)
                            borderRadius += box(0.px)
                        }
                    }
                    rectangle(width = 0.0, height = 10.0)
                    hbox(alignment = Pos.TOP_CENTER) {
                        button("1") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}1".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                        button("2") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}2".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                        button("3") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}3".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                    }
                    hbox(alignment = Pos.TOP_CENTER) {
                        button("4") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}4".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                        button("5") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}5".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                        button("6") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}6".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                    }
                    hbox(alignment = Pos.TOP_CENTER) {
                        button("7") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}7".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                        button("8") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}8".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                        button("9") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}9".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                    }
                    hbox(alignment = Pos.TOP_CENTER) {
                        button("←") { addClass(MainStylesheet.redButton, MainStylesheet.keyButton) }.action {
                            val s = dineroEntregado.value.toString()
                            if (s.length > 1)
                                dineroEntregado.set(s.substring(0, s.length - 1).toInt())
                            else
                                dineroEntregado.set(0)
                        }
                        button("0") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}0".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                        button("00") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action {
                            try {
                                dineroEntregado.set("${dineroEntregado.value}00".toInt())
                            } catch (e: NumberFormatException) {
                                dineroEntregado.set(0)
                            }
                        }
                    }
                    rectangle(width = 0.0, height = 10.0)
                    button("Realizar venta") {
                        addClass(MainStylesheet.greenButton)
                        prefWidth = 440.0
                        style {
                            fontSize = 40.px
                            fontWeight = FontWeight.BOLD
                            textFill = Color.WHITE
                        }
                        action {
                            if (uncommittedItems.size > 0 && dineroEntregado.value >= valorTotal.value) {
                                openInternalWindow<CommitVenta>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf(
                                        "observableList" to uncommittedItemsAsViews,
                                        "papi" to view,
                                        "dineroEntregado" to dineroEntregado,
                                        "valorTotal" to valorTotal
                                    )
                                )
                                removeAlwaysFocusListener()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun recalculateTotal() {
        valorTotal.set(uncommittedItemsAsViews.sumByDouble { (it.cantidad.value.toDouble() * it.producto.precioVenta) })
    }

    fun addAlwaysFocusListener() {
        currentCodigoTextField.requestFocus()
        scene.focusOwnerProperty().addListener(listener)
    }

    private fun removeAlwaysFocusListener() {
        scene.focusOwnerProperty().removeListener(listener)
    }
}

class CreateItemVentaManuallyForm : Fragment() {

    private val productoController = find<ProductoController>()
    private val model = UncommittedIVModel()

    @Suppress("UNCHECKED_CAST")
    private val uncommittedItemsAsViews: ObservableList<ItemVentaComponent> =
        params["observableList"] as ObservableList<ItemVentaComponent>
    private val papi: PuntoDeVentaView = params["papi"] as PuntoDeVentaView

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["papi"] as UIComponent
        super.onUndock()
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 600.0
        label("Añadir ítem de venta") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.greenLabel)
        }
        form {
            fieldset {
                field("Producto") {
                    combobox<Producto>(model.producto, productoController.productos).apply {
                        prefWidth = 400.0
                        makeAutocompletable(false)
                    }.validator {
                        when (it) {
                            null -> error("Producto requerido")
                            else -> null
                        }
                    }

                }
                field("Cantidad") {
                    model.cantidad.value = 1
                    spinner(
                        property = model.cantidad,
                        initialValue = 1,
                        min = 1,
                        max = Int.MAX_VALUE,
                        amountToStepBy = 1,
                        editable = true
                    ) {
                        prefWidth = 400.0
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
                                uncommittedItemsAsViews.add(
                                    ItemVentaComponent(
                                        UncommittedItemVenta(model.producto.value, model.cantidad.value.toInt()),
                                        uncommittedItemsAsViews,
                                        uncommittedItemsAsViews.size
                                    )
                                )
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
    }
}

class CommitVenta : Fragment() {

    private val printingService = find<CustomApplicationContextWrapper>().context.getBean(RecipePrintingService::class.java)

    private val empleadoController = find<EmpleadoController>()
    private val clienteController = find<ClienteController>()
    private val ventaController = find<VentaController>()
    private val model = VentaModel()

    @Suppress("UNCHECKED_CAST")
    private val uncommittedItemsAsViews: ObservableList<ItemVentaComponent> = params["observableList"] as ObservableList<ItemVentaComponent>
    private val papi: PuntoDeVentaView = params["papi"] as PuntoDeVentaView
    private val dineroEntregado = params["dineroEntregado"] as SimpleIntegerProperty
    private val valorTotal = params["valorTotal"] as SimpleDoubleProperty
    //private val impresora = SimpleStringProperty(printingService.getPrinters()[0])

    private val imprimirFactura = SimpleStringProperty("Sí")

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["papi"] as UIComponent
        super.onUndock()
    }

    override val root = vbox(spacing = 0, alignment = Pos.CENTER) {
        useMaxSize = true
        prefWidth = 600.0
        label("Checkout") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.greenLabel)
        }
        label("Cambio: $${NumberFormat.getIntegerInstance().format(dineroEntregado.value - valorTotal.value)}").style {
            fontSize = 64.px
        }
        form {
            fieldset {
                field("Empleado") {
                    combobox<Empleado>(model.empleado, empleadoController.empleados).apply {
                        prefWidth = 400.0
                        makeAutocompletable(false)
                    }.validator {
                        when (it) {
                            null -> error("Empleado requerido")
                            else -> null
                        }
                    }
                }
                field("Cliente") {
                    combobox<Cliente>(model.cliente, clienteController.clientes).apply {
                        prefWidth = 400.0
                        makeAutocompletable(false)
                    }.validator {
                        when (it) {
                            null -> error("Cliente requerido")
                            else -> null
                        }
                    }
                }
                field("¿Imprimir factura?") {
                    combobox<String>(imprimirFactura, listOf("Sí", "No")).apply {
                        prefWidth = 400.0
                        makeAutocompletable(false)
                    }
                }
                //field("Impresora seleccionada") {
                //    combobox(impresora, printingService.getPrinters()).apply {
                //        prefWidth = 400.0
                //        makeAutocompletable(false)
                //    }
                //}

                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(
                            MainStylesheet.coolBaseButton,
                            MainStylesheet.greenButton,
                            MainStylesheet.expandedButton
                        )
                        action {
                            model.commit {
                                // Persistence logic
                                ventaController.add(
                                    Venta(
                                        null,
                                        LocalDateTime.now(),
                                        (if (valorTotal.value % 50 < 25) Math.floor(valorTotal.value / 50) * 50 else Math.ceil(valorTotal.value / 50) * 50).toInt(),
                                        dineroEntregado.value,
                                        model.empleado.value,
                                        model.cliente.value
                                    ),
                                    uncommittedItemsAsViews.map {
                                        UncommittedItemVenta(
                                            it.producto,
                                            it.cantidad.value
                                        )
                                    }
                                )

                                // Print recipe
                                //if (imprimirFactura.value == "Sí")
                                //    printingService.printRecipe(res, impresora.value)

                                uncommittedItemsAsViews.clear()

                                uncommittedItemsAsViews.clear()
                                papi.addAlwaysFocusListener()
                                valorTotal.set(0.0)
                                dineroEntregado.set(0)
                                close()
                            }
                        }
                    }
                }
            }
        }
    }
}
