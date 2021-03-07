package xyz.acevedosharp.views.screens

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import tornadofx.*
import xyz.acevedosharp.Joe
import xyz.acevedosharp.controllers.ClienteController
import xyz.acevedosharp.controllers.ProductoController
import xyz.acevedosharp.controllers.ReportesController
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.helpers.CurrentModule.REPORTES
import xyz.acevedosharp.views.shared_components.SideNavigation

class ReporteScreen : View("Módulo de Reportes") {
    private val reportesController = find<ReportesController>()
    private val productoController = find<ProductoController>()
    private val clienteController = find<ClienteController>()

    private var contentContainer: HBox by singleAssign()

    private val filterByCliente = SimpleStringProperty("No")
    private val clienteToFilterBy = SimpleObjectProperty<ClienteDB>()

    private val startDate = SimpleStringProperty("")
    private val startDates = FXCollections.observableArrayList<String>()

    private val endDate = SimpleStringProperty("")
    private val endDates = FXCollections.observableArrayList<String>()

    init {
        Joe.currentView = this@ReporteScreen

        startDates.setAll(reportesController.getStartDates())

        startDate.onChange { currentStartDate ->
            endDates.setAll(reportesController.getEndDates(currentStartDate!!))
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(REPORTES, this@ReporteScreen))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true


                    vbox {
                        label("¿Filtrar por cliente específico?").apply { addClass(MainStylesheet.searchLabel) }
                        combobox(
                            property = filterByCliente,
                            values = FXCollections.observableArrayList("Sí", "No")
                        ) {
                            prefWidth = 250.0
                        }
                    }

                    vbox {
                        label("Seleccionar cliente").apply { addClass(MainStylesheet.searchLabel) }
                        combobox(
                            property = clienteToFilterBy,
                            values = clienteController.getClientesWithUpdate()
                        ) {
                            prefWidth = 250.0
                            makeAutocompletable()
                        }
                        hiddenWhen {
                            filterByCliente.isNotEqualTo("Sí")
                        }
                    }

                    vbox {
                        label("Inicio").apply { addClass(MainStylesheet.searchLabel) }
                        combobox(
                            property = startDate,
                            values = startDates
                        ) {
                            prefWidth = 250.0
                        }
                    }

                    vbox {
                        label("Fin").apply { addClass(MainStylesheet.searchLabel) }
                        hiddenWhen { startDate.isEqualTo("") }
                        combobox(
                            property = endDate,
                            values = endDates
                        ) {
                            prefWidth = 250.0
                        }
                    }

                    rectangle(width = 32, height = 0)

                    button("Generar Reporte") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        style {
                            fontSize = 24.px
                        }
                        action {
                            contentContainer.children.setAll(
                                reportesController.generateReport(
                                    filterByCliente.value == "Sí",
                                    clienteToFilterBy.value,
                                    startDate.value,
                                    endDate.value
                                )
                            )
                        }
                    }
                }
            }

            center {
                contentContainer = hbox()
            }

            style {
                backgroundColor += Color.WHITE
            }
        }
    }
}
