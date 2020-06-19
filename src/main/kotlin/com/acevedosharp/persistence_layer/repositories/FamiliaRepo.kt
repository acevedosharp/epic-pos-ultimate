package com.acevedosharp.persistence_layer.repositories

import com.acevedosharp.entities.FamiliaDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FamiliaRepo: JpaRepository<FamiliaDB, Int> {
    fun findByNombre(string: String): FamiliaDB

    fun existsByNombre(string: String): Boolean
}