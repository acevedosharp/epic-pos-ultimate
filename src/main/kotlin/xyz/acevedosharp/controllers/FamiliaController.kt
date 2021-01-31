package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.Familia
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.*
import xyz.acevedosharp.persistence.entities.FamiliaDB
import xyz.acevedosharp.persistence.repositories.FamiliaRepo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FamiliaController: Controller(), UpdateSnapshot {
    private val familiaRepo = find<CustomApplicationContextWrapper>().context.getBean(FamiliaRepo::class.java)

    private val familias: ObservableList<FamiliaDB> = FXCollections.observableArrayList()

    fun getFamiliasWithUpdate(): ObservableList<FamiliaDB> {
        updateSnapshot()
        return familias
    }

    fun getFamiliasClean(): ObservableList<FamiliaDB> {
        return familias
    }

    fun findById(id: Int) = familiaRepo.findByIdOrNull(id)

    fun save(familia: Familia) {
        familiaRepo.save(
            FamiliaDB(
                familia.id,
                familia.nombre
            )
        )
        updateSnapshot()
    }

    fun isNombreAvailable(nombre: String): Boolean {
        return familiaRepo.existsByNombre(nombre)
    }
    fun existsOtherWithNombre(nombre: String, id: Int): Boolean {
        return familiaRepo.existsByNombre(nombre) && (familiaRepo.findByNombre(nombre).familiaId != id)
    }

    override fun updateSnapshot() {
        familias.setAll(familiaRepo.findAll())
    }
}
