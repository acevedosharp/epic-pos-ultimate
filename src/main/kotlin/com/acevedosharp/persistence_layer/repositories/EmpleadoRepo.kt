package com.acevedosharp.persistence_layer.repositories

import com.acevedosharp.entities.EmpleadoDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmpleadoRepo: JpaRepository<EmpleadoDB, Int> {
    fun findByNombre(string: String): EmpleadoDB
    fun findByTelefono(string: String): EmpleadoDB

    fun existsByNombre(string: String): Boolean
    fun existsByTelefono(string: String): Boolean
}