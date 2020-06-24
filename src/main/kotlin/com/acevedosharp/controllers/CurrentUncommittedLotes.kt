package com.acevedosharp.controllers

import com.acevedosharp.ui_models.Lote
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class CurrentUncommittedLotes : Controller() {
    val lotes: ObservableList<Lote> = FXCollections.observableArrayList<Lote>()
}
