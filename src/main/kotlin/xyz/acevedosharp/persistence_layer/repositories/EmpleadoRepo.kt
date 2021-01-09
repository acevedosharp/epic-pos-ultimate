package xyz.acevedosharp.persistence_layer.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence_layer.entities.EmpleadoDB

@Repository
interface EmpleadoRepo: JpaRepository<EmpleadoDB, Int> {
    fun findByNombre(string: String): EmpleadoDB
    fun findByTelefono(string: String): EmpleadoDB

    fun existsByNombre(string: String): Boolean
    fun existsByTelefono(string: String): Boolean
}
