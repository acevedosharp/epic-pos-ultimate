package xyz.acevedosharp.views.screens

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.TableView
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import tornadofx.*
import xyz.acevedosharp.Joe
import xyz.acevedosharp.controllers.ProductoController
import xyz.acevedosharp.controllers.ReportesController
import xyz.acevedosharp.ui_models.Cliente
import xyz.acevedosharp.ui_models.Producto
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.helpers.CurrentModule.REPORTES
import xyz.acevedosharp.views.shared_components.SideNavigation

class ReporteView : View("Módulo de Reportes") {

    private val reportesController = find<ReportesController>()
    private val productoController = find<ProductoController>()

    private var contentContainer: HBox by singleAssign()
    private val view = this
    private val reportType = SimpleStringProperty("")
    private val productQuantity = SimpleStringProperty("")
    private val selectedProduct = SimpleObjectProperty<Producto>()

    private val startDate = SimpleStringProperty("")
    private val startDates = FXCollections.observableArrayList<String>()

    private val endDate = SimpleStringProperty("")
    private val endDates = FXCollections.observableArrayList<String>()

    init {
        Joe.currentView = view

        reportType.onChange { currentValue ->
            startDates.setAll( reportesController.getStartDates(currentValue!!) )
        }

        startDate.onChange { currentValue ->
            endDates.setAll( reportesController.getEndDates(reportType.value, currentValue!!) )
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(REPORTES, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true

                    vbox {
                        label("Tipo de reporte").apply { addClass(MainStylesheet.searchLabel) }
                        combobox(
                            property = reportType,
                            values = listOf("Ventas", "Pedidos")
                        ) {
                            prefWidth = 200.0
                        }
                    }

                    vbox {
                        hiddenWhen { reportType.isEqualTo("") }
                        label("Cantidad de productos").apply { addClass(MainStylesheet.searchLabel) }
                        combobox(
                            property = productQuantity,
                            values = listOf("Todos los productos", "Un producto")
                        ) {
                            prefWidth = 300.0
                        }
                    }

                    vbox {
                        hiddenWhen { productQuantity.isNotEqualTo("Un producto") }
                        label("Selecciona un producto").apply { addClass(MainStylesheet.searchLabel) }
                        combobox<Producto>(
                            property = selectedProduct,
                            values = productoController.productos
                        ) {
                            prefWidth = 400.0
                            makeAutocompletable(true)
                        }
                    }

                    vbox {
                        label("Inicio").apply { addClass(MainStylesheet.searchLabel) }
                        hiddenWhen {
                            // true if valid
                            val caseSingleProductValid = productQuantity.isEqualTo("Un producto").and(selectedProduct.isNotEqualTo(""))
                            val caseAllProductsValid = productQuantity.isEqualTo("Todos los productos")

                            return@hiddenWhen caseSingleProductValid.or(caseAllProductsValid).not()
                        }
                        combobox(
                            property = startDate,
                            values = startDates
                        )
                    }

                    vbox {
                        label("Fin").apply { addClass(MainStylesheet.searchLabel) }
                        hiddenWhen { startDate.isEqualTo("") }
                        combobox(
                            property = endDate,
                            values = endDates
                        )
                    }

                    rectangle(width = 64, height = 0)

                    button("Generar Reporte") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        style {
                            fontSize = 24.px
                        }
                        action {
                            contentContainer.children.setAll(
                                reportesController.generateReport(
                                    reportType.value,
                                    productQuantity.value,
                                    if (selectedProduct.isNull.value) selectedProduct.value else null,
                                    startDate.value,
                                    endDate.value
                                )
                            )
                        }
                    }
                }
            }

            center {
                contentContainer = hbox {

                }
            }

            style {
                backgroundColor += Color.WHITE
            }
        }
    }
}
