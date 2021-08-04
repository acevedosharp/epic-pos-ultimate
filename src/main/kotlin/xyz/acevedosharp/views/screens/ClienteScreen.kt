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
import javafx.util.Duration
import tornadofx.*
import xyz.acevedosharp.GlobalHelper
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.ui_models.Cliente
import xyz.acevedosharp.ui_models.ClienteModel

class ClienteView : View("Epic POS - Clientes") {
    private val clienteController = find<ClienteController>()

    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty("")
    private var table: TableView<ClienteDB> by singleAssign()

    init {
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
        add(SideNavigation(CLIENTES, this@ClienteView))
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
                                params = mapOf("owner" to this@ClienteView)
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
                                    "owner" to this@ClienteView
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
                        column("Día", ClienteDB::birthdayDay)
                        column("Mes", ClienteDB::birthdayMonth)
                        smartResize()

                        placeholder = label("No hay clientes")

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

    private var firstTextField: TextField by singleAssign()

    private val model: ClienteModel = if (formType == CREATE)
        ClienteModel()
    else
        ClienteModel().apply {
            val cliente = clienteController.findById(id!!)!!

            this.id.value = cliente.clienteId
            this.nombre.value = cliente.nombre
            this.telefono.value = cliente.telefono
            this.direccion.value = cliente.direccion
            this.birthdayDay.value = GlobalHelper.denullifyIntBy0Value(cliente.birthdayDay)
            this.birthdayMonth.value = GlobalHelper.denullifyIntBy0Value(cliente.birthdayMonth)
        }

    init {
        runLater(Duration.millis(200.0)) {
            firstTextField.requestFocus()
        }
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
                    firstTextField = textfield(model.nombre) {
                        validator(trigger = ValidationTrigger.OnChange()) {
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
                }
                field("Teléfono") {
                    textfield(model.telefono).validator(trigger = ValidationTrigger.OnChange()) {
                        when {
                            if (formType == CREATE && !it.isNullOrBlank()) clienteController.isTelefonoAvailable(it.toString())
                            else clienteController.existsOtherWithTelefono(it.toString(), model.id.value)
                            -> error("Teléfono no disponible")
                            !it.isNullOrBlank() && it.length > 20 -> error("Máximo 20 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Dirección") {
                    textfield(model.direccion).validator(trigger = ValidationTrigger.OnChange()) {
                        when {
                            !it.isNullOrBlank() && it.length > 100 -> error("Máximo 100 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                label("Valores de 0 son nulos.").style {
                    textFill = Color.BLUE
                }
                field("Cumpleaños") {
                    hbox(spacing = 20) {
                        field("Día") {
                            textfield(model.birthdayDay).validator {
                                when {
                                    !it.isNullOrBlank() && !it.isInt() -> error("Sólo puedes ingresar un número")
                                    (!it.isNullOrBlank() && it.toInt() == 0) &&
                                            (model.birthdayMonth.value.toInt() != 0) -> error("Debes ingresar tanto mes y día o ponerlos en 0")
                                    !it.isNullOrBlank() && (it.toInt() > 31 || it.toInt() < 0) -> error("Día inválido")
                                    else -> null
                                }
                            }
                        }
                        field("Mes") {
                            textfield(model.birthdayMonth).validator {
                                when {
                                    !it.isNullOrBlank() && !it.isInt() -> error("Sólo puedes ingresar un número")
                                    (!it.isNullOrBlank() && it.toInt() == 0) &&
                                            (model.birthdayDay.value.toInt() != 0) -> error("Debes ingresar tanto mes y día o ponerlos en 0")
                                    !it.isNullOrBlank() && (it.toInt() > 12 || it.toInt() < 0) -> error("Mes inválido")
                                    else -> null
                                }
                            }
                        }

                    }
                }
                rectangle(width = 0, height = 24)
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
                                        model.direccion.value,
                                        model.birthdayDay.value,
                                        model.birthdayMonth.value
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
        Joe.currentView.setValue(this@NewClienteFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}

class EditClienteFormView : Fragment() {
    override val root = BaseClienteFormView(EDIT, params["id"] as Int).root

    override fun onDock() {
        Joe.currentView.setValue(this@EditClienteFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}
