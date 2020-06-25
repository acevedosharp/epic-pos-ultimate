package com.acevedosharp.views.modules

import com.acevedosharp.controllers.ProductoController
import com.acevedosharp.ui_models.UncommittedItemVenta
import com.acevedosharp.views.MainStylesheet
import com.acevedosharp.views.helpers.CurrentModule
import com.acevedosharp.views.shared_components.ItemVentaComponent
import com.acevedosharp.views.shared_components.SideNavigation
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class PuntoDeVentaView : View("Punto de venta") {

    private val productoController = find<ProductoController>()
    private val view = this

    private val uncommittedItemsAsViews: ObservableList<ItemVentaComponent> = FXCollections.observableArrayList()
    private val uncommittedItems: ObservableList<Node> = FXCollections.observableArrayList()
    private val dineroEntregado = SimpleIntegerProperty()
    private val currentCodigo = SimpleStringProperty()

    init {
        uncommittedItemsAsViews.setAll(productoController.productos.mapIndexed { index, producto -> ItemVentaComponent(UncommittedItemVenta(producto, 1), uncommittedItemsAsViews, index)})
        uncommittedItemsAsViews.onChange {
            uncommittedItemsAsViews.forEachIndexed { index, node: ItemVentaComponent ->
                node.indexProperty.set(index)
            }
            uncommittedItems.setAll(uncommittedItemsAsViews.map { it.root })
        }
        uncommittedItems.setAll(uncommittedItemsAsViews.map { it.root })
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(CurrentModule.PUNTO_DE_VENTA, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            style {
                backgroundColor += LinearGradient(
                    1.0,
                    0.0,
                    1.0,
                    1.0,
                    true,
                    null,
                    listOf(
                        Stop(0.0000, c(169, 193, 215)),
                        Stop(0.0075, c(169, 193, 215)),
                        Stop(1.0000, c(36, 116, 191))
                    )
                )
            }

            top {
                hbox(spacing = 10) {
                    addClass(MainStylesheet.topBar)

                    rectangle(height = 0.0, width = 8.0)
                    stackpane {
                        alignment = Pos.CENTER_LEFT
                        rectangle(width = 720, height = 70) { fill = c(255, 255, 255, 0.0) }
                        label("Producto").style {
                            prefWidth = 715.px
                            fontSize = 28.px
                            fontWeight = FontWeight.BOLD
                            textAlignment = TextAlignment.LEFT
                            textFill = Color.WHITE
                        }
                    }
                    line(0, 0, 0, 60).style {
                        stroke = c(255, 255, 255, 0.25)
                    }
                    stackpane {
                        alignment = Pos.CENTER_LEFT
                        rectangle(width = 150, height = 70) { fill = c(255, 255, 255, 0.0) }
                        label("Cantidad").style {
                            prefWidth = 145.px
                            fontSize = 28.px
                            fontWeight = FontWeight.BOLD
                            textAlignment = TextAlignment.CENTER
                            textFill = Color.WHITE
                        }
                    }
                    line(0, 0, 0, 60).style {
                        stroke = c(255, 255, 255, 0.25)
                    }
                    stackpane {
                        alignment = Pos.CENTER_LEFT
                        rectangle(width = 170, height = 70) { fill = c(255, 255, 255, 0.0) }
                        label("P. Unidad").style {
                            prefWidth = 165.px
                            fontSize = 28.px
                            fontWeight = FontWeight.BOLD
                            textAlignment = TextAlignment.CENTER
                            textFill = Color.WHITE
                        }
                    }
                    rectangle(height = 0.0, width = 104.0)
                    line(0, 0, 0, 90).style {
                        stroke = c(255, 255, 255, 0.40)
                    }
                    textfield(currentCodigo).style {
                        fontSize = 32.px
                    }
                    button("\uD83D\uDD0D") { addClass(MainStylesheet.greenButton) }.style {
                        fontSize = 32.px
                        textFill = Color.WHITE
                    }

                }
            }
            center {
                vbox(spacing = 10, alignment = Pos.TOP_CENTER) {

                    paddingAll = 10
                    scrollpane {
                        prefHeight = 1080.0
                        prefWidth = 1230.0
                        maxWidth = 1230.0
                        vbox(spacing = 10, alignment = Pos.TOP_CENTER) {
                            Bindings.bindContent(children, uncommittedItems)
                        }

                        isPannable = true
                        paddingAll = 8.0
                        style {
                            borderRadius += box(10.px)
                            borderWidth += box(0.px)
                            borderColor += box(c(0, 0, 0, 0.125))
                        }
                    }
                }
            }
            right {
                vbox(alignment = Pos.TOP_CENTER) {
                    paddingAll = 8.0
                    prefWidth = 474.0
                    hgrow = Priority.ALWAYS
                    text("Total: 5600").style { fontSize = 40.px }

                    textfield(dineroEntregado) {
                        prefWidth = 440.0; maxWidth = 440.0
                        alignment = Pos.CENTER
                        isFocusTraversable = false
                        isEditable = false
                        style {
                            backgroundColor += c(255, 255, 255, 0.5)
                            fontSize = 64.px
                            backgroundRadius += box(0.px)
                            borderRadius += box(0.px)
                        }
                    }
                    rectangle(width = 0.0, height = 10.0)
                    hbox(alignment = Pos.TOP_CENTER) {
                        button("1") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}1".toInt()) }
                        button("2") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}2".toInt()) }
                        button("3") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}3".toInt()) }
                    }
                    hbox(alignment = Pos.TOP_CENTER) {
                        button("4") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}4".toInt()) }
                        button("5") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}5".toInt()) }
                        button("6") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}6".toInt()) }
                    }
                    hbox(alignment = Pos.TOP_CENTER) {
                        button("7") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}7".toInt()) }
                        button("8") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}8".toInt()) }
                        button("9") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}9".toInt()) }
                    }
                    hbox(alignment = Pos.TOP_CENTER) {
                        button("←") { addClass(MainStylesheet.redButton, MainStylesheet.keyButton) }.action {
                            val s = dineroEntregado.value.toString()
                            if (s.length > 1)
                                dineroEntregado.set(s.substring(0, s.length - 1).toInt())
                            else
                                dineroEntregado.set(0)
                        }
                        button("0") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}0".toInt()) }
                        button("00") {
                            addClass(
                                MainStylesheet.grayButton,
                                MainStylesheet.keyButton
                            )
                        }.action { dineroEntregado.set("${dineroEntregado.value}00".toInt()) }
                    }
                    rectangle(width = 0.0, height = 10.0)
                    button("Realizar venta") {
                        addClass(MainStylesheet.greenButton)
                        prefWidth = 440.0
                        style {
                            fontSize = 40.px
                            fontWeight = FontWeight.BOLD
                            textFill = Color.WHITE
                        }

                        action {

                        }
                    }
                }
            }
        }
    }
}