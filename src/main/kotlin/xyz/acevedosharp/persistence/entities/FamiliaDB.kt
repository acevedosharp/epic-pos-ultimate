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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FamiliaDB

        if (familiaId != other.familiaId) return false
        if (nombre != other.nombre) return false

        return true
    }

    override fun hashCode(): Int {
        var result = familiaId ?: 0
        result = 31 * result + nombre.hashCode()
        return result
    }

}
