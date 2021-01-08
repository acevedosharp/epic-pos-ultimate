package xyz.acevedosharp.persistence_layer.repositories

import xyz.acevedosharp.entities.VentaDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import xyz.acevedosharp.ui_models.Venta

@Repository
interface VentaRepo: JpaRepository<VentaDB, Int> {
    // oldest venta
    fun findFirstByOrderByVentaIdAsc(): VentaDB

    // newest venta
    fun findFirstByOrderByVentaIdDesc(): VentaDB
}
