package com.acevedosharp.persistence_access.repositories

import com.acevedosharp.entities.LoteDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoteRepo: JpaRepository<LoteDB, Int>