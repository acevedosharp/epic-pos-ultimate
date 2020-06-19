package com.acevedosharp.controllers

import com.acevedosharp.CustomApplicationContextWrapper
import com.acevedosharp.entities.FamiliaDB
import com.acevedosharp.persistence_layer.repository_services.FamiliaService
import com.acevedosharp.ui_models.Familia
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller

class FamiliaController: Controller() {
    private val familiaService =
        find<CustomApplicationContextWrapper>().context.getBean<FamiliaService>(FamiliaService::class.java)

    val familias: ObservableList<Familia> = FXCollections.observableArrayList<Familia>(
        familiaService.all().map {
            Familia(
                it.familiaId,
                it.nombre
            )
        }
    )

    fun add(familia: Familia) {
        val res = familiaService.add(
            FamiliaDB(
                null,
                familia.nombre
            )
        )
        familias.add(familia.apply { id = res.familiaId })
    }

    fun edit(familia: Familia) {
        val res = familiaService.edit(
            FamiliaDB(
                familia.id,
                familia.nombre
            )
        )

        familia.apply {
            nombre = res.nombre
        }
    }


    fun isNombreAvailable(nombre: String): Boolean = familiaService.repo.existsByNombre(nombre)
    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return familiaService.repo.existsByNombre(nombre) && (familiaService.repo.findByNombre(nombre).familiaId != id)
    }
}