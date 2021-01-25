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

    @Column(name = "precio_total")
    var precioTotal: Int,

    @Column(name = "pago_recibido")
    var pagoRecibido: Int,

    @ManyToOne @JoinColumn(name = "empleado")
    var empleado: EmpleadoDB,

    @ManyToOne @JoinColumn(name = "cliente")
    var cliente: ClienteDB,

    @OneToMany(mappedBy = "venta", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var items: Set<ItemVentaDB>
) {
    val cambio: Int
    get() = pagoRecibido - precioTotal
    fun toModel() = Venta(
        ventaId,
        fechaHora.toLocalDateTime(),
        precioTotal,
        pagoRecibido,
        empleado,
        cliente
    )
}
