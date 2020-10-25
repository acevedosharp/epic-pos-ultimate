package xyz.acevedosharp.views.screens

import xyz.acevedosharp.controllers.ClienteController
import xyz.acevedosharp.ui_models.Cliente
import xyz.acevedosharp.ui_models.ClienteModel
import xyz.acevedosharp.views.helpers.FormType
import xyz.acevedosharp.views.helpers.FormType.*
import xyz.acevedosharp.views.helpers.CurrentModule.*
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.shared_components.SideNavigation
import xyz.acevedosharp.views.UnknownErrorDialog
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class ClienteView : View("Módulo de clientes") {

    private val clienteController = find<ClienteController>()
    private val model: ClienteModel by inject()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty()
    private var table: TableView<Cliente> by singleAssign()
    private val view = this

    init {
        searchByNombre.onChange {
            table.items = clienteController.clientes.filter {
                it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase())
            }.asObservable()
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(CLIENTES, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Cliente") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewClienteFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditClienteFormView>(
                                closeButton = false,
                                modal = true
                            )
                        }
                    }
                    rectangle(width = 10, height = 0)
                    line(0, 0, 0, 35).style {
                        stroke = c(255, 255, 255, 0.25)
                    }
                    rectangle(width = 10, height = 0)
                    hbox(spacing = 10, alignment = Pos.CENTER) {
                        vbox {
                            label("Buscar por nombre").apply { addClass(MainStylesheet.searchLabel) }
                            textfield(searchByNombre)

                            prefWidth = 250.0
                        }
                    }
                }
            }

            center {
                hbox {
                    table = tableview(clienteController.clientes) {
                        column("Nombre", Cliente::nombreProperty)
                        column("Teléfono", Cliente::telefonoProperty)
                        column("Dirección", Cliente::direccionProperty).remainingWidth()

                        smartResize()

                        bindSelected(model)
                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                            model.id.value = it?.id
                        }

                        hgrow = Priority.ALWAYS
                    }
                    paddingAll = 6
                    style {
                        backgroundColor += Color.WHITE
                    }
                }
            }

            style {
                backgroundColor += Color.WHITE
            }
        }
    }
}

class BaseClienteFormView(formType: FormType): Fragment() {

    private val clienteController = find<ClienteController>()
    private val model = if (formType == CREATE) ClienteModel() else find(ClienteModel::class)

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 800.0
        label(if (formType == CREATE) "Nuevo Cliente" else "Editar Cliente") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(if (formType == CREATE) MainStylesheet.greenLabel else MainStylesheet.blueLabel)
        }
        form {
            fieldset {
                field("Nombre") {
                    textfield(model.nombre).validator {
                        when {
                            if (formType == CREATE) clienteController.isNombreAvailable(it.toString())
                            else clienteController.existsOtherWithNombre(it.toString(), model.id.value)
                            -> error("Nombre no disponible")
                            it.isNullOrBlank() -> error("Nombre requerido")
                            it.length > 50 -> error("Máximo 50 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Teléfono") {
                    textfield(model.telefono).validator {
                        when {
                            if (formType == CREATE) clienteController.isTelefonoAvailable(it.toString())
                            else clienteController.existsOtherWithTelefono(it.toString(), model.id.value)
                            -> error("Teléfono no disponible")
                            it.isNullOrBlank() -> error("Teléfono requerido")
                            it.length > 20 -> error("Máximo 20 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Dirección") {
                    textfield(model.direccion).validator {
                        when {
                            !it.isNullOrBlank() && it.length > 100 -> error("Máximo 100 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                        action {
                            if (formType == CREATE) {
                                try {
                                    model.commit {
                                        clienteController.add(
                                            Cliente(
                                                null,
                                                model.nombre.value,
                                                model.telefono.value,
                                                model.direccion.value
                                            )
                                        )
                                        close()
                                    }
                                } catch (e: Exception) {
                                    openInternalWindow(UnknownErrorDialog())
                                    println(e.message)
                                }
                            } else {
                                try {
                                    model.commit {
                                        clienteController.edit(model.item)
                                        close()
                                    }
                                } catch (e: Exception) {
                                    openInternalWindow(UnknownErrorDialog())
                                    println(e.message)
                                }
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                        action {
                            if (formType == CREATE) {
                                close()
                            } else {
                                model.rollback()
                                close()
                            }
                        }
                    }
                }
            }
        }
    }
}

// 1. These com.acevedosharp.views need to be accesible from anywhere so that they can be used in other modules for convenience.
class NewClienteFormView : Fragment() {
    override val root = BaseClienteFormView(CREATE).root
}

class EditClienteFormView : Fragment() {
    override val root = BaseClienteFormView(EDIT).root
}