package xyz.acevedosharp.views

import javafx.geometry.Pos
import tornadofx.*
import xyz.acevedosharp.Joe

class CodigoNotRecognizedDialog: Fragment() {

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Código no reconocido") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.redLabel)
        }
        label("No se ha encontrado un producto con el código introducido.").style {
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
