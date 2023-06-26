package xyz.acevedosharp.views.screens

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.control.DateTimePicker
import xyz.acevedosharp.controllers.ClienteController
import xyz.acevedosharp.controllers.ReportesController
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.helpers.CurrentModule.REPORTES
import xyz.acevedosharp.views.shared_components.SideNavigation
import java.time.LocalDateTime

class ReporteScreen : View("Epic POS - Reportes") {
    private val reportesController = find<ReportesController>()

    private var contentContainer: HBox by singleAssign()
    private var dayOrRangeSelectorInjector: HBox by singleAssign()

    private val reportType = SimpleStringProperty("Productos")

    private val reportRange = SimpleStringProperty("")

    private val selectedDay = SimpleObjectProperty<LocalDateTime>()

    private val startDate = SimpleStringProperty("")
    private val startDates = FXCollections.observableArrayList<String>()

    private val endDate = SimpleStringProperty("")
    private val endDates = FXCollections.observableArrayList<String>()

    init {
        val daySelector = vbox {
            label("Día").apply { addClass(MainStylesheet.searchLabel) }
            add(DateTimePicker().apply {
                this.format = "dd/MM/yyyy"
                dateTimeValueProperty().bindBidirectional(selectedDay)
                prefWidth = 250.0
            })
        }

        val monthlyStartRangeSelector = vbox {
            label("Inicio").apply { addClass(MainStylesheet.searchLabel) }
            combobox(
                property = startDate,
                values = startDates
            ) {
                prefWidth = 250.0
            }
        }

        val monthlyEndRangeSelector = vbox {
            label("Fin").apply { addClass(MainStylesheet.searchLabel) }
            hiddenWhen { startDate.isEqualTo("") }
            combobox(
                property = endDate,
                values = endDates
            ) {
                prefWidth = 250.0
            }
        }

        reportRange.onChange { currentReportRange ->
            try {
                when (currentReportRange) {
                    "Diario" -> {
                        dayOrRangeSelectorInjector.children.setAll(
                            daySelector
                        )
                        startDate.value = ""
                        endDate.value = ""
                    }
                    "Mensual" -> {
                        dayOrRangeSelectorInjector.children.setAll(
                            monthlyStartRangeSelector, monthlyEndRangeSelector
                        )
                        startDates.setAll(reportesController.getMonthlyStartDates())
                        selectedDay.value = null
                    }
                    else -> throw IllegalStateException("$currentReportRange shouldn't be achievable.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        startDate.onChange { currentStartDate ->
            if (!currentStartDate.isNullOrBlank()) {
                endDates.setAll(reportesController.getEndDates(currentStartDate))
            }
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
                        label("Tipo de Reporte").apply { addClass(MainStylesheet.searchLabel) }
                        combobox(
                            property = reportType,
                            values = FXCollections.observableArrayList("Productos", "Ventas por Empleado")
                        ) {
                            prefWidth = 350.0
                        }
                    }

                    vbox {
                        label("Rango del reporte").apply { addClass(MainStylesheet.searchLabel) }
                        combobox(
                            property = reportRange,

                            values = listOf("Diario", "Mensual")
                        ) {
                            prefWidth = 250.0
                        }
                    }

                    dayOrRangeSelectorInjector = hbox {
                        style { spacing = 10.px }
                    }

                    rectangle(width = 32, height = 0)

                    button("Generar Reporte") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        style {
                            fontSize = 24.px
                        }
                        action {
                            contentContainer.children.clear()
                            if ("Productos" == reportType.value) {
                                contentContainer.children.setAll(
                                    reportesController.generateProductReport(
                                        reportRange.value,
                                        startDate.value,
                                        endDate.value,
                                        selectedDay.value
                                    )
                                )
                            } else {
                                contentContainer.children.setAll(
                                    reportesController.generateEmployeeSalesReport(
                                        reportRange.value,
                                        startDate.value,
                                        endDate.value,
                                        selectedDay.value
                                    )
                                )
                            }
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
