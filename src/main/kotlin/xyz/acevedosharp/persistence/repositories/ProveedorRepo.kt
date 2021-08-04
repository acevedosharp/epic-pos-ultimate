package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.ProveedorDB

@Repository
interface ProveedorRepo: JpaRepository<ProveedorDB, Int> {
    fun findByNombre(string: String): ProveedorDB
    fun findByNit(string: String): ProveedorDB
    fun findByTelefono(string: String): ProveedorDB
    fun findByCorreo(string: String): ProveedorDB

    fun existsByNombre(string: String): Boolean
    fun existsByNit(string: String): Boolean
    fun existsByTelefono(string: String): Boolean
    fun existsByCorreo(string: String): Boolean
}
