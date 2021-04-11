package xyz.acevedosharp

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
}