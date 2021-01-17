package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.ClienteDB

@Repository
interface ClienteRepo: JpaRepository<ClienteDB, Int> {
    fun findByNombre(string: String): ClienteDB
    fun findByTelefono(string: String): ClienteDB

    fun existsByNombre(string: String): Boolean
    fun existsByTelefono(string: String): Boolean
}
