package xyz.acevedosharp.persistence.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "item_venta", schema = "epic")
class ItemVentaDB(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_venta_id")
    var itemVentaId: Int? = null,

    @Column(name = "fecha_hora")
    var fechaHora: Timestamp,

    @Column(name = "cantidad")
    var cantidad: Int,

    @Column(name = "precio_venta_con_iva")
    var precioVentaConIva: Int,

    @ManyToOne @JoinColumn(name = "producto")
    var producto: ProductoDB,

    @ManyToOne @JoinColumn(name = "venta")
    var venta: VentaDB,

    @ManyToOne @JoinColumn(name = "cliente")
    var cliente: ClienteDB,

    @Column(name = "precio_venta_sin_iva")
    var precioVentaSinIva: Double,

    @Column(name = "iva")
    var iva: Int,

    @Column(name = "margen")
    var margen: Double
)
