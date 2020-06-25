package com.acevedosharp.views.shared_components

import com.acevedosharp.ui_models.UncommittedItemVenta
import com.acevedosharp.views.MainStylesheet
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class ItemVentaComponent(uncommittedItemVenta: UncommittedItemVenta, observableList: ObservableList<ItemVentaComponent>, index: Int) : Fragment() {
    val producto = uncommittedItemVenta.producto // Not observable
    val cantidad = uncommittedItemVenta.cantidad // Observable
    val indexProperty = SimpleIntegerProperty(index)

    override val root = region {
        setPrefSize(1186.0, 80.0)
        paddingAll = 10
        style {
            backgroundRadius += box(10.px)
            borderRadius += box(10.px)
            borderWidth += box(1.px)
            borderColor += box(c(0, 0, 0, 0.2))
            backgroundColor += Color.WHITE
        }

        hbox(spacing = 10) {
            alignment = Pos.CENTER_LEFT
            paddingAll = 5
            stackpane {
                alignment = Pos.CENTER_LEFT
                rectangle(width = 720, height = 46) { fill = c(255, 255, 255, 0.0)}
                label(producto.descLarga).style {
                    prefWidth = 715.px
                    fontSize = 26.px
                    fontWeight = FontWeight.BOLD
                    textAlignment = TextAlignment.LEFT
                }
            }
            line(0.0, 0.0, 0.0, 64).style {
                stroke = c(0, 0, 0, 0.2)
                strokeWidth = 2.px
            }
            stackpane {
                alignment = Pos.CENTER_LEFT
                rectangle(width = 150, height = 64) { fill = c(255, 255, 255, 0.0)}
                label(cantidad).style {
                    prefWidth = 145.px
                    fontSize = 26.px
                    fontWeight = FontWeight.BOLD
                    textAlignment = TextAlignment.CENTER
                }
            }
            line(0.0, 0.0, 0.0, 64).style {
                stroke = c(0, 0, 0, 0.2)
                strokeWidth = 2.px
            }
            stackpane {
                alignment = Pos.CENTER_LEFT
                rectangle(width = 170, height = 64) { fill = c(255, 255, 255, 0.0)}
                label(producto.precioVentaProperty).style {
                    prefWidth = 165.px
                    fontSize = 26.px
                    fontWeight = FontWeight.BOLD
                    textAlignment = TextAlignment.LEFT
                }
            }
            button("X") {
                addClass(MainStylesheet.redButton)
                style {
                    prefWidth = 70.px
                    prefHeight = 70.px
                    backgroundRadius += box(10.px)
                    borderRadius += box(10.px)
                    fontSize  = 30.px
                    fontWeight = FontWeight.BOLD
                    textFill = Color.WHITE
                }
                action {
                    observableList.removeAt(indexProperty.value)
                }
            }
        }
    }
}

