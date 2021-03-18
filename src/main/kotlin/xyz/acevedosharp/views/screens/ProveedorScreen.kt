package xyz.acevedosharp.views.screens

import xyz.acevedosharp.controllers.ProveedorController
import xyz.acevedosharp.ui_models.Proveedor
import xyz.acevedosharp.ui_models.ProveedorModel
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
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence.entities.ProveedorDB

class ProveedorView : View("Módulo de proveedores") {
    private val proveedorController = find<ProveedorController>()

    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty("")
    private var table: TableView<ProveedorDB> by singleAssign()

    init {
        Joe.currentView.setValue(this@ProveedorView)

        searchByNombre.onChange { searchString ->
            if (searchString != null) {
                table.items = proveedorController.getProveedoresClean().filter {
                    it.nombre.toLowerCase().contains(searchString.toLowerCase())
                }.asObservable()
            }
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(PROVEEDORES, this@ProveedorView))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Proveedor") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewProveedorFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("owner" to this@ProveedorView)
                            )
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditProveedorFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf(
                                    "id" to selectedId.value,
                                    "owner" to this@ProveedorView
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
                    table = tableview(proveedorController.getProveedoresWithUpdate()) {
                        column("Nombre", ProveedorDB::nombre)
                        column("Teléfono", ProveedorDB::telefono)
                        column("Correo", ProveedorDB::correo)
                        column("Dirección", ProveedorDB::direccion).remainingWidth()
                        smartResize()

                        proveedorController.getProveedoresClean().onChange {
                            if (searchByNombre.value == "")
                                table.items = proveedorController.getProveedoresClean().asObservable()
                            else
                                searchByNombre.value = ""
                        }

                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                            if (it != null) {
                                selectedId.set(it.proveedorId!!)
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

class BaseProveedorFormView(formType: FormType, id: Int?) : Fragment() {
    private val proveedorController = find<ProveedorController>()

    private var firstTextField: TextField by singleAssign()

    private val model = if (formType == CREATE)
        ProveedorModel()
    else
        ProveedorModel().apply {
            val proveedor = proveedorController.findById(id!!)!!

            this.id.value = proveedor.proveedorId
            this.nombre.value = proveedor.nombre
            this.telefono.value = proveedor.telefono
            this.direccion.value = proveedor.direccion
            this.correo.value = proveedor.correo
        }

    init {
        runLater(Duration.millis(200.0)) {
            firstTextField.requestFocus()
        }
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 800.0
        label(if (formType == CREATE) "Nuevo Proveedor" else "Editar Proveedor") {
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
                                if (formType == CREATE) proveedorController.isNombreAvailable(it.toString())
                                else proveedorController.existsOtherWithNombre(it.toString(), model.id.value)
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
                            if (formType == CREATE) proveedorController.isTelefonoAvailable(it.toString())
                            else proveedorController.existsOtherWithTelefono(it.toString(), model.id.value)
                            -> error("Teléfono no disponible")
                            it.isNullOrBlank() -> error("Teléfono requerido")
                            it.length > 20 -> error("Máximo 20 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Correo") {
                    textfield(model.correo).validator(trigger = ValidationTrigger.OnChange()) {
                        when {
                            if (formType == CREATE && !it.isNullOrBlank()) proveedorController.isCorreoAvailable(it.toString())
                            else proveedorController.existsOtherWithCorreo(it.toString(), model.id.value)
                            -> error("Correo no disponible")
                            !it.isNullOrBlank() && it.length > 40 -> error("Máximo 40 caracteres (${it.length})")
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
                rectangle(width = 0, height = 24)
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                        action {
                            model.commit {
                                proveedorController.save(
                                    Proveedor(
                                        if (formType == CREATE) null else model.id.value,
                                        model.nombre.value,
                                        model.telefono.value,
                                        model.direccion.value,
                                        model.correo.value
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


class NewProveedorFormView : Fragment() {
    override val root = BaseProveedorFormView(CREATE, null).root

    override fun onDock() {
        Joe.currentView.setValue(this@NewProveedorFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}

class EditProveedorFormView : Fragment() {
    override val root = BaseProveedorFormView(EDIT, params["id"] as Int).root

    override fun onDock() {
        Joe.currentView.setValue(this@EditProveedorFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}
