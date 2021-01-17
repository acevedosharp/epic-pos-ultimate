package xyz.acevedosharp.persistence.entities

import xyz.acevedosharp.ui_models.Familia
import javax.persistence.*

@Entity
@Table(name = "familia", schema = "epic")
class FamiliaDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "familia_id")
    var familiaId: Int? = null,

    @Column(name = "nombre")
    var nombre: String
) {
    fun toModel() = Familia(
        familiaId,
        nombre
    )

    override fun toString() = nombre
}
