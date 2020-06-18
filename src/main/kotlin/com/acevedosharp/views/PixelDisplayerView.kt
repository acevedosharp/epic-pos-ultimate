package com.acevedosharp.views

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import tornadofx.*

class SizeDisplayer {
    var width = SimpleDoubleProperty()
    var height = SimpleDoubleProperty()
}

class PixelDisplayerView : View() {

    var sizeLabel: Label by singleAssign()
    var sizeDisplayer = SizeDisplayer()

    override val root = vbox(alignment = Pos.CENTER) {
        useMaxSize = true

        sizeDisplayer.width.set(width)
        sizeDisplayer.height.set(height)

        widthProperty().addListener { _, _, newValue ->
            sizeDisplayer.width.set(newValue as Double)
        }

        heightProperty().addListener { _, _, newValue ->
            sizeDisplayer.height.set(newValue as Double)
        }
        spacingProperty().bind(this.heightProperty().divide(5))

        sizeLabel = label(Bindings.concat(sizeDisplayer.width, "px ", sizeDisplayer.height, "px"))

    }
}