package xyz.acevedosharp

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

    fun calculateSellPriceBrokenDown(basePrice: Double, margin: Double, iva: Int): Triple<Double, Double, Double> {
        val withMargin = basePrice / (1 - (margin / 100))
        val marginAmount = withMargin - basePrice

        val ivaAmount = withMargin*(iva.toDouble()/100)
        val withIva = withMargin + ivaAmount
        val withIvaRounded = (withIva - 1) + (50 - ((withIva - 1) % 50))

        return Triple(marginAmount, ivaAmount, withIvaRounded)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}