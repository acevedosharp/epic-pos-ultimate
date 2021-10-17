package xyz.acevedosharp.views.shared_components

import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleStringProperty
import xyz.acevedosharp.ui_models.UncommittedItemVenta
import xyz.acevedosharp.views.MainStylesheet
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*
import xyz.acevedosharp.GlobalHelper.formatCurrency
import xyz.acevedosharp.Joe
import xyz.acevedosharp.views.screens.ModifyItemVentaQuantityDialog
import xyz.acevedosharp.views.screens.PuntoDeVentaView

class ItemVentaComponent(
    uncommittedItemVenta: UncommittedItemVenta,
    val currentUncommittedIVS: PuntoDeVentaView.CurrentUncommittedIVS,
    val papi: PuntoDeVentaView
) : Fragment() {

    val producto = uncommittedItemVenta.producto // Not observable
    val cantidad = uncommittedItemVenta.cantidadProperty // Observable

    val cantidadProxyString: StringBinding = cantidad.asString()
    val itemPriceProxyString = SimpleStringProperty(
        producto.precioVenta
            .formatCurrency()
    )

    lateinit var editionField: TextField

    init {
        cantidad.onChange {
            currentUncommittedIVS.recalculateTotal()
            itemPriceProxyString.set(
                (producto.precioVenta * it)
                   .formatCurrency()
            )
        }
    }

    override val root = region {
        setPrefSize(1186.0, 100.0)
        paddingAll = 10
        style {
            backgroundRadius += box(10.px)
            borderRadius += box(10.px)
            borderWidth += box(1.px)
            borderColor += box(c(56, 56, 56))
            backgroundColor += Color.WHITE
        }

        hbox(spacing = 10) {
            alignment = Pos.CENTER_LEFT
            paddingAll = 5
            // Producto
            stackpane {
                alignment = Pos.CENTER_LEFT
                rectangle(width = 590, height = 90) { fill = c(255, 255, 255, 0.0) }
                label(producto.descripcionLarga).style {
                    prefWidth = 590.px
                    fontSize = 32.px
                    fontWeight = FontWeight.NORMAL
                    textAlignment = TextAlignment.LEFT
                    wrapText = true
                }
            }
            line(0.0, 0.0, 0.0, 64).style {
                stroke = c(0, 0, 0, 0.2)
                strokeWidth = 2.px
            }
            // Cantidad
            stackpane {
                alignment = Pos.CENTER_LEFT
                button(cantidadProxyString) {
                    addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                    style {
                        prefWidth = 120.px
                        fontSize = 28.px
                        fontWeight = FontWeight.BOLD
                        textAlignment = TextAlignment.CENTER
                    }

                    action {
                        papi.removeAlwaysFocusListener()
                        Joe.currentView.value.openInternalWindow(
                            view = ModifyItemVentaQuantityDialog::class,
                            modal = true,
                            closeButton = false,
                            params = mapOf(
                                "owner" to papi,
                                "targetProperty" to cantidad,
                                "pdvView" to papi
                            )
                        )
                    }
                }
            }
            line(0.0, 0.0, 0.0, 64).style {
                stroke = c(0, 0, 0, 0.2)
                strokeWidth = 2.px
            }
            // P. Unidad
            stackpane {
                alignment = Pos.CENTER_LEFT
                label(
                    producto.precioVenta
                        .formatCurrency()
                ).style {
                    prefWidth = 135.px
                    fontSize = 32.px
                    textAlignment = TextAlignment.LEFT
                }
            }
            line(0.0, 0.0, 0.0, 64).style {
                stroke = c(0, 0, 0, 0.2)
                strokeWidth = 2.px
            }
            // P. Ítems
            stackpane {
                alignment = Pos.CENTER_LEFT
                label(itemPriceProxyString).style {
                    prefWidth = 165.px
                    fontSize = 32.px
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
                    fontSize = 30.px
                    fontWeight = FontWeight.BOLD
                    textFill = Color.WHITE
                }
                action {
                    currentUncommittedIVS.removeByCodigo(producto.codigo)
                }
            }
        }
    }
}
