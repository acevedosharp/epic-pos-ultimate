package com.acevedosharp.controllers

import javafx.collections.FXCollections
import com.acevedosharp.ui_models.Proveedor
import tornadofx.Controller

class ProveedorController : Controller() {
    val proveedores = FXCollections.observableArrayList(
        Proveedor("Datastax", "3022175285", "", "admin@datastax.com"),
        Proveedor("Amazon Web Services", "3022175286", "Seattle, WA", "admin@aws.com"),
        Proveedor("Google Cloud Platform", "3022175287", "Mountain View, CA", "admin@google.com")
    )
}