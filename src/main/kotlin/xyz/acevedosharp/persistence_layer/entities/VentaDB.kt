package xyz.acevedosharp.persistence_layer.entities

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
    val items: Set<ItemVentaDB>
)
