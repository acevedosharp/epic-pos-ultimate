package xyz.acevedosharp.persistence_layer.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "pedido", schema = "epic")
class PedidoDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    var pedidoId: Int? = null,

    @Column(name = "fecha_hora")
    var fechaHora: Timestamp,

    @ManyToOne @JoinColumn(name = "proveedor")
    var proveedor: ProveedorDB,

    @ManyToOne @JoinColumn(name = "empleado")
    var empleado: EmpleadoDB,

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val lotes: Set<LoteDB>
)
