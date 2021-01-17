package xyz.acevedosharp.views.screens

import xyz.acevedosharp.controllers.FamiliaController
import xyz.acevedosharp.ui_models.Familia
import xyz.acevedosharp.ui_models.FamiliaModel
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
import xyz.acevedosharp.persistence.entities.FamiliaDB

class FamiliaView : View("Módulo de familias") {

    private val familiaController = find<FamiliaController>()
    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty("")
    private var table: TableView<FamiliaDB> by singleAssign()
    private val view = this

    init {
        Joe.currentView = view

        searchByNombre.onChange {
            table.items = familiaController.familias.filter {
                it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase())
            }.asObservable()
        }

        familiaController.familias.onChange {
            table.items = familiaController.familias.filter {
                it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase())
            }.asObservable()
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(FAMILIAS, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nueva Familia") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewFamiliaFormView>(
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
                            openInternalWindow<EditFamiliaFormView>(
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
                    table = tableview(familiaController.familias) {
                        column("Nombre", FamiliaDB::nombre)

                        smartResize()

                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                            if (it != null) {
                                selectedId.set(it.familiaId!!)
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

class BaseFamiliaFormView(formType: FormType, id: Int?) : Fragment() {

    private val familiaController = find<FamiliaController>()
    private val model = if (formType == CREATE)
        FamiliaModel()
    else
        FamiliaModel().apply {
            val familia = familiaController.findById(id!!)!!.toModel()

            this.id.value = familia.id
            this.nombre.value = familia.nombre
        }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 800.0
        label(if (formType == CREATE) "Nueva Familia" else "Editar Familia") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(if (formType == CREATE) MainStylesheet.greenLabel else MainStylesheet.blueLabel)
        }
        form {
            fieldset {
                field("Nombre") {
                    textfield(model.nombre).validator(trigger = ValidationTrigger.OnBlur) {
                        when {
                            if (formType == CREATE) familiaController.isNombreAvailable(it.toString())
                            else familiaController.existsOtherWithNombre(it.toString(), model.id.value)
                            -> error("Nombre no disponible")
                            it.isNullOrBlank() -> error("Nombre requerido")
                            it.length > 30 -> error("Máximo 30 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                        action {
                            model.commit {
                                familiaController.save(model.item)
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

// 1. These com.acevedosharp.views need to be accesible from anywhere so that they can be used in other modules for convenience.
class NewFamiliaFormView : Fragment() {
    override val root = BaseFamiliaFormView(CREATE, null).root

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}

class EditFamiliaFormView : Fragment() {
    override val root = BaseFamiliaFormView(EDIT, params["id"] as Int).root

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}
