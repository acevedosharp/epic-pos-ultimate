package xyz.acevedosharp.views.dialogs

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.input.KeyCode
import javafx.util.Duration
import tornadofx.*
import xyz.acevedosharp.GlobalHelper
import xyz.acevedosharp.Joe
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.helpers.CurrentModule
import xyz.acevedosharp.views.helpers.CurrentModuleHelper
import xyz.acevedosharp.views.helpers.SecuritySettings
import xyz.acevedosharp.views.screens.PuntoDeVentaView

class PasswordDialog(currentView: View, tag: CurrentModule) : Fragment() {

    private val passwordProperty = SimpleStringProperty("")
    private lateinit var passField: PasswordField
    private lateinit var aceptarButton: Button

    init {
        if (currentView is PuntoDeVentaView) {
            currentView.removeAlwaysFocusListener()

            GlobalHelper.runLaterMinimumDelay {
                passField.requestFocus()
            }
        }
    }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("Ingresa la contraseña") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.blueLabel)
        }
        form {
            fieldset {
                field("Contraseña") {
                    passField = passwordfield(passwordProperty)
                    setOnKeyPressed { keyEvent ->
                        if (keyEvent.code == KeyCode.ENTER) {
                            aceptarButton.fire()
                        }
                    }
                }
            }
        }
        hbox(spacing = 80, alignment = Pos.CENTER) {
            aceptarButton = button("Aceptar") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.greenButton)
                addClass(MainStylesheet.expandedButton)
                action {
                    if (passField.text == SecuritySettings.password) {
                        close()
                        val targetView = CurrentModuleHelper.screenMappings[tag]!!
                        if (targetView::class == PuntoDeVentaView::class) {
                            // don't keep items in ivs while prices may change
                            (targetView as PuntoDeVentaView).clearAllIVS()
                        }
                        Joe.currentView.setValue(targetView)
                        currentView.replaceWith(targetView)
                    } else {
                        this@PasswordDialog.openInternalWindow(GenericErrorDialog("Contraseña incorrecta"))
                    }
                }
            }
            button("Cancelar") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.redButton)
                addClass(MainStylesheet.expandedButton)
                action {
                    if (currentView is PuntoDeVentaView) {
                        currentView.addAlwaysFocusListener()
                    }
                    close()
                }
            }
        }
    }
}