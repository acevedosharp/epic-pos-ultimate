package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.FamiliaDB

@Repository
interface FamiliaRepo: JpaRepository<FamiliaDB, Int> {
    fun findByNombre(string: String): FamiliaDB

    fun existsByNombre(string: String): Boolean
}
