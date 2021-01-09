package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.persistence_layer.repository_services.FamiliaService
import xyz.acevedosharp.ui_models.Familia
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Parent
import tornadofx.*
import xyz.acevedosharp.persistence_layer.entities.FamiliaDB
import xyz.acevedosharp.views.NoInternetConnectionErrorDialog

class FamiliaController: Controller(), UpdateSnapshot {
    private val familiaService =
        find<CustomApplicationContextWrapper>().context.getBean(FamiliaService::class.java)

    val familias: ObservableList<Familia> = FXCollections.observableArrayList()

    init {
        updateSnapshot()
    }

    fun add(familia: Familia) {
        familiaService.add(
            FamiliaDB(
                null,
                familia.nombre
            )
        )
        updateSnapshot()
    }

    fun edit(familia: Familia) {
        familiaService.edit(
            FamiliaDB(
                familia.id,
                familia.nombre
            )
        )
        updateSnapshot()
    }


    fun isNombreAvailable(nombre: String): Boolean {
        return familiaService.repo.existsByNombre(nombre)
    }
    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return familiaService.repo.existsByNombre(nombre) && (familiaService.repo.findByNombre(nombre).familiaId != id)
    }

    override fun updateSnapshot() {
        familias.setAll(
            familiaService.all().map {
                Familia(
                    it.familiaId,
                    it.nombre
                )
            }
        )
    }
}
