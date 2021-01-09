package xyz.acevedosharp.persistence_layer.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "lote", schema = "epic")
class LoteDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lote_id")
    var loteId: Int? = null,

    @Column(name = "cantidad")
    var cantidad: Int,

    @Column(name = "precio_compra")
    var precioCompra: Double,

    @ManyToOne @JoinColumn(name = "producto")
    var producto: ProductoDB,

    @ManyToOne @JoinColumn(name = "pedido")
    var pedido: PedidoDB
)
