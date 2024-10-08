package xyz.acevedosharp.views.screens

import xyz.acevedosharp.controllers.EmpleadoController
import xyz.acevedosharp.ui_models.Empleado
import xyz.acevedosharp.ui_models.EmpleadoModel
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
import xyz.acevedosharp.persistence.entities.EmpleadoDB

class EmpleadoView : View("Epic POS - Empleados") {
    private val empleadoController = find<EmpleadoController>()

    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty("")
    private var table: TableView<EmpleadoDB> by singleAssign()

    init {
        searchByNombre.onChange { searchString ->
            if (searchString != null) {
                table.items = empleadoController.getEmpleadosClean().filter {
                    it.nombre.toLowerCase().contains(searchString.toLowerCase())
                }.asObservable()
            }
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(EMPLEADOS, this@EmpleadoView))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Empleado") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewEmpleadoFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("owner" to this@EmpleadoView)
                            )
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditEmpleadoFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf(
                                    "id" to selectedId.value,
                                    "owner" to this@EmpleadoView
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
                    table = tableview(empleadoController.getEmpleadosWithUpdate()) {
                        column("Nombre", EmpleadoDB::nombre)
                        column("Teléfono", EmpleadoDB::telefono)
                        smartResize()

                        placeholder = label("No hay empleados")

                        empleadoController.getEmpleadosClean().onChange {
                            if (searchByNombre.value == "")
                                table.items = empleadoController.getEmpleadosClean().asObservable()
                            else
                                searchByNombre.value = ""
                        }

                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                            if (it != null) {
                                selectedId.set(it.empleadoId!!)
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

class BaseEmpleadoFormView(formType: FormType, id: Int?) : Fragment() {
    private val empleadoController = find<EmpleadoController>()

    private var firstTextField: TextField by singleAssign()

    private val model = if (formType == CREATE)
        EmpleadoModel()
    else
        EmpleadoModel().apply {
            val empleado = empleadoController.findById(id!!)!!

            this.id.value = empleado.empleadoId
            this.nombre.value = empleado.nombre
            this.telefono.value = empleado.telefono
        }

    init {
        GlobalHelper.runLaterMinimumDelay {
            firstTextField.requestFocus()
        }
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 800.0
        label(if (formType == CREATE) "Nuevo Empleado" else "Editar Empleado") {
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
                                if (formType == CREATE) empleadoController.isNombreAvailable(it.toString())
                                else empleadoController.existsOtherWithNombre(it.toString(), model.id.value)
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
                            if (formType == CREATE) empleadoController.isTelefonoAvailable(it.toString())
                            else empleadoController.existsOtherWithTelefono(it.toString(), model.id.value)
                            -> error("Teléfono no disponible")
                            it.isNullOrBlank() -> error("Teléfono requerido")
                            it.length > 20 -> error("Máximo 20 caracteres (${it.length})")
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
                                empleadoController.save(
                                    Empleado(
                                        if (formType == CREATE) null else model.id.value,
                                        model.nombre.value,
                                        model.telefono.value
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

class NewEmpleadoFormView : Fragment() {
    override val root = BaseEmpleadoFormView(CREATE, null).root

    override fun onDock() {
        Joe.currentView.setValue(this@NewEmpleadoFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}

class EditEmpleadoFormView : Fragment() {
    override val root = BaseEmpleadoFormView(EDIT, params["id"] as Int).root

    override fun onDock() {
        Joe.currentView.setValue(this@EditEmpleadoFormView)
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView.setValue(params["owner"] as UIComponent)
        super.onUndock()
    }
}
