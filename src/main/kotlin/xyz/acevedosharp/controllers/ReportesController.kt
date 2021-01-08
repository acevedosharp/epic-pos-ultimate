package xyz.acevedosharp.controllers

import javafx.scene.layout.HBox
import tornadofx.*
import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.persistence_layer.repositories.PedidoRepo
import xyz.acevedosharp.persistence_layer.repositories.ProductoRepo
import xyz.acevedosharp.persistence_layer.repositories.VentaRepo
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
    private val pedidoRepo = find<CustomApplicationContextWrapper>().context.getBean(PedidoRepo::class.java)

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

    fun generateReport(/*reportType: String, productQuantity: String, selectedProduct: String, startDate: Date, endDate: Date*/): HBox {
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

    private fun encodeMonthAndYearRaw(month: Int, year: Int): String {
        return "${year + 1900}${monthYearSeparator}${numberToMonth[month + 1]}"
    }

    private fun decodeMonthAndYearRaw(str: String): Pair<Int, Int> {
        val split = str.split(monthYearSeparator)

        return monthToNumber[split[1]]!! to split[0].toInt()
    }
}
