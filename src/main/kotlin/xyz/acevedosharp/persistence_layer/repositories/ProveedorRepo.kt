package xyz.acevedosharp.persistence_layer.repositories

import xyz.acevedosharp.entities.ProveedorDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProveedorRepo: JpaRepository<ProveedorDB, Int> {
    fun findByNombre(string: String): ProveedorDB
    fun findByTelefono(string: String): ProveedorDB
    fun findByCorreo(string: String): ProveedorDB

    fun existsByNombre(string: String): Boolean
    fun existsByTelefono(string: String): Boolean
    fun existsByCorreo(string: String): Boolean
}