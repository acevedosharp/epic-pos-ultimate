package xyz.acevedosharp

import javafx.util.Duration
import tornadofx.runLater
import java.text.NumberFormat
import kotlin.math.ceil
import kotlin.math.round

object GlobalHelper {
    fun nullableStringEnforcer(str: String?): String? {
        if (str == null || str.isBlank()) return null
        else return str
    }

    fun nullableIntBy0Value(n: Int): Int? {
        return if (n == 0) null else n
    }

    fun denullifyIntBy0Value(n: Int?): Int {
        return n ?: 0
    }

    // m = 1 - ( bp / sp )
    fun calculateMargin(buyPrice: Double, sellPrice: Double): Double {
        return (1 - (buyPrice / sellPrice)) * 100
    }

    fun calculateSellPriceBrokenDown(basePrice: Double, margin: Double, iva: Int): Triple<Double, Double, Double> {
        val withMargin = basePrice / (1 - (margin / 100))
        val marginAmount = withMargin - basePrice

        val ivaAmount = withMargin * (iva.toDouble() / 100)
        val withIva = withMargin + ivaAmount
        val withIvaRounded: Double
        if (withIva < 50)
            withIvaRounded = withIva.round(4)
        else
            withIvaRounded = (withIva - 1) + (50 - ((withIva - 1) % 50))
        return Triple(marginAmount, ivaAmount, withIvaRounded)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    // this exists in case the target computer is really slow and the item the operation is on hasn't loaded yet
    fun runLaterMinimumDelay(op: () -> Unit) {
        runLaterRecursive(op, 50.0, 1)
    }

    fun Double.formatCurrency(): String {
        if (this < 50.0)
            return "$${this}"
        else
            return "$${NumberFormat.getIntegerInstance().format(ceil(this).toInt())}"
    }

    private fun runLaterRecursive(op: () -> Unit, delay: Double, recursionCount: Int) {
        if (recursionCount <= 10) {
            try {
                runLater(Duration.millis(delay), op)
            } catch (e: RuntimeException) {
                runLaterRecursive(op, delay + 100.0, recursionCount + 1)
            }
        }
        // no RuntimeException please
    }
}