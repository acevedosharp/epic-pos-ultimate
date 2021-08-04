package xyz.acevedosharp.persistence.entities

import xyz.acevedosharp.ui_models.Venta
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "venta", schema = "epic")
class VentaDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    var ventaId: Int? = null,

    @Column(name = "fecha_hora")
    var fechaHora: Timestamp,

    @Column(name = "total_sin_iva")
    var totalSinIva: Double,

    @Column(name = "pago_recibido")
    var pagoRecibido: Int,

    @ManyToOne @JoinColumn(name = "empleado")
    var empleado: EmpleadoDB,

    @ManyToOne @JoinColumn(name = "cliente")
    var cliente: ClienteDB,

    @OneToMany(mappedBy = "venta", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var items: Set<ItemVentaDB>,

    @Column(name = "total_con_iva")
    var totalConIva: Int
) {
    val cambio: Int
        get() = pagoRecibido - totalConIva

    fun toModel() = Venta(
        ventaId,
        fechaHora.toLocalDateTime(),
        totalSinIva,
        pagoRecibido,
        empleado,
        cliente,
        totalConIva
    )
}
