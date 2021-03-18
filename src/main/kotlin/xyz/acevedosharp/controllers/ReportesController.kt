@file:Suppress("DEPRECATION", "JoinDeclarationAndAssignment")

package xyz.acevedosharp.controllers

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.persistence.entities.FamiliaDB
import xyz.acevedosharp.persistence.entities.ProductoDB
import xyz.acevedosharp.persistence.repositories.ItemVentaRepo
import xyz.acevedosharp.persistence.repositories.ProductoRepo
import xyz.acevedosharp.persistence.repositories.VentaRepo
import xyz.acevedosharp.views.dialogs.GenericApplicationException
import xyz.acevedosharp.views.MainStylesheet
import java.sql.Timestamp
import java.text.NumberFormat
import java.util.*

class ReportesController : Controller() {
    private val monthYearSeparator = ", "
    private val numberToMonth = hashMapOf(
        1 to "Enero",
        2 to "Febrero",
        3 to "Marzo",
        4 to "Abril",
        5 to "Mayo",
        6 to "Junio",
        7 to "Julio",
        8 to "Agosto",
        9 to "Septiembre",
        10 to "Octubre",
        11 to "Noviembre",
        12 to "Diciembre"
    )

    private val monthToNumber = numberToMonth.map { it.value to it.key }.toMap()

    private val searchByFamilia = SimpleObjectProperty<FamiliaDB>()

    private val productoRepo = find<CustomApplicationContextWrapper>().context.getBean(ProductoRepo::class.java)
    private val ventaRepo = find<CustomApplicationContextWrapper>().context.getBean(VentaRepo::class.java)
    private val itemVentaRepo = find<CustomApplicationContextWrapper>().context.getBean(ItemVentaRepo::class.java)
    private val familiaController = find<FamiliaController>()

    fun generateReport(filterByCliente: Boolean, clienteToFilterBy: ClienteDB?, startDate: String, endDate: String): VBox {

        val startDateMonthYear = decodeMonthAndYearRaw(startDate)
        val startCalendar = Calendar.getInstance()
        startCalendar.set(Calendar.MONTH, startDateMonthYear.first - 1)
        startCalendar.set(Calendar.YEAR, startDateMonthYear.second)
        startCalendar.set(Calendar.DATE, 1)
        startCalendar.set(Calendar.HOUR_OF_DAY, 0)
        startCalendar.set(Calendar.MINUTE, 0)
        startCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)
        val startDateTimestamp = Timestamp(startCalendar.timeInMillis)

        val endDateMonthYear = decodeMonthAndYearRaw(endDate)
        val endCalendar = Calendar.getInstance()
        endCalendar.set(Calendar.YEAR, endDateMonthYear.second)
        endCalendar.set(Calendar.MONTH, endDateMonthYear.first - 1) // gregorian calendar months are 0-11
        endCalendar.set(Calendar.DATE, endCalendar.getActualMaximum(Calendar.DATE))
        endCalendar.set(Calendar.HOUR_OF_DAY, 23)
        endCalendar.set(Calendar.MINUTE, 59)
        endCalendar.set(Calendar.SECOND, 59)
        endCalendar.set(Calendar.MILLISECOND, 999)
        val endDateTimestamp = Timestamp(endCalendar.timeInMillis)


        val data = arrayListOf<RankingReportDisplay>()
        var bagQuantity = 0
        var totalSales = 0

        val products = productoRepo.findAll()

        var totalNumberOfSales = 0

        products.forEach { producto ->
            val matchingSoldItems = if (filterByCliente) {
                itemVentaRepo.findAllByProductoEqualsAndFechaHoraBetweenAndClienteEquals(
                    producto,
                    startDateTimestamp,
                    endDateTimestamp,
                    clienteToFilterBy!!
                )
            } else { // don't filter by cliente
                itemVentaRepo.findAllByProductoEqualsAndFechaHoraBetween(
                    producto,
                    startDateTimestamp,
                    endDateTimestamp
                )
            }

            totalNumberOfSales += matchingSoldItems.size

            totalSales = matchingSoldItems.groupBy { it.venta }.size

            if (producto.codigo != "bolsa") {
                var amountSold = 0
                var soldQuantity = 0

                matchingSoldItems.forEach {
                    amountSold += it.cantidad * it.precioVenta
                    soldQuantity += it.cantidad
                }

                data.add(
                    RankingReportDisplay(
                        producto.descripcionLarga,
                        producto.precioVenta,
                        amountSold,
                        producto.margen,
                        0.0,
                        soldQuantity,
                        amountSold * (producto.margen / 100),
                        0.0,
                        producto
                    )
                )
            } else {
                matchingSoldItems.forEach {
                    bagQuantity += it.cantidad
                }
            }
        }

        // This case can only be produced when a registered client hasn't bought anything, if there are no sales in general
        // (only once in the POS' lifetime) the tab wouldn't even open
        if (totalNumberOfSales == 0) {
            throw GenericApplicationException("El cliente ${clienteToFilterBy!!.nombre} no ha realizado compras en el periodo de tiempo indicado.")
        } else {
            var totalAmountSold = 0.0
            var totalAmountEarned = 0.0

            data.forEach {
                totalAmountSold += it.amountSold
                totalAmountEarned += it.amountEarned
            }

            data.forEach {
                it.percentageSold = (it.amountSold / totalAmountSold) * 100
                it.percentageEarned = (it.amountEarned / totalAmountEarned) * 100
            }

            return object : View() {
                private var table: TableView<RankingReportDisplay> by singleAssign()

                override val root = vbox {
                    hgrow = Priority.ALWAYS
                    paddingAll = 6
                    style {
                        backgroundColor += Color.WHITE
                    }

                    hbox(alignment = Pos.CENTER) {
                        label("Reporte de: ").style { fontSize = 36.px }
                        label(startDate).style { fontSize = 36.px; fontWeight = FontWeight.EXTRA_BOLD }
                        label(" hasta ").style { fontSize = 36.px }
                        label(endDate).style { fontSize = 36.px; fontWeight = FontWeight.EXTRA_BOLD }
                        if (filterByCliente) {
                            label(" - Cliente: ").style { fontSize = 36.px }
                            label(clienteToFilterBy!!.nombre).style { fontSize = 36.px; fontWeight = FontWeight.EXTRA_BOLD }
                        }

                        hgrow = Priority.ALWAYS
                        style {
                            backgroundColor += c("#A7C8DB")
                        }
                    }

                    table = tableview(data.toObservable()) {
                        readonlyColumn("Descripción Larga", RankingReportDisplay::longDesc)
                        readonlyColumn("PdV actual", RankingReportDisplay::sellPrice)
                        readonlyColumn("$ de ventas", RankingReportDisplay::amountSold)
                        readonlyColumn("Margen", RankingReportDisplay::margen)
                        readonlyColumn("% de ventas", RankingReportDisplay::percentageSoldStr)
                        readonlyColumn("Unds vendidas", RankingReportDisplay::soldQuantity)
                        readonlyColumn("$ de ganancias", RankingReportDisplay::amountEarned)
                        readonlyColumn("% de las ganancias", RankingReportDisplay::percentageEarnedStr)
                        smartResize()
                        hgrow = Priority.ALWAYS
                        vgrow = Priority.ALWAYS
                    }

                    borderpane {
                        left {
                            vbox(spacing = 6, alignment = Pos.CENTER_LEFT) {
                                prefHeight = 150.0

                                hbox {
                                    label("Ventas totales: ").style { fontSize = 24.px }
                                    label("$${NumberFormat.getIntegerInstance().format(totalAmountSold)}").style {
                                        textFill = Color.GREEN
                                        fontSize = 24.px
                                        fontWeight = FontWeight.BOLD
                                    }
                                }

                                hbox {
                                    label("Ganancias totales: ").style { fontSize = 24.px }
                                    label("$${NumberFormat.getIntegerInstance().format(totalAmountEarned)} (${(totalAmountEarned/totalAmountSold)*100}%)").style {
                                        textFill = Color.GREEN
                                        fontSize = 24.px
                                        fontWeight = FontWeight.BOLD
                                    }
                                }
                                hbox {
                                    label("Número de bolsas: ").style { fontSize = 24.px }
                                    label(bagQuantity.toString()).style {
                                        textFill = Color.GREEN
                                        fontSize = 24.px
                                        fontWeight = FontWeight.BOLD
                                    }
                                }
                                hbox {
                                    label("Número de ventas: ").style { fontSize = 24.px }
                                    label("$totalSales (promedio de $${NumberFormat.getIntegerInstance().format(totalAmountSold / totalSales)} por venta)").style {
                                        textFill = Color.GREEN
                                        fontSize = 24.px
                                        fontWeight = FontWeight.BOLD
                                    }
                                }
                            }
                        }

                        center {
                            vbox(spacing = 10, alignment = Pos.CENTER) {
                                hbox(spacing = 20, alignment = Pos.CENTER) {
                                    vbox {
                                        label("Filtrar por descripción").style { fontSize = 18.px }
                                        textfield {

                                            textProperty().onChange { searchString ->
                                                table.items = data.filter {
                                                    it.producto.descripcionLarga.toLowerCase().contains(searchString!!.toLowerCase()) ||
                                                            it.producto.descripcionCorta.toLowerCase().contains(searchString.toLowerCase())
                                                }.toObservable()
                                            }
                                            style { fontSize = 24.px }
                                        }

                                        minWidth = 400.0
                                        maxWidth = 400.0
                                        prefWidth = 400.0
                                    }

                                    hbox(spacing = 10, alignment = Pos.CENTER) {
                                        vbox {
                                            label("Buscar por familia").style { fontSize = 18.px }
                                            combobox<FamiliaDB>(searchByFamilia, familiaController.getFamiliasWithUpdate()).apply {
                                                prefWidth = 300.0
                                                makeAutocompletable(false)

                                                valueProperty().onChange { searchFamilia ->
                                                    if (searchFamilia != null) {
                                                        table.items = data.filter {
                                                            it.producto.familia.familiaId == searchFamilia.familiaId
                                                        }.toObservable()
                                                    } else {
                                                        table.items = data.toObservable()
                                                    }
                                                }
                                            }
                                            prefWidth = 250.0
                                        }
                                        button("Quitar filtro") {
                                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton)
                                            action { searchByFamilia.value = null }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }.root
        }
    }

    fun getStartDates(): List<String> {

        val oldestDate: Timestamp = ventaRepo.findFirstByOrderByVentaIdAsc()?.fechaHora ?: throw GenericApplicationException("No existe ninguna venta registrada!")
        val newestDate: Timestamp = ventaRepo.findFirstByOrderByVentaIdDesc()?.fechaHora ?: throw GenericApplicationException("No existe ninguna venta registrada!")

        val startDates = arrayListOf<String>()

        IntRange(oldestDate.year, newestDate.year).forEach { currentYear ->

            if (currentYear == oldestDate.year && oldestDate.year != newestDate.year) {

                IntRange(oldestDate.month, 11).forEach { currentMonth ->
                    startDates.add(encodeMonthAndYearRaw(currentMonth, currentYear))
                }

            } else if (currentYear == oldestDate.year && oldestDate.year == newestDate.year) {

                IntRange(oldestDate.month, newestDate.month).forEach { currentMonth ->
                    startDates.add(encodeMonthAndYearRaw(currentMonth, currentYear))
                }

            } else if (currentYear == newestDate.year && currentYear != oldestDate.year) {

                IntRange(0, newestDate.month).forEach { currentMonth ->
                    startDates.add(encodeMonthAndYearRaw(currentMonth, currentYear))
                }

            } else {

                IntRange(0, 11).forEach { currentMonth ->
                    startDates.add(encodeMonthAndYearRaw(currentMonth, currentYear))
                }

            }
        }
        return startDates
    }

    fun getEndDates(startDate: String): List<String> {
        val startMonthYear = decodeMonthAndYearRaw(startDate)

        return getStartDates().filter {
            val endMonthYear = decodeMonthAndYearRaw(it)

            return@filter endMonthYear.second >= startMonthYear.second && endMonthYear.first >= startMonthYear.first
        }
    }

    private fun encodeMonthAndYearRaw(month: Int, year: Int): String {
        return "${year + 1900}${monthYearSeparator}${numberToMonth[month + 1]}"
    }

    private fun decodeMonthAndYearRaw(str: String): Pair<Int, Int> {
        val split = str.split(monthYearSeparator)

        return monthToNumber[split[1]]!! to split[0].toInt()
    }


    class RankingReportDisplay(
        val longDesc: String,
        val sellPrice: Int,
        val amountSold: Int,
        val margen: Double,
        var percentageSold: Double,
        val soldQuantity: Int,
        val amountEarned: Double,
        var percentageEarned: Double,
        val producto: ProductoDB
    ) {
        val percentageSoldStr: String
            get() = percentageSold.toBigDecimal().toPlainString()

        val percentageEarnedStr: String
            get() = percentageEarned.toBigDecimal().toPlainString()
    }
}
