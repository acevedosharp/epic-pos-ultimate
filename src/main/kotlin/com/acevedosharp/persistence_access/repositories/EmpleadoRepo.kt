package com.acevedosharp.persistence_access.repositories

import com.acevedosharp.entities.EmpleadoDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmpleadoRepo: JpaRepository<EmpleadoDB, Int>