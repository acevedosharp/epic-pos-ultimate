package xyz.acevedosharp.ui_models

import java.util.*

data class Notification(val uuid: UUID, val type: NotificationType, val message: String) {
    fun getReadableType() = notificationMappings[type]!!
    fun getReadableMessage() = message
}

enum class NotificationType {
    CUSTOMER_BIRTHDAY,
    INVENTORY_ALERT,
    EXPIRATION_DATE_ALERT,
    PRICE_ROUNDED_UP_ALERT
}

val notificationMappings = hashMapOf(
    NotificationType.CUSTOMER_BIRTHDAY to "Cumpleaños",
    NotificationType.INVENTORY_ALERT to "Bajo inventario",
    NotificationType.EXPIRATION_DATE_ALERT to "Fecha de expedición",
    NotificationType.PRICE_ROUNDED_UP_ALERT to "Subida P. de venta"
)
