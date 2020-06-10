package views

import javafx.geometry.Pos
import javafx.scene.paint.Color
import styles.MainStylesheet
import tornadofx.*

class SideNavigation: Fragment() {
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
            addClass(MainStylesheet.selectedButton)
            graphic = imageview("images/products.png") {
                fitWidth = 60.0
                fitHeight = 60.0
            }
        }
        button("Pedidos") {
            addClass(MainStylesheet.navigationButton)
            addClass(MainStylesheet.unselectedButton)
            graphic = imageview("images/orders.png") {
                fitWidth = 60.0
                fitHeight = 60.0
            }
        }

        style {
            backgroundColor += c(21, 55, 83)
        }
    }
}