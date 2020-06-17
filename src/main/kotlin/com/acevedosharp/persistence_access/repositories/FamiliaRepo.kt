package com.acevedosharp.persistence_access.repositories

import com.acevedosharp.entities.FamiliaDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FamiliaRepo: JpaRepository<FamiliaDB, Int>