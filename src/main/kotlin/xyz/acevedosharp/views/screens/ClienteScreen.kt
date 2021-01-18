package xyz.acevedosharp.views.screens

import xyz.acevedosharp.controllers.ClienteController
import xyz.acevedosharp.views.helpers.FormType
import xyz.acevedosharp.views.helpers.FormType.*
import xyz.acevedosharp.views.helpers.CurrentModule.*
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.shared_components.SideNavigation
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.ui_models.Cliente
import xyz.acevedosharp.ui_models.ClienteModel

class ClienteView : View("Módulo de clientes") {

    private val clienteController = find<ClienteController>()

    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty("")
    private var table: TableView<ClienteDB> by singleAssign()
    private val view = this

    init {
        Joe.currentView = view

        searchByNombre.onChange { searchString ->
            if (searchString != null) {
                table.items = clienteController.getClientesClean().filter {
                    it.nombre.toLowerCase().contains(searchString.toLowerCase())
                }.asObservable()
            }
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
                            openInternalWindow<NewClienteFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("owner" to view)
                            )
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditClienteFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf(
                                    "id" to selectedId.value,
                                    "owner" to view
                                )
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
                    table = tableview(clienteController.getClientesWithUpdate()) {
                        column("Nombre", ClienteDB::nombre)
                        column("Teléfono", ClienteDB::telefono)
                        column("Dirección", ClienteDB::direccion).remainingWidth()
                        smartResize()

                        clienteController.getClientesClean().onChange {
                            if (searchByNombre.value == "")
                                table.items = clienteController.getClientesClean().asObservable()
                            else
                                searchByNombre.value = ""
                        }

                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                            if (it != null) {
                                selectedId.set(it.clienteId!!)
                            }
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

class BaseClienteFormView(formType: FormType, id: Int?) : Fragment() {

    private val clienteController = find<ClienteController>()
    private val model: ClienteModel = if (formType == CREATE)
        ClienteModel()
    else
        ClienteModel().apply {
            val cliente = clienteController.findById(id!!)!!

            this.id.value = cliente.clienteId
            this.nombre.value = cliente.nombre
            this.telefono.value = cliente.telefono
            this.direccion.value = cliente.direccion
        }

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
                    textfield(model.nombre).validator(trigger = ValidationTrigger.OnBlur) {
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
                    textfield(model.telefono).validator(trigger = ValidationTrigger.OnBlur) {
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
                    textfield(model.direccion).validator(trigger = ValidationTrigger.OnBlur) {
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
                            model.commit {
                                clienteController.save(
                                    Cliente(
                                        if (formType == CREATE) null else model.id.value,
                                        model.nombre.value,
                                        model.telefono.value,
                                        model.direccion.value
                                    )
                                )
                                close()
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                        action {
                            close()
                        }
                    }
                }
            }
        }
    }
}

class NewClienteFormView : Fragment() {
    override val root = BaseClienteFormView(CREATE, null).root

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}

class EditClienteFormView : Fragment() {
    override val root = BaseClienteFormView(EDIT, params["id"] as Int).root

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}
