package xyz.acevedosharp.views

import javafx.geometry.Pos
import tornadofx.*

class NoInternetConnectionErrorDialog : Fragment() {
    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("No hay acceso a internet") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.redLabel)
        }
        text("Por favor cambie de red y vuelva a intentar. Si sí tiene internet comuníquese con 302 217 5285.").style {
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

