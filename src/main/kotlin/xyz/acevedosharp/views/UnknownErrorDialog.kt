package xyz.acevedosharp.views

import javafx.geometry.Pos
import tornadofx.*

class UnknownErrorDialog(message: String): Fragment() {

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Ocurrió un error inesperado") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.redLabel)
        }
        label("Mensaje: $message").style {
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
