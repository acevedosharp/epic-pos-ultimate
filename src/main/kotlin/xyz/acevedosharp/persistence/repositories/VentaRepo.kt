package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.VentaDB
import java.sql.Timestamp

@Repository
interface VentaRepo: JpaRepository<VentaDB, Int> {
    // oldest venta
    fun findFirstByOrderByVentaIdAsc(): VentaDB

    // newest venta
    fun findFirstByOrderByVentaIdDesc(): VentaDB

    fun findAllByFechaHoraBetween(start: Timestamp, end: Timestamp): List<VentaDB>
}
