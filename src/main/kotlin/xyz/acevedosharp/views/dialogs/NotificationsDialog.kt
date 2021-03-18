package xyz.acevedosharp.views.dialogs

import javafx.geometry.Pos
import tornadofx.*
import xyz.acevedosharp.ui_models.Notification
import xyz.acevedosharp.views.MainStylesheet

class NotificationsDialog(notifications: List<Notification>) : Fragment() {
    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Notificaciones") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.blueLabel)
        }
        scrollpane(fitToWidth = true, fitToHeight = true) {
            vbox {
                children.setAll(notifications.map { SingleNotificationCard(it, notifications).root })
            }
        }
        hbox(spacing = 80, alignment = Pos.CENTER) {
            button("Aceptar") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.greenButton)
                addClass(MainStylesheet.expandedButton)
                action {
                    close()
                }
            }
        }
    }
}

class SingleNotificationCard(notification: Notification, notifications: List<Notification>) : Fragment() {
    override val root = hbox {
        label(notification.message)
        button("Eliminar") {
            action {
                println("deleted")
            }
        }
    }
}
