package com.acevedosharp.validation_helpers

import com.acevedosharp.styles.MainStylesheet
import javafx.geometry.Pos
import tornadofx.*

class InputNotUniqueDialog(msg: String) : Fragment() {

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Error guardando") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.yellowLabel)
        }
        label(msg).style {
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