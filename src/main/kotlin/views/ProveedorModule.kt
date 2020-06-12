package views

import controllers.ProveedorController
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import misc.FormType
import misc.FormType.*
import models.CurrentModule.*
import models.Proveedor
import models.ProveedorModel
import styles.MainStylesheet
import tornadofx.*

class ProveedorView : View("Módulo de proveedores") {

    private val proveedorController = find<ProveedorController>()
    private val model: ProveedorModel by inject()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty()
    private var table: TableView<Proveedor> by singleAssign()
    private val view = this

    init {
        searchByNombre.onChange {
            table.items = FXCollections.observableArrayList(proveedorController.proveedores.filter { it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase()) })
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
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewProveedorFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditProveedorFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Eliminar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.redButton)
                        action {
                            openInternalWindow<ConfirmDeleteProveedorDialog>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("selected" to table.selectedItem)
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
                        column("Dirección", Proveedor::direccionProperty)
                        column("Correo", Proveedor::correoProperty)

                        smartResize()

                        bindSelected(model)
                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
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

class BaseProveedorFormField(formType: FormType): Fragment() {

    private val model = if (formType == CREATE) ProveedorModel() else find(ProveedorModel::class)

    override val root = vbox(spacing = 0) {
        useMaxSize = true
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
                            it.isNullOrBlank() -> error("Nombre requerido")
                            it.length > 30 -> error("Máximo 30 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Teléfono") {
                    textfield(model.telefono).validator {
                        when {
                            it.isNullOrBlank() -> error("Teléfono requerido")
                            it.length > 10 -> error("Máximo 10 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Dirección") {
                    textfield(model.direccion).validator {
                        when {
                            it.isNullOrBlank() -> error("Dirección requerida")
                            it.length > 80 -> error("Máximo 80 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Correo") {
                    textfield(model.correo).validator {
                        when {
                            it.isNullOrBlank() -> error("Correo requerido")
                            it.length > 25 -> error("Máximo 25 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.greenButton)
                        addClass(MainStylesheet.expandedButton)
                        action {
                            if (formType == CREATE) {
                                model.commit {
                                    find<ProveedorController>().proveedores.add(
                                        Proveedor(
                                            model.nombre.value,
                                            model.telefono.value,
                                            model.direccion.value,
                                            model.correo.value
                                        )
                                    )
                                    close()
                                }
                            } else {
                                model.commit()
                                close()
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.redButton)
                        addClass(MainStylesheet.expandedButton)
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

// 1. These views need to be accesible from anywhere so that they can be used in other modules for convenience.
class NewProveedorFormView : Fragment() {
    override val root = BaseProveedorFormField(CREATE).root
}

class EditProveedorFormView : View() {
    override val root = BaseProveedorFormField(EDIT).root
}

class ConfirmDeleteProveedorDialog : View() {
    val proveedorController: ProveedorController by inject()

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("¿Está seguro de eliminar la selección?") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.redLabel)
        }
        label("Esta acción no se puede deshacer. ¿Confirmar?").style {
            padding = box(vertical = 30.px, horizontal = 5.px)
        }
        hbox(spacing = 80, alignment = Pos.CENTER) {
            button("Sí") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.greenButton)
                addClass(MainStylesheet.expandedButton)
                action {
                    proveedorController.proveedores.remove(params["selected"])
                    close()
                }
            }
            button("No") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.redButton)
                addClass(MainStylesheet.expandedButton)
                action { close() }
            }
        }
    }
}