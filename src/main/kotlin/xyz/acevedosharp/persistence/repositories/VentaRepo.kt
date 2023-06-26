package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.acevedosharp.persistence.entities.ClienteDB
import xyz.acevedosharp.persistence.entities.VentaDB
import java.sql.Timestamp

@Repository
interface VentaRepo: JpaRepository<VentaDB, Int> {
    fun findFirstByOrderByVentaIdAsc(): VentaDB? // oldest venta
    fun findFirstByOrderByVentaIdDesc(): VentaDB? // newest venta
    fun findAllByFechaHoraBetween(start: Timestamp, end: Timestamp): List<VentaDB>
    fun countAllByFechaHoraBetween(start: Timestamp, end: Timestamp): Int
}
