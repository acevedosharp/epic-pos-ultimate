package xyz.acevedosharp.persistence_layer.repositories

import xyz.acevedosharp.entities.ClienteDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepo: JpaRepository<ClienteDB, Int> {
    fun findByNombre(string: String): ClienteDB
    fun findByTelefono(string: String): ClienteDB

    fun existsByNombre(string: String): Boolean
    fun existsByTelefono(string: String): Boolean
}