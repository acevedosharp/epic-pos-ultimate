package xyz.acevedosharp.ui_models

import java.util.*

data class Notification(val uuid: UUID, val type: NotificationType, val message: String)

enum class NotificationType {
    CUSTOMER_BIRTHDAY,
    INVENTORY_ALERT,
    EXPIRATION_DATE_ALERT
}
