package com.acevedosharp.views.modules

import com.acevedosharp.controllers.LoteController
import com.acevedosharp.controllers.PedidoController
import com.acevedosharp.controllers.ProveedorController
import com.acevedosharp.ui_models.Proveedor
import com.acevedosharp.views.MainStylesheet
import com.acevedosharp.views.shared_components.SideNavigation
import com.acevedosharp.views.helpers.CurrentModule.PEDIDOS
import com.acevedosharp.views.shared_components.PedidoDisplay
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.layout.FlowPane
import tornadofx.*


class PedidoView : View("Módulo de pedidos") {

    private val pedidoController = find<PedidoController>()
    private val loteController = find<LoteController>()
    private val proveedorController = find<ProveedorController>()

    private val items: ObservableList<Node> = FXCollections.observableArrayList(
        pedidoController.pedidos.sortedBy { it.fechaHora }.map { PedidoDisplay(it).root }
    )
    private var provComboBox by singleAssign<ComboBox<Proveedor>>()
    private val searchByProveedor = SimpleObjectProperty<Proveedor>()
    private val view = this

    init {
        searchByProveedor.onChange { selectedItem ->
            if (selectedItem == null)
                items.setAll(pedidoController.pedidos.sortedBy { it.fechaHora }.map { PedidoDisplay(it).root })
            else
                items.setAll(pedidoController.pedidos.filter { it.proveedor == selectedItem }.sortedBy { it.fechaHora }.map { PedidoDisplay(it).root })
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(PEDIDOS, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Pedido") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewProductoFormView>(closeButton = false, modal = true)
                        }
                    }
                    rectangle(width = 10, height = 0)
                    line(0, 0, 0, 35).style {
                        stroke = c(255, 255, 255, 0.25)
                    }
                    rectangle(width = 10, height = 0)
                    hbox(spacing = 10, alignment = Pos.CENTER) {
                        vbox {
                            label("Buscar por proveedor").apply { addClass(MainStylesheet.searchLabel) }
                            provComboBox = combobox(searchByProveedor, proveedorController.proveedores) {
                                prefWidth = 400.0
                                makeAutocompletable(false)
                            }

                        }
                        button("Quitar filtro") {
                            addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton)
                            action { provComboBox.selectionModel.clearSelection() }
                        }

                    }
                }
            }
            center {
                flowpane {
                    paddingAll = 20
                    hgap = 15.0
                    vgap = 15.0
                    Bindings.bindContent(children, items)
                }
            }
        }
    }
}
