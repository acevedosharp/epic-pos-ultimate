package xyz.acevedosharp.views

import javafx.geometry.Pos
import tornadofx.*

class UnknownErrorDialog: Fragment() {

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Error inesperado") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.redLabel)
        }
        label("Ha ocurrido un error inesperado. Es recomendable reiniciar el POS en este momento, pues la información que muestra puede no ser confiable.").style {
            padding = box(vertical = 30.px, horizontal = 5.px)
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