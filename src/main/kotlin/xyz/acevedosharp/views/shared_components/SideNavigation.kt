package xyz.acevedosharp.views.shared_components

import xyz.acevedosharp.views.MainStylesheet
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import xyz.acevedosharp.views.helpers.CurrentModule
import xyz.acevedosharp.views.helpers.CurrentModule.*
import tornadofx.*
import xyz.acevedosharp.Joe
import xyz.acevedosharp.controllers.NotificationsController
import xyz.acevedosharp.views.dialogs.NotificationsDialog
import xyz.acevedosharp.views.dialogs.PasswordDialog
import xyz.acevedosharp.views.helpers.CurrentModuleHelper
import xyz.acevedosharp.views.helpers.SecuritySettings
import xyz.acevedosharp.views.screens.PuntoDeVentaView

class SideNavigation(currentModule: CurrentModule, currentView: View) : Fragment() {
    override val root = vbox(alignment = Pos.TOP_CENTER) {
        rectangle(width = 0, height = 40)
        imageview("images/epic_logo.png") {
            fitWidth = 150.0
            fitHeight = 150.0
            preserveRatioProperty().set(true)
        }
        rectangle(width = 0, height = 25)
        line(startX = 0, endX = 185).style {
            stroke = c(255, 255, 255, 0.35)
        }
        button("Punto de Venta") {
            val tag = PUNTO_DE_VENTA
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            addClass(if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/cart.png") {
                fitWidth = 80.0
                fitHeight = 80.0
            }
            style {
                prefWidth = 200.px
                prefHeight = 100.px
                contentDisplay = ContentDisplay.TOP
                alignment = Pos.CENTER
                fontSize = 20.px
                textFill = Color.WHITE
                paddingVertical = 32
            }
            action {
                doNavigationMiddleware(tag, currentView)
            }
        }
        line(startX = 0, endX = 175).style {
            stroke = c(255, 255, 255, 0.25)
        }
        button("Productos") {
            val tag = PRODUCTOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/productos.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                doNavigationMiddleware(tag, currentView)
            }
        }
        button("Pedidos") {
            val tag = PEDIDOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/pedidos.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                doNavigationMiddleware(tag, currentView)
            }
        }
        button("Reportes") {
            val tag = REPORTES
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/reportes.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                doNavigationMiddleware(tag, currentView)
            }
        }
        line(startX = 0, endX = 175).style {
            stroke = c(255, 255, 255, 0.25)
        }
        button("Familias") {
            val tag = FAMILIAS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/familias.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                doNavigationMiddleware(tag, currentView)
            }
        }
        button("Proveedores") {
            val tag = PROVEEDORES
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/proveedores.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                doNavigationMiddleware(tag, currentView)
            }
        }
        button("Empleados") {
            val tag = EMPLEADOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/empleados.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                doNavigationMiddleware(tag, currentView)
            }
        }
        button("Clientes") {
            val tag = CLIENTES
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/clientes.jpg") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                doNavigationMiddleware(tag, currentView)
            }
        }
        rectangle(width = 0, height = 20)
        button {
            style {
                fontSize = 22.px
                borderRadius += box(4.px)
                backgroundRadius += box(4.px)
                padding = box(8.px, 15.px)
                backgroundInsets += box(0.px)
                textFill = Color.WHITE
                fontWeight = FontWeight.BOLD
                wrapText = true
                prefHeight = 110.px
                prefWidth = 180.px
                textAlignment = TextAlignment.CENTER
            }
            addClass(MainStylesheet.redButton)

            val notificationsController = find<NotificationsController>()
            notificationsController.bindTextNotificationButton(this)
            action {
                openInternalWindow(
                    NotificationsDialog(notificationsController),
                    owner = Joe.currentView.value.root
                )
            }
        }

        style {
            backgroundColor += c(21, 55, 83)
        }
    }

    // check password and update Joe.currentView here since I can't get onDock working on Views
    private fun doNavigationMiddleware(tag: CurrentModule, currentView: View) {
        if (SecuritySettings.securedModules[tag]!!) {
            currentView.openInternalWindow(PasswordDialog(currentView, tag))
        } else {
            val targetView = CurrentModuleHelper.screenMappings[tag]!!
            if (targetView::class == PuntoDeVentaView::class) {
                // don't keep items in ivs while prices may change
                (targetView as PuntoDeVentaView).clearAllIVS()
            }
            Joe.currentView.setValue(targetView)
            currentView.replaceWith(targetView)
        }
    }
}
