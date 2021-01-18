package xyz.acevedosharp.persistence.entities

import xyz.acevedosharp.ui_models.Lote
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
) {
    fun toModel() = Lote(
        loteId,
        cantidad,
        precioCompra,
        producto
    )
}
