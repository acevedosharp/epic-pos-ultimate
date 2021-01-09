package xyz.acevedosharp.persistence_layer.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence_layer.entities.ProveedorDB

@Repository
interface ProveedorRepo: JpaRepository<ProveedorDB, Int> {
    fun findByNombre(string: String): ProveedorDB
    fun findByTelefono(string: String): ProveedorDB
    fun findByCorreo(string: String): ProveedorDB

    fun existsByNombre(string: String): Boolean
    fun existsByTelefono(string: String): Boolean
    fun existsByCorreo(string: String): Boolean
}
