package xyz.acevedosharp.views.screens

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*
import tornadofx.control.DateTimePicker
import xyz.acevedosharp.Joe
import xyz.acevedosharp.controllers.VentaController
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.VentaDB
import xyz.acevedosharp.views.MainStylesheet
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.Month
import java.util.*

class ChooseHistoryRange : Fragment() {
    private val startYear = SimpleIntegerProperty(LocalDateTime.now().year)
    private val startMonth = SimpleObjectProperty<NumberToMonth>()
    private val startDay = SimpleIntegerProperty()
    private val startHour = SimpleIntegerProperty()
    private val startMinute = SimpleIntegerProperty()
    private val startDays = FXCollections.observableArrayList<Int>()

    private val endYear = SimpleIntegerProperty(LocalDateTime.now().year)
    private val endMonth = SimpleObjectProperty<NumberToMonth>()
    private val endDay = SimpleIntegerProperty()
    private val endHour = SimpleIntegerProperty()
    private val endMinute = SimpleIntegerProperty()
    private val endDays = FXCollections.observableArrayList<Int>()

    class NumberToMonth(val n: Int, val month: String) {
        override fun toString() = month
    }

    private val numberToMonth = listOf(
        NumberToMonth(0, "Enero"),
        NumberToMonth(1, "Febrero"),
        NumberToMonth(2, "Marzo"),
        NumberToMonth(3, "Abril"),
        NumberToMonth(4, "Mayo"),
        NumberToMonth(5, "Junio"),
        NumberToMonth(6, "Julio"),
        NumberToMonth(7, "Agosto"),
        NumberToMonth(8, "Septiembre"),
        NumberToMonth(9, "Octubre"),
        NumberToMonth(10, "Noviembre"),
        NumberToMonth(11, "Diciembre")
    )

    init {
        startMonth.onChange {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.YEAR, startYear.value)
            calendar.set(Calendar.MONTH, startMonth.value.n)

            startDays.setAll(
                IntRange(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).map { it }
            )
        }

        endMonth.onChange {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.YEAR, endYear.value)
            calendar.set(Calendar.MONTH, endMonth.value.n)

            endDays.setAll(
                IntRange(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).map { it }
            )
        }
    }

    override val root = vbox(spacing = 0, alignment = Pos.TOP_CENTER) {
        useMaxSize = true
        prefWidth = 1200.0
        prefHeight = 800.0
        label("Escoge un rango de tiempo") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.blueLabel)
        }
        hbox(alignment = Pos.CENTER) {
            hgrow = Priority.ALWAYS
            form {
                fieldset {
                    field("Inicio") {
                        hbox(spacing = 5) {
                            vbox {
                                label("Año")
                                textfield(startYear).style { fontSize = 28.px }
                            }
                            vbox {
                                label("Mes")
                                combobox(startMonth, numberToMonth).style { fontSize = 28.px }
                            }
                            vbox {
                                label("Día")
                                combobox(startDay, startDays) {
                                    makeAutocompletable()
                                    enableWhen { startMonth.isNotNull }
                                    style { fontSize = 28.px }
                                }
                            }
                            vbox {
                                label("Hora")
                                combobox(startHour, IntRange(0, 23).map { it }.toObservable()) {
                                    makeAutocompletable()
                                    style { fontSize = 28.px }
                                }
                            }
                            vbox {
                                label("Minuto")
                                combobox(startMinute, IntRange(0, 59).map { it }.toObservable()) {
                                    makeAutocompletable()
                                    style { fontSize = 28.px }
                                }
                            }
                        }
                    }
                    field("Fin") {
                        hbox(spacing = 5) {
                            vbox {
                                label("Año")
                                textfield(endYear).style { fontSize = 28.px }
                            }
                            vbox {
                                label("Mes")
                                combobox(endMonth, numberToMonth).style { fontSize = 28.px }
                            }
                            vbox {
                                label("Día")
                                combobox(endDay, endDays) {
                                    makeAutocompletable()
                                    enableWhen { endMonth.isNotNull }
                                    style { fontSize = 28.px }
                                }
                            }
                            vbox {
                                label("Hora")
                                combobox(endHour, IntRange(0, 23).map { it }.toObservable()) {
                                    makeAutocompletable()
                                    style { fontSize = 28.px }
                                }
                            }
                            vbox {
                                label("Minuto")
                                combobox(endMinute, IntRange(0, 59).map { it }.toObservable()) {
                                    makeAutocompletable()
                                    style { fontSize = 28.px }
                                }
                            }
                        }
                    }

                    hbox(spacing = 80, alignment = Pos.CENTER) {
                        button("Aceptar") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                            action {
                                openInternalWindow<VentaHistory>(
                                    closeButton = false,
                                    modal = true,
                                    params = mapOf(
                                        "owner" to this@ChooseHistoryRange,
                                        "inicio" to Timestamp.valueOf(
                                            LocalDateTime.of(
                                                startYear.value,
                                                Month.of(startMonth.value.n + 1),
                                                startDay.value,
                                                startHour.value,
                                                startMinute.value
                                            )
                                        ),
                                        "fin" to Timestamp.valueOf(
                                            LocalDateTime.of(
                                                endYear.value,
                                                Month.of(endMonth.value.n + 1),
                                                endDay.value,
                                                endHour.value,
                                                endMinute.value
                                            )
                                        ),
                                    )
                                )
                            }
                        }
                        button("Cancelar") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                            action { close() }
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["papi"] as UIComponent
        super.onUndock()
    }
}

class VentaHistory : Fragment() {
    private val ventaController = find<VentaController>()

    private val inicio = params["inicio"] as Timestamp
    private val fin = params["fin"] as Timestamp

    private var table: TableView<VentaDB> by singleAssign()

    init {
        println(inicio)
        println(fin)
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 1100.0
        prefHeight = 700.0
        label("Historial de ventas") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel, MainStylesheet.grayLabel)
        }
        table = tableview(ventaController.getVentasFromTo(inicio, fin).sortedByDescending { it.fechaHora }.toObservable()) {
            readonlyColumn("Fecha y Hora", VentaDB::fechaHora)
            readonlyColumn("Precio Total", VentaDB::precioTotal)
            readonlyColumn("Pago Recibido", VentaDB::pagoRecibido)
            readonlyColumn("Cambio", VentaDB::cambio)
            readonlyColumn("Empleado", VentaDB::empleado)
            readonlyColumn("Cliente", VentaDB::cliente)

            vgrow = Priority.ALWAYS

            smartResize()
        }

        hbox(spacing = 10, alignment = Pos.CENTER) {
            button("Ver detalles") {
                addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton, MainStylesheet.expandedButton)

                disableWhen {
                    table.selectionModel.selectedItemProperty().isNull
                }

                action {
                    openInternalWindow<DetalleVenta>(
                        closeButton = false,
                        modal = true,
                        params = mapOf(
                            "owner" to this@VentaHistory,
                            "venta" to table.selectedItem!!
                        )
                    )
                }
            }
            button("Cerrar") {
                addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                action { close() }
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
}

class DetalleVenta : Fragment() {
    private val venta = params["venta"] as VentaDB

    override val root = vbox(spacing = 10) {
        prefWidth = 700.0

        tableview(venta.items.toList().toObservable()) {
            readonlyColumn("Producto", ItemVentaDB::producto).pctWidth(60)
            readonlyColumn("Cantidad", ItemVentaDB::cantidad).pctWidth(15)
            readonlyColumn("P. Unidad", ItemVentaDB::precioVenta).pctWidth(25)

            smartResize()
        }

        hbox(alignment = Pos.CENTER) {
            button("Cerrar") {
                addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                action { close() }
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
}
