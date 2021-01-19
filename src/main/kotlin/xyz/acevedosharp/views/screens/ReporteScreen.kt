package xyz.acevedosharp.views.screens

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import tornadofx.*
import xyz.acevedosharp.Joe
import xyz.acevedosharp.controllers.ProductoController
import xyz.acevedosharp.controllers.ReportesController
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.helpers.CurrentModule.REPORTES
import xyz.acevedosharp.views.shared_components.SideNavigation

class ReporteScreen : View("Módulo de Reportes") {

    private val reportesController = find<ReportesController>()
    private val productoController = find<ProductoController>()

    private var contentContainer: HBox by singleAssign()
    private val view = this

    private val startDate = SimpleStringProperty("")
    private val startDates = FXCollections.observableArrayList<String>()

    private val endDate = SimpleStringProperty("")
    private val endDates = FXCollections.observableArrayList<String>()

    init {
        Joe.currentView = view

        startDates.setAll(reportesController.getStartDates())

        startDate.onChange { currentStartDate ->
            endDates.setAll(reportesController.getEndDates(currentStartDate!!))
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
                        label("Inicio").apply { addClass(MainStylesheet.searchLabel) }
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

                    rectangle(width = 32, height = 0)

                    button("Generar Reporte") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        style {
                            fontSize = 24.px
                        }
                        action {
                            contentContainer.children.setAll(
                                reportesController.generateReport(
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
