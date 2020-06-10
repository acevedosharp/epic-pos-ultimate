package views

import styles.MainStylesheet
import tornadofx.*

class SideNavigation: Fragment() {
    override val root = vbox {
        style {
            backgroundColor += c(21, 55, 83)
        }
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
    }
}