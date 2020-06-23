package com.acevedosharp.views.modules

import com.acevedosharp.controllers.EmpleadoController
import com.acevedosharp.ui_models.Empleado
import com.acevedosharp.ui_models.EmpleadoModel
import com.acevedosharp.views.helpers.FormType
import com.acevedosharp.views.helpers.FormType.*
import com.acevedosharp.views.helpers.CurrentModule.*
import com.acevedosharp.views.MainStylesheet
import com.acevedosharp.views.shared_components.SideNavigation
import com.acevedosharp.views.UnknownErrorDialog
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class EmpleadoView : View("Módulo de empleados") {

    private val empleadoController = find<EmpleadoController>()
    private val model: EmpleadoModel by inject()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty()
    private var table: TableView<Empleado> by singleAssign()
    private val view = this

    init {
        searchByNombre.onChange {
            table.items = empleadoController.empleados.filter {
                it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase())
            }.asObservable()
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(EMPLEADOS, view))
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
                            openInternalWindow<NewEmpleadoFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditEmpleadoFormView>(
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
                    table = tableview(empleadoController.empleados) {
                        column("Nombre", Empleado::nombreProperty)
                        column("Teléfono", Empleado::telefonoProperty)

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

class BaseEmpleadoFormField(formType: FormType): Fragment() {

    private val empleadoController = find<EmpleadoController>()
    private val model = if (formType == CREATE) EmpleadoModel() else find(EmpleadoModel::class)

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
                    textfield(model.nombre).validator {
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
                field("Teléfono") {
                    textfield(model.telefono).validator {
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
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                        action {
                            if (formType == CREATE) {
                                try {
                                    model.commit {
                                        empleadoController.add(
                                            Empleado(
                                                null,
                                                model.nombre.value,
                                                model.telefono.value
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
                                        empleadoController.edit(model.item)
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
class NewEmpleadoFormView : Fragment() {
    override val root = BaseEmpleadoFormField(CREATE).root
}

class EditEmpleadoFormView : Fragment() {
    override val root = BaseEmpleadoFormField(EDIT).root
}