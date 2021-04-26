package xyz.acevedosharp.views.dialogs

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import xyz.acevedosharp.controllers.NotificationsController
import xyz.acevedosharp.ui_models.Notification
import xyz.acevedosharp.views.MainStylesheet
import java.util.*

class NotificationsDialog(notificationsController: NotificationsController) : Fragment() {
    private val notifications: ObservableList<Notification> = notificationsController.getNotifications()
    private var table: TableView<Notification> by singleAssign()
    private val selectedUUID = SimpleObjectProperty<UUID>()
    private val existsSelection = SimpleBooleanProperty(false)

    override val root = vbox(spacing = 0) {
        prefWidth = 1280.0
        prefHeight = 600.0

        useMaxSize = true
        hbox {
            addClass(MainStylesheet.topBar)
            paddingBottom = 4
            useMaxWidth = true

            label("Notificaciones") {
                style {
                    fontSize = 26.px
                    fontWeight = FontWeight.BOLD
                    textFill = Color.WHITE
                }
            }
            rectangle(width = 20, height = 0)
            button("Eliminar seleccionada") {
                enableWhen(existsSelection)
                addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton)
                action {
                    notificationsController.clearNotification(selectedUUID.value)
                }
            }
            rectangle(width = 256, height = 0)
            button("Eliminar todas") {
                enableWhen(notifications.sizeProperty.isNotEqualTo(0))
                addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton)
                action {
                    notificationsController.clearAllNotifications()
                }
            }
        }

        table = tableview(notifications) {
            column("Mensaje", Notification::getReadableMessage)
            column("Tipo", Notification::getReadableType)
            smartResize()

            placeholder = label("No hay notificaciones pendientes")

            selectionModel.selectedItemProperty().onChange {
                existsSelection.value = it != null
                if (it != null) {
                    selectedUUID.set(it.uuid)
                }
            }

            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            useMaxHeight = true
        }

        rectangle(width = 0, height = 20)
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
