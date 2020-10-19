package xyz.acevedosharp.persistence_layer.repositories

import xyz.acevedosharp.entities.VentaDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VentaRepo: JpaRepository<VentaDB, Int>