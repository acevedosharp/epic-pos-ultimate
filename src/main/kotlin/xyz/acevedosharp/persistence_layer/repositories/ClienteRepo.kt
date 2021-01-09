package xyz.acevedosharp.persistence_layer.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence_layer.entities.ClienteDB

@Repository
interface ClienteRepo: JpaRepository<ClienteDB, Int> {
    fun findByNombre(string: String): ClienteDB
    fun findByTelefono(string: String): ClienteDB

    fun existsByNombre(string: String): Boolean
    fun existsByTelefono(string: String): Boolean
}
