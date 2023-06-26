package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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
    @Query(nativeQuery = true, value = """
        select v.empleado as employee, sum(v.total_con_iva) as totalSales, count(*) as numSales from venta as v
        where v.fecha_hora between ?1 and ?2
        group by v.empleado
    """)
    fun aggregateSalesPerEmployee(start: Timestamp, end: Timestamp): List<ISalesPerEmployee>

    interface ISalesPerEmployee {
        fun getEmployee(): Int
        fun getTotalSales(): Double
        fun getNumSales(): Int
    }
}
