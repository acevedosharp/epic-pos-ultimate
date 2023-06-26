@file:Suppress("DEPRECATION", "JoinDeclarationAndAssignment")

package xyz.acevedosharp.controllers

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.util.Callback
import tornadofx.*
import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.GlobalHelper
import xyz.acevedosharp.GlobalHelper.round
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.persistence.entities.FamiliaDB
import xyz.acevedosharp.persistence.entities.ProductoDB
import xyz.acevedosharp.persistence.repositories.ItemVentaRepo
import xyz.acevedosharp.persistence.repositories.ProductoRepo
import xyz.acevedosharp.persistence.repositories.VentaRepo
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.dialogs.GenericApplicationException
import java.sql.Timestamp
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    fun generateReport(
        reportRange: String, // Diario, Mensual
        filterByCliente: Boolean,
        clienteToFilterBy: ClienteDB?,
        startDate: String,
        endDate: String,
        day: LocalDateTime?
    ): VBox {
        val startRangeTimestamp: Timestamp
        val endRangeTimestamp: Timestamp
        if (reportRange == "Mensual") {
            val startCalendar = decodeMonthAndYearRaw(startDate)
            startCalendar.set(Calendar.DATE, 1)
            startCalendar.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH) - 1)
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)
            startCalendar.set(Calendar.MILLISECOND, 0)

            val endCalendar = decodeMonthAndYearRaw(endDate)
            endCalendar.set(Calendar.MONTH, endCalendar.get(Calendar.MONTH) - 1)
            endCalendar.set(Calendar.DATE, endCalendar.getActualMaximum(Calendar.DATE))
            endCalendar.set(Calendar.HOUR_OF_DAY, 23)
            endCalendar.set(Calendar.MINUTE, 59)
            endCalendar.set(Calendar.SECOND, 59)
            endCalendar.set(Calendar.MILLISECOND, 999)

            startRangeTimestamp = Timestamp(startCalendar.timeInMillis)
            endRangeTimestamp = Timestamp(endCalendar.timeInMillis)
        } else {
            val startCalendar = Calendar.getInstance()
            val endCalendar = Calendar.getInstance()

            day!! // no way day is null if reportRange is 'Diario'
            startCalendar.set(Calendar.MONTH, day.monthValue - 1)
            startCalendar.set(Calendar.YEAR, day.year)
            startCalendar.set(Calendar.DATE, day.dayOfMonth)
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)
            startCalendar.set(Calendar.MILLISECOND, 0)

            endCalendar.set(Calendar.MONTH, day.monthValue - 1)
            endCalendar.set(Calendar.YEAR, day.year)
            endCalendar.set(Calendar.DATE, day.dayOfMonth)
            endCalendar.set(Calendar.HOUR_OF_DAY, 23)
            endCalendar.set(Calendar.MINUTE, 59)
            endCalendar.set(Calendar.SECOND, 59)
            endCalendar.set(Calendar.MILLISECOND, 999)

            startRangeTimestamp = Timestamp(startCalendar.timeInMillis)
            endRangeTimestamp = Timestamp(endCalendar.timeInMillis)
        }

        val data = arrayListOf<RankingReportDisplay>()
        var bagQuantity = 0

        val products = productoRepo.findAll()

        val totalNumberOfSales = ventaRepo.countAllByFechaHoraBetween(startRangeTimestamp, endRangeTimestamp)

        val aggregatedSaleItems =
            itemVentaRepo.findAllAggregatedByProductoAndFechaHoraBetween(startRangeTimestamp, endRangeTimestamp)
                .associateBy { it.getProduct() }

        products.forEach { p ->
            val aggregatedSaleItem: ItemVentaRepo.IAggregatedSaleItem = aggregatedSaleItems[p.productoId] ?: object : ItemVentaRepo.IAggregatedSaleItem {
                override fun getProduct() = p.productoId!!
                override fun getQuantity() = 0
                override fun getSalePriceWithoutTax() = 0.0
                override fun getSalePriceWithTax() = 0.0
            }

            if (p.codigo == "bolsa") {
                bagQuantity += aggregatedSaleItem.getQuantity()
                return@forEach
            }

            val (_, ivaAmount, sellPrice) = GlobalHelper.calculateSellPriceBrokenDown(
                p.precioCompra,
                p.margen,
                p.iva
            )

            data.add(
                RankingReportDisplay(
                    longDesc = p.descripcionLarga,
                    sinIva = (sellPrice - ivaAmount).round(2),
                    conIva = p.precioVenta.round(2),
                    sinIvaVolume = aggregatedSaleItem.getSalePriceWithoutTax().round(2),
                    conIvaVolume = aggregatedSaleItem.getSalePriceWithTax().round(2),
                    margin = p.margen,
                    soldQuantity = aggregatedSaleItem.getQuantity(),
                    earningsAmount = (aggregatedSaleItem.getSalePriceWithoutTax() * (p.margen / 100)).round(2),
                    taxAmount = (aggregatedSaleItem.getSalePriceWithTax() - aggregatedSaleItem.getSalePriceWithoutTax()).round(2),
                    percentageTotalEarnings = 0.0,
                    percentageTotalSalesSinIva = 0.0,
                    percentageTotalSalesConIva = 0.0,
                    producto = p
                )
            )
        }

        var totalConIva = 0.0
        var totalSinIva = 0.0
        var totalEarnings = 0.0

        data.forEach {
            totalConIva += it.conIvaVolume
            totalSinIva += it.sinIvaVolume
            totalEarnings += it.earningsAmount
        }

        data.forEach {
            it.percentageTotalEarnings = ((it.earningsAmount / totalEarnings) * 100).round(4)
            it.percentageTotalSalesSinIva = ((it.sinIvaVolume / totalSinIva) * 100).round(4)
            it.percentageTotalSalesConIva = ((it.conIvaVolume / totalConIva) * 100).round(4)
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
                    if (reportRange == "Mensual") {
                        label("Reporte de: ").style { fontSize = 36.px }
                        label(startDate).style { fontSize = 36.px; fontWeight = FontWeight.EXTRA_BOLD }
                        label(" hasta ").style { fontSize = 36.px }
                        label(endDate).style { fontSize = 36.px; fontWeight = FontWeight.EXTRA_BOLD }
                    } else {
                        label("Reporte de: ").style { fontSize = 36.px }
                        label(day!!.format(DateTimeFormatter.ISO_LOCAL_DATE)).style {
                            fontSize = 36.px; fontWeight = FontWeight.EXTRA_BOLD
                        }
                    }
                    if (filterByCliente) {
                        label(" - Cliente: ").style { fontSize = 36.px }
                        label(clienteToFilterBy!!.nombre).style { fontSize = 36.px; fontWeight = FontWeight.EXTRA_BOLD }
                    }

                    hgrow = Priority.ALWAYS
                    style {
                        backgroundColor += c("#A7C8DB")
                    }
                }

                val moneyCF = Callback<TableColumn<RankingReportDisplay, Double>, TableCell<RankingReportDisplay, Double>> {
                    object : TableCell<RankingReportDisplay, Double>() {
                        override fun updateItem(item: Double?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = if (empty) null else String.format("$%,.0f", item!!)
                        }
                    }
                }

                val percentageCF = Callback<TableColumn<RankingReportDisplay, Double>, TableCell<RankingReportDisplay, Double>> {
                    object : TableCell<RankingReportDisplay, Double>() {
                        override fun updateItem(item: Double?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = if (empty) null else String.format("%.2f%%", item!!)
                        }
                    }
                }

                table = tableview(data.toObservable()) {
                    readonlyColumn("Descripción Larga", RankingReportDisplay::longDesc)
                    readonlyColumn("Marg", RankingReportDisplay::margin).apply { cellFactory = percentageCF }
                    readonlyColumn("Sin Iva", RankingReportDisplay::sinIva).apply { cellFactory = moneyCF }
                    readonlyColumn("Con Iva", RankingReportDisplay::conIva).apply { cellFactory = moneyCF }
                    readonlyColumn("Vol. sin Iva", RankingReportDisplay::sinIvaVolume).apply { cellFactory = moneyCF }
                    readonlyColumn("Vol. con Iva", RankingReportDisplay::conIvaVolume).apply { cellFactory = moneyCF }
                    readonlyColumn("Unds", RankingReportDisplay::soldQuantity)
                    readonlyColumn("Ganancias", RankingReportDisplay::earningsAmount).apply { cellFactory = moneyCF }
                    readonlyColumn("Impuestos", RankingReportDisplay::taxAmount).apply { cellFactory = moneyCF }
                    readonlyColumn("% ganancias", RankingReportDisplay::percentageTotalEarnings).apply { cellFactory = percentageCF }
                    readonlyColumn("% ventas sin Iva", RankingReportDisplay::percentageTotalSalesSinIva).apply { cellFactory = percentageCF }
                    readonlyColumn("% ventas con Iva", RankingReportDisplay::percentageTotalSalesConIva).apply { cellFactory = percentageCF }
                    smartResize()
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                }

                borderpane {
                    left {
                        vbox(spacing = 2, alignment = Pos.CENTER_LEFT) {
                            prefHeight = 150.0

                            hbox {
                                label("Ventas totales (sin iva): ").style { fontSize = 20.px }
                                label("$${NumberFormat.getIntegerInstance().format(totalSinIva)}").style {
                                    textFill = Color.GREEN
                                    fontSize = 20.px
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            hbox {
                                label("Ventas totales (con iva): ").style { fontSize = 20.px }
                                label("$${NumberFormat.getIntegerInstance().format(totalConIva)}").style {
                                    textFill = Color.GREEN
                                    fontSize = 20.px
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            hbox {
                                label("Ganancias totales: ").style { fontSize = 20.px }
                                label(
                                    "$${
                                        NumberFormat.getIntegerInstance().format(totalEarnings)
                                    } (${((totalEarnings / totalSinIva) * 100).round(4)}% sin iva)"
                                ).style {
                                    textFill = Color.GREEN
                                    fontSize = 20.px
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            hbox {
                                label("Número de bolsas: ").style { fontSize = 20.px }
                                label(bagQuantity.toString()).style {
                                    textFill = Color.GREEN
                                    fontSize = 20.px
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            hbox {
                                label("Número de ventas: ").style { fontSize = 20.px }
                                label(
                                    "$totalNumberOfSales (promedio de $${
                                        NumberFormat.getIntegerInstance().format(totalConIva / totalNumberOfSales)
                                    } por venta con iva)"
                                ).style {
                                    textFill = Color.GREEN
                                    fontSize = 20.px
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
                                                it.producto.descripcionLarga.toLowerCase()
                                                    .contains(searchString!!.toLowerCase()) ||
                                                        it.producto.descripcionCorta.toLowerCase()
                                                            .contains(searchString.toLowerCase())
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
                                        combobox<FamiliaDB>(
                                            searchByFamilia,
                                            familiaController.getFamiliasWithUpdate()
                                        ).apply {
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

    fun getMonthlyStartDates(): List<String> {

        val oldestDate: Timestamp = ventaRepo.findFirstByOrderByVentaIdAsc()?.fechaHora
            ?: throw GenericApplicationException("No existe ninguna venta registrada!")
        val newestDate: Timestamp = ventaRepo.findFirstByOrderByVentaIdDesc()?.fechaHora
            ?: throw GenericApplicationException("No existe ninguna venta registrada!")

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

        return getMonthlyStartDates().filter {
            val endMonthYear = decodeMonthAndYearRaw(it)

            return@filter endMonthYear.after(startMonthYear)
        }
    }

    private fun encodeMonthAndYearRaw(month: Int, year: Int): String {
        return "${year + 1900}${monthYearSeparator}${numberToMonth[month + 1]}"
    }

    private fun decodeMonthAndYearRaw(str: String): Calendar {
        val split = str.split(monthYearSeparator)

        return Calendar.getInstance().apply { set(split[0].toInt(), monthToNumber[split[1]]!!, 1) }
    }


    class RankingReportDisplay(
        val longDesc: String,
        val sinIva: Double,
        val conIva: Double,
        val sinIvaVolume: Double,
        val conIvaVolume: Double,
        val margin: Double,
        val soldQuantity: Int,
        val earningsAmount: Double,
        val taxAmount: Double,
        var percentageTotalEarnings: Double,
        var percentageTotalSalesSinIva: Double,
        var percentageTotalSalesConIva: Double,
        val producto: ProductoDB
    )
}
