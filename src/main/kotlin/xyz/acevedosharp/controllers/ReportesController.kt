package xyz.acevedosharp.controllers

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.HBox
import tornadofx.*
import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.persistence_layer.repositories.ItemVentaRepo
import xyz.acevedosharp.persistence_layer.repositories.PedidoRepo
import xyz.acevedosharp.persistence_layer.repositories.ProductoRepo
import xyz.acevedosharp.persistence_layer.repositories.VentaRepo
import xyz.acevedosharp.ui_models.Producto
import java.sql.Timestamp
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

    private val productoRepo = find<CustomApplicationContextWrapper>().context.getBean(ProductoRepo::class.java)
    private val ventaRepo = find<CustomApplicationContextWrapper>().context.getBean(VentaRepo::class.java)
    private val itemVentaRepo = find<CustomApplicationContextWrapper>().context.getBean(ItemVentaRepo::class.java)
    private val pedidoRepo = find<CustomApplicationContextWrapper>().context.getBean(PedidoRepo::class.java)

    fun generateReport(reportType: String, productQuantity: String, selectedProduct: Producto?, startDate: String, endDate: String): HBox {

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


        if (productQuantity == "Todos los productos") {
            var data = arrayListOf<RankingReportDisplay>()

            if (reportType == "Ventas") {
                val products = productoRepo.findAll()

                products.forEach { producto ->
                    val matchingSoldItems =
                        itemVentaRepo.findAllByProductoEqualsAndFechaHoraBetween(
                            producto,
                            startDateTimestamp,
                            endDateTimestamp
                        )

                    var soldValue = 0
                    var soldQuantity = 0
                    var costValue = 0


                    matchingSoldItems.forEach {
                        soldValue += it.cantidad * it.precioVenta.toInt()
                        soldQuantity += it.cantidad
                    }
                }

            } else // Pedidos
                data = arrayListOf()

            return object : View() {
                override val root = hbox {
                    button("Joe") {
                        action {
                            this.text = "Joe Mama"
                        }
                    }
                }
            }.root

        } else {

            return object : View() {
                override val root = hbox {
                    button("Joe") {
                        action {
                            this.text = "Joe Mama"
                        }
                    }
                }
            }.root

        }
    }

    fun getStartDates(reportType: String): List<String> {

        val oldestDate = ventaRepo.findFirstByOrderByVentaIdAsc().fechaHora
        val newestDate = ventaRepo.findFirstByOrderByVentaIdDesc().fechaHora

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

    fun getEndDates(reportType: String, startDate: String): List<String> {
        val startMonthYear = decodeMonthAndYearRaw(startDate)

        return getStartDates(reportType).filter {
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


    class RankingReportDisplay(barCode: String, longDesc: String, soldValue: Int, percentageSold: Double, soldQuantity: Int, costValue: Int, earnings: Int, earningsPercentage: Double) {
        val barCode = SimpleStringProperty(barCode)
        val longDesc = SimpleStringProperty(longDesc)
        val soldValue = SimpleIntegerProperty(soldValue)
        val percentageSold = SimpleDoubleProperty(percentageSold)
        val soldQuantity = SimpleIntegerProperty(soldQuantity)
        val costValue = SimpleIntegerProperty(costValue)
        val earnings = SimpleIntegerProperty(earnings)
        val earningsPercentage = SimpleDoubleProperty(earningsPercentage)
        override fun toString(): String {
            return "RankingReportDisplay(barCode=$barCode, longDesc=$longDesc, soldValue=$soldValue, percentageSold=$percentageSold, soldQuantity=$soldQuantity, costValue=$costValue, earnings=$earnings, earningsPercentage=$earningsPercentage)"
        }
    }
}
