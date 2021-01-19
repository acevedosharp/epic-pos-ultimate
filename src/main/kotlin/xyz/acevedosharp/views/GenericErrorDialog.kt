package xyz.acevedosharp.views

import javafx.geometry.Pos
import tornadofx.*

class GenericErrorDialog(message: String): Fragment() {

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Ha ocurrido un error") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.redLabel)
        }
        text("Mensaje: $message").style {
            wrapText = true
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

class GenericApplicationException(message: String): RuntimeException(message)
