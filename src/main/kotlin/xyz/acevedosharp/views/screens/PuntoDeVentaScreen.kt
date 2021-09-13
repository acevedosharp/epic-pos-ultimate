@file:Suppress("UNCHECKED_CAST", "ClassName")

package xyz.acevedosharp.views.screens

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.*
import xyz.acevedosharp.views.dialogs.CodigoNotRecognizedDialog
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.helpers.CurrentModule
import xyz.acevedosharp.views.helpers.RecipePrintingService
import xyz.acevedosharp.views.shared_components.ItemVentaComponent
import xyz.acevedosharp.views.shared_components.SideNavigation
import javafx.beans.binding.Bindings
import javafx.beans.property.IntegerProperty
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
import xyz.acevedosharp.GlobalHelper
import xyz.acevedosharp.Joe
import xyz.acevedosharp.controllers.*
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.persistence.entities.EmpleadoDB
import xyz.acevedosharp.views.dialogs.GenericApplicationException
import xyz.acevedosharp.views.dialogs.UnexpectedErrorDialog
import java.text.NumberFormat
import java.time.LocalDateTime

class PuntoDeVentaView : View("Epic POS - Punto de Venta") {
    class CurrentUncommittedIVS {
        val ivs: ObservableList<ItemVentaComponent> = FXCollections.observableArrayList()

        val total = SimpleIntegerProperty(0)

        val totalDisplay = SimpleStringProperty("0")

        init {
            ivs.onChange {
                recalculateTotal()
            }
        }

        fun removeByCodigo(barCode: String) {
            var index = -1
            ivs.forEachIndexed { i, it ->
                if (it.producto.codigo == barCode) {
                    index = i
                    return@forEachIndexed
                }
            }
            ivs.removeAt(index)
        }

        fun flush() {
            ivs.clear()
        }

        fun recalculateTotal() {
            if (ivs.size != 0) {
                var accountTotal = 0.0
                for (iv in ivs) {
                    val ivTotal = iv.producto.precioVenta * iv.cantidad.value.toDouble()

                    accountTotal += ivTotal
                }

                total.value = ((accountTotal - 1) + (50 - ((accountTotal - 1) % 50))).toInt()

                totalDisplay.value = NumberFormat.getIntegerInstance().format(total.value)
            } else {
                total.value = 0
                totalDisplay.value = "0"
            }
        }
    }

    private val productoController = find<ProductoController>()

    private lateinit var scene: Scene
    private lateinit var listener: ChangeListener<Node>

    private val uncommittedItems: ObservableList<Node> = FXCollections.observableArrayList()
    private val dineroEntregado = SimpleIntegerProperty()
    private val currentCodigo = SimpleStringProperty()

    private val currentUncommittedIVS = CurrentUncommittedIVS()

    private lateinit var currentCodigoTextField: TextField

    init {
        productoController.updateSnapshot()
        currentUncommittedIVS.flush()

        currentUncommittedIVS.ivs.onChange {
            uncommittedItems.clear()
            uncommittedItems.addAll(currentUncommittedIVS.ivs.map { it.root })
        }

        // Let's hope the scene doesn't take longer than this to load - probably not, 650ms is a lot of time
        runLater(Duration.millis(600.0)) {
            currentCodigoTextField.requestFocus()
            scene = this.currentStage!!.scene
            listener = ChangeListener<Node> { _, _, _ ->
                if (!currentCodigoTextField.isFocused)
                    currentCodigoTextField.requestFocus()
            }
            addAlwaysFocusListener()
        }
    }

    fun clearAllIVS() {
        currentUncommittedIVS.ivs.clear()
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(CurrentModule.PUNTO_DE_VENTA, this@PuntoDeVentaView))
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
                            if (currentCodigo.value in currentUncommittedIVS.ivs.map { it.producto.codigo }) {
                                val res = currentUncommittedIVS.ivs.find { it.producto.codigo == currentCodigo.value }!!
                                res.cantidad.value = res.cantidad.value + 1
                            } else if (productoController.getProductosWithUpdate(codigoQuery = currentCodigo.value).size > 0) {
                                currentUncommittedIVS.ivs.add(
                                    ItemVentaComponent(
                                        UncommittedItemVenta(
                                            productoController.findByCodigo(currentCodigo.value),
                                            1
                                        ),
                                        currentUncommittedIVS,
                                        this@PuntoDeVentaView
                                    )
                                )
                            } else {
                                openInternalWindow<CodigoNotRecognizedDialog>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf("owner" to this@PuntoDeVentaView)
                                )
                            }
                            currentCodigo.set("")
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
                            openInternalWindow<SelectProductoDialog>(
                                closeButton = false,
                                modal = true,
                                params = mapOf(
                                    "owner" to this@PuntoDeVentaView,
                                    "cuivs" to currentUncommittedIVS,
                                    "disableCodigoSearch" to true
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
                    text(Bindings.concat("Total: $", currentUncommittedIVS.totalDisplay)).style {
                        fontSize = 54.px
                    }

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
                    hbox(spacing = 5) {
                        button {
                            prefWidth = 80.0
                            prefHeight = 130.0

                            graphic = imageview("images/bag.png") {
                                fitWidth = 72.0
                                fitHeight = 72.0
                            }
                            addClass(MainStylesheet.blueButton)
                            action {
                                openInternalWindow<BolsasSelect>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf(
                                        "cuivs" to currentUncommittedIVS,
                                        "owner" to this@PuntoDeVentaView
                                    )
                                )
                                removeAlwaysFocusListener()
                            }
                        }
                        button("Vender") {
                            addClass(MainStylesheet.greenButton)
                            prefWidth = 260.0
                            prefHeight = 130.0
                            style {
                                fontSize = 40.px
                                fontWeight = FontWeight.BOLD
                                textFill = Color.WHITE
                            }
                            action {
                                if (uncommittedItems.size > 0 && dineroEntregado.value >= currentUncommittedIVS.total.value) {
                                    openInternalWindow<CommitVenta>(
                                        closeButton = false,
                                        modal = true,
                                        params = mapOf(
                                            "cuivs" to currentUncommittedIVS,
                                            "owner" to this@PuntoDeVentaView,
                                            "dineroEntregado" to dineroEntregado,
                                            "valorTotal" to currentUncommittedIVS.total.value.toDouble()
                                        )
                                    )
                                    removeAlwaysFocusListener()
                                } else if (dineroEntregado.value < currentUncommittedIVS.total.value) {
                                    throw GenericApplicationException("El dinero entregado no es suficiente.")
                                }
                            }
                        }
                        button {
                            prefWidth = 80.0
                            prefHeight = 130.0

                            graphic = imageview("images/history.png") {
                                fitWidth = 72.0
                                fitHeight = 72.0
                            }
                            addClass(MainStylesheet.grayButton)
                            action {
                                openInternalWindow<ChooseHistoryRange>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf(
                                        "owner" to this@PuntoDeVentaView
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

    fun addAlwaysFocusListener() {
        currentCodigoTextField.requestFocus()
        scene.focusOwnerProperty().addListener(listener)
    }

    fun removeAlwaysFocusListener() {
        scene.focusOwnerProperty().removeListener(listener)
    }
}

class BolsasSelect : Fragment() {
    private val productoController = find<ProductoController>()

    private val numeroBolsas = SimpleIntegerProperty(1)

    private val currentUncommittedIVS = params["cuivs"] as PuntoDeVentaView.CurrentUncommittedIVS
    private val papi = params["owner"] as PuntoDeVentaView

    override val root = vbox(spacing = 0, alignment = Pos.CENTER) {
        useMaxSize = true
        prefWidth = 600.0
        label("Bolsas") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.grayLabel)
        }
        rectangle(width = 0, height = 24)
        combobox(numeroBolsas, IntRange(1, 7).map { it }.toObservable()) {
            prefWidth = 400.0
            style { fontSize = 32.px }
        }
        rectangle(width = 0, height = 24)
        hbox(spacing = 80, alignment = Pos.CENTER) {
            button("Añadir") {
                addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                action {
                    val producto = productoController.findByCodigo("bolsa")
                    val cantidad = numeroBolsas.value

                    if (producto.codigo in currentUncommittedIVS.ivs.map { it.producto.codigo }) {
                        val res = currentUncommittedIVS.ivs.find { it.producto.codigo == producto.codigo }!!
                        res.cantidad.set(res.cantidad.value + cantidad)
                    } else if (productoController.getProductosWithUpdate(codigoQuery = producto.codigo).size > 0) {
                        currentUncommittedIVS.ivs.add(
                            ItemVentaComponent(
                                UncommittedItemVenta(
                                    producto,
                                    cantidad
                                ),
                                currentUncommittedIVS,
                                papi
                            )
                        )
                    } else {
                        this@BolsasSelect.openInternalWindow(UnexpectedErrorDialog("Debe existir un producto con el código: 'bolsa'"))
                    }
                    papi.addAlwaysFocusListener()
                    close()
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


    override fun onDock() {
        Joe.currentView.setValue(this@BolsasSelect)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}

class CommitVenta : Fragment() {
    private val printingService =
        find<CustomApplicationContextWrapper>().context.getBean(RecipePrintingService::class.java)

    private val empleadoController = find<EmpleadoController>()
    private val clienteController = find<ClienteController>()
    private val ventaController = find<VentaController>()
    private val model = VentaModel()

    private val currentUncommittedIVS = params["cuivs"] as PuntoDeVentaView.CurrentUncommittedIVS
    private val owner: PuntoDeVentaView = params["owner"] as PuntoDeVentaView
    private val dineroEntregado = params["dineroEntregado"] as SimpleIntegerProperty
    private val valorTotal = params["valorTotal"] as Double
    private val impresora =
        if (Joe.rememberPrinter.value == false) SimpleStringProperty(printingService.getPrinters()[0]) else Joe.persistentPrinter
    private val impresoras = FXCollections.observableArrayList<String>()

    private val imprimirFactura = SimpleStringProperty("No")

    override fun onDock() {
        Joe.currentView.setValue(this)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }

    override val root = vbox(spacing = 0, alignment = Pos.CENTER) {
        useMaxSize = true
        prefWidth = 800.0
        label("Checkout") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.greenLabel)
        }
        label("Cambio: $${NumberFormat.getIntegerInstance().format(dineroEntregado.value - valorTotal)}").style {
            fontSize = 64.px
        }
        form {
            fieldset {
                field("Empleado") {
                    combobox<EmpleadoDB>(model.empleado, empleadoController.getEmpleadosWithUpdate()) {
                        prefWidth = 400.0
                        makeAutocompletable(false)
                        style { fontSize = 28.px }
                    }.validator {
                        when (it) {
                            null -> error("Empleado requerido")
                            else -> null
                        }
                    }
                }
                field("Cliente") {
                    combobox<ClienteDB>(model.cliente, clienteController.getClientesWithUpdate()) {
                        prefWidth = 400.0
                        makeAutocompletable(false)
                        style { fontSize = 28.px }
                    }.validator {
                        when (it) {
                            null -> error("Cliente requerido")
                            else -> null
                        }
                    }
                    button("+") {
                        addClass(MainStylesheet.addButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewClienteFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("owner" to this@CommitVenta)
                            )
                        }
                    }
                }
                field("¿Imprimir factura?") {
                    combobox<String>(imprimirFactura, listOf("Sí", "No")) {
                        prefWidth = 400.0
                        makeAutocompletable(false)
                        style { fontSize = 28.px }
                    }
                }
                field("Impresora seleccionada") {
                    hiddenWhen { imprimirFactura.isNotEqualTo("Sí") }
                    combobox(impresora, impresoras).apply {
                        imprimirFactura.onChange {
                            if (it == "Sí")
                                impresoras.setAll(printingService.getPrinters())
                        }

                        prefWidth = 400.0
                        makeAutocompletable(false)
                        style { fontSize = 28.px }
                    }
                    checkbox("¿Recordar?", Joe.rememberPrinter)
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
                                var totalSinIva = 0.0
                                var totalConIva = 0

                                currentUncommittedIVS.ivs.forEach {
                                    val (_, ivaAmount, sellPrice) = GlobalHelper.calculateSellPriceBrokenDown(
                                        it.producto.precioCompra,
                                        it.producto.margen,
                                        it.producto.iva
                                    )

                                    totalSinIva += (sellPrice - ivaAmount) * it.cantidad.value.toDouble()
                                    totalConIva += sellPrice.toInt() * it.cantidad.value
                                }

                                // Persistence logic
                                val res = ventaController.add(
                                    Venta(
                                        null,
                                        LocalDateTime.now(),
                                        totalSinIva,
                                        dineroEntregado.value,
                                        model.empleado.value,
                                        model.cliente.value,
                                        totalConIva
                                    ),
                                    currentUncommittedIVS.ivs.map {
                                        UncommittedItemVenta(
                                            it.producto,
                                            it.cantidad.value
                                        )
                                    }
                                )

                                //Print recipe
                                if (imprimirFactura.value == "Sí")
                                    printingService.printRecipe(res, impresora.value)
                                currentUncommittedIVS.flush()
                                owner.addAlwaysFocusListener()
                                dineroEntregado.set(0)

                                // remember printer selection
                                if (Joe.rememberPrinter.value) {
                                    Joe.persistentPrinter.set(impresora.value)
                                }
                                close()
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(
                            MainStylesheet.coolBaseButton,
                            MainStylesheet.redButton,
                            MainStylesheet.expandedButton
                        )
                        action {
                            owner.addAlwaysFocusListener()
                            close()
                        }
                    }
                }
            }
        }
    }
}

class ModifyItemVentaQuantityDialog : Fragment() {
    private val papi = params["owner"] as UIComponent
    private val targetQuantityProperty = params["targetProperty"] as IntegerProperty
    private val pdvView = params["pdvView"] as PuntoDeVentaView

    private var hasUsedAnyTouchButton = false

    private lateinit var cantidadTextField: TextField

    override val root = vbox(alignment = Pos.TOP_CENTER) {
        style {
            backgroundColor += c(0, 0, 0, 0.8)
            padding = box(6.px)
            prefWidth = 342.px
        }
        vbox(alignment = Pos.CENTER) {
            cantidadTextField = textfield(targetQuantityProperty) {
                alignment = Pos.CENTER
                style {
                    fontSize = 40.px
                }
                action {
                    pdvView.addAlwaysFocusListener()
                    close()
                }
            }

            style {
                backgroundColor += c("#ffffff")
            }
        }
        rectangle(width = 0, height = 6)
        hbox(alignment = Pos.TOP_CENTER) {
            button("1") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(1) }
            }
            button("2") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(2) }
            }
            button("3") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(3) }
            }
        }
        hbox(alignment = Pos.TOP_CENTER) {
            button("4") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(4) }
            }
            button("5") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(5) }
            }
            button("6") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(6) }
            }
        }
        hbox(alignment = Pos.TOP_CENTER) {
            button("7") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(7) }
            }
            button("8") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(8) }
            }
            button("9") {
                cantidadTextField.requestFocus()
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(9) }
            }
        }
        hbox(alignment = Pos.TOP_CENTER) {
            button("←") {
                addClass(MainStylesheet.redButton, MainStylesheet.keyButtonSmall)
                action {
                    val s = targetQuantityProperty.value.toString()
                    if (s.length > 1)
                        targetQuantityProperty.set(s.substring(0, s.length - 1).toInt())
                    else
                        targetQuantityProperty.set(0)
                }
            }
            button("0") {
                addClass(MainStylesheet.grayButton, MainStylesheet.keyButtonSmall)
                action { pressTouchNumberWithMiddleware(0) }
            }
            button("Ok") {
                addClass(MainStylesheet.greenButton, MainStylesheet.keyButtonSmall)
                action {
                    pdvView.addAlwaysFocusListener()
                    close()
                }
            }
        }
    }

    private fun pressTouchNumberWithMiddleware(n: Int) {
        if (!hasUsedAnyTouchButton) {
            targetQuantityProperty.set(0)
        }

        hasUsedAnyTouchButton = true

        try {
            targetQuantityProperty.set("${targetQuantityProperty.value}$n".toInt())
        } catch (e: NumberFormatException) {
            targetQuantityProperty.set(0)
        }
    }

    override fun onDock() {
        Joe.currentView.setValue(this@ModifyItemVentaQuantityDialog)
        super.onDock()

        // we want this to be fast, but it might fail with short times
        arrayOf(100.0, 200.0, 300.0, 400.0, 500.0, 600.0).forEach {
            runLater(Duration.millis(it)) {
                cantidadTextField.requestFocus()
            }
        }
    }

    override fun onUndock() {
        Joe.currentView.setValue(papi)
        super.onUndock()
    }
}
