package xyz.acevedosharp.views.screens

import xyz.acevedosharp.controllers.ProveedorController
import xyz.acevedosharp.ui_models.Proveedor
import xyz.acevedosharp.ui_models.ProveedorModel
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

class ProveedorView : View("Módulo de proveedores") {

    private val proveedorController = find<ProveedorController>()
    private val model: ProveedorModel by inject()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty("")
    private var table: TableView<Proveedor> by singleAssign()
    private val view = this

    init {
        searchByNombre.onChange {
            table.items = proveedorController.proveedores.filter {
                it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase())
            }.asObservable()
        }

        proveedorController.proveedores.onChange {
            // force refresh
            table.items = proveedorController.proveedores.filter {
                it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase())
            }.asObservable()
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(PROVEEDORES, view))
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
                            openInternalWindow<NewProveedorFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditProveedorFormView>(
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
                    table = tableview(proveedorController.proveedores) {
                        column("Nombre", Proveedor::nombreProperty)
                        column("Teléfono", Proveedor::telefonoProperty)
                        column("Correo", Proveedor::correoProperty)
                        column("Dirección", Proveedor::direccionProperty).remainingWidth()

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

class BaseProveedorFormView(formType: FormType): Fragment() {

    private val proveedorController = find<ProveedorController>()
    private val model = if (formType == CREATE) ProveedorModel() else find(ProveedorModel::class)

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
                    textfield(model.nombre).validator {
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
                field("Teléfono") {
                    textfield(model.telefono).validator {
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
                    textfield(model.correo).validator {
                        when {
                            if (formType == CREATE) !it.isNullOrBlank() && proveedorController.isCorreoAvailable(it.toString())
                            else !it.isNullOrBlank() && proveedorController.existsOtherWithCorreo(it.toString(), model.id.value)
                            -> error("Correo no disponible")
                            !it.isNullOrBlank() && it.length > 40 -> error("Máximo 40 caracteres (${it.length})")
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
                                        proveedorController.add(
                                            Proveedor(
                                                null,
                                                model.nombre.value,
                                                model.telefono.value,
                                                model.direccion.value,
                                                model.correo.value
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
                                        proveedorController.edit(model.item)
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
class NewProveedorFormView : Fragment() {
    override val root = BaseProveedorFormView(CREATE).root
}

class EditProveedorFormView : Fragment() {
    override val root = BaseProveedorFormView(EDIT).root
}
