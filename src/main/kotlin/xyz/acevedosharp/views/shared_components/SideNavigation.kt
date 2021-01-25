package xyz.acevedosharp.views.shared_components

import xyz.acevedosharp.views.MainStylesheet
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.paint.Color
import xyz.acevedosharp.views.helpers.CurrentModule
import xyz.acevedosharp.views.helpers.CurrentModule.*
import xyz.acevedosharp.views.screens.*
import tornadofx.*

class SideNavigation(currentModule: CurrentModule, currentView: View) : Fragment() {
    override val root = vbox(alignment = Pos.TOP_CENTER) {
        rectangle(width = 0, height = 40)
        imageview("images/epic_logo.png") {
            fitWidth = 150.0
            fitHeight = 150.0
        }
        rectangle(width = 0, height = 25)
        line(startX = 0, endX = 185).style {
            stroke = c(255, 255, 255, 0.35)
        }
        button("Punto de Venta") {
            val tag = PUNTO_DE_VENTA
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            addClass(if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/store_logo.png") {
                fitWidth = 150.0
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
                currentView.replaceWith(PuntoDeVentaView())
            }
        }
        line(startX = 0, endX = 175).style {
            stroke = c(255, 255, 255, 0.25)
        }
        button("Productos") {
            tag = PRODUCTOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/productos.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                currentView.replaceWith(ProductoView())
            }
        }
        button("Pedidos") {
            tag = PEDIDOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/pedidos.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                currentView.replaceWith(PedidoView())
            }
        }
        button("Reportes") {
            tag = REPORTES
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/reportes.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                currentView.replaceWith(ReporteScreen())
            }
        }
        line(startX = 0, endX = 175).style {
            stroke = c(255, 255, 255, 0.25)
        }
        button("Familias") {
            tag = FAMILIAS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/familias.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                currentView.replaceWith(FamiliaView())
            }
        }
        button("Proveedores") {
            tag = PROVEEDORES
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/proveedores.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                currentView.replaceWith(ProveedorView())
            }
        }
        button("Empleados") {
            tag = EMPLEADOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/empleados.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                currentView.replaceWith(EmpleadoView())
            }
        }
        button("Clientes") {
            tag = CLIENTES
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/clientes.jpg") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                currentView.replaceWith(ClienteView())
            }
        }

        style {
            backgroundColor += c(21, 55, 83)
        }
    }
}
