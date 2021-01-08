package xyz.acevedosharp.views.screens

import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import tornadofx.*
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.helpers.CurrentModule.REPORTES
import xyz.acevedosharp.views.shared_components.SideNavigation

class ReporteView : View("Módulo de Reportes") {

    private val view = this
    private val reportType = SimpleStringProperty("")
    private val productQuantity = SimpleStringProperty("")
    private val selectedProduct = SimpleStringProperty("")
    private val startDate = SimpleStringProperty("")
    private val endDate = SimpleStringProperty("")

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
                        combobox(
                            property = selectedProduct,
                            values = listOf("Prod1", "Prod2", "Prod3", "Prod4")
                        )
                    }

                        vbox {
                            hiddenWhen {
                                // true if valid
                                val caseSingleProductValid = productQuantity.isEqualTo("Un producto").and(selectedProduct.isNotEqualTo(""))
                                val caseAllProductsValid = productQuantity.isEqualTo("Todos los productos")

                                return@hiddenWhen caseSingleProductValid.or(caseAllProductsValid).not()
                            }
                            label("Inicio").apply { addClass(MainStylesheet.searchLabel) }
                            combobox(
                                property = startDate,
                                values = listOf("2020, Noviembre", "2020, Diciembre", "2021, Enero", "2021, Febrero")
                            )
                        }

                    vbox {
                        hiddenWhen { startDate.isEqualTo("") }
                        label("Fin").apply { addClass(MainStylesheet.searchLabel) }
                        combobox(
                            property = endDate,
                            values = listOf("2020, Noviembre", "2020, Diciembre", "2021, Enero", "2021, Febrero")
                        )
                    }

                    rectangle(width = 64, height = 0)

                    button("Generar Reporte") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        style {
                            fontSize = 24.px
                        }
                        action {
                            openInternalWindow<NewProductoFormView>(closeButton = false, modal = true)
                        }
                    }
                }
            }

            center {
                hbox {

                }
            }

            style {
                backgroundColor += Color.WHITE
            }
        }
    }
}
