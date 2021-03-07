package xyz.acevedosharp.persistence.entities

import xyz.acevedosharp.ui_models.Cliente
import javax.persistence.*

@Entity
@Table(name = "cliente", schema = "epic")
class ClienteDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    var clienteId: Int? = null,

    @Column(name = "nombre")
    var nombre: String,

    @Column(name = "telefono")
    var telefono: String?,

    @Column(name = "direccion")
    var direccion: String?
) {
    fun toModel() = Cliente(
        clienteId,
        nombre,
        telefono,
        direccion
    )

    override fun toString() = nombre
}
