package views

import javafx.geometry.Pos
import models.CurrentModule
import models.CurrentModule.*
import styles.MainStylesheet
import tornadofx.*

class SideNavigation(currentModule: CurrentModule, root: View): Fragment() {
    override val root = vbox(alignment = Pos.TOP_CENTER) {
        rectangle(width = 0, height = 40)
        imageview("images/epic_logo.png") {
            fitWidth = 150.0
            fitHeight = 150.0
        }
        rectangle(width = 0, height = 25)
        line(startX = 0, endX = 175).style {
            stroke = c(255, 255, 255, 0.25)
        }
        rectangle(width = 0, height = 25)

        button("Productos") {
            addClass(MainStylesheet.navigationButton)
            addClass(if (currentModule == PRODUCTOS) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/products.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(ProductosView())
            }
        }
        button("Proveedores") {
            addClass(MainStylesheet.navigationButton)
            addClass(if (currentModule == PROVEEDORES) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/providers.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(ProveedorView())
            }
        }

        style {
            backgroundColor += c(21, 55, 83)
        }
    }
}