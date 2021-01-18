package xyz.acevedosharp.persistence.entities

import xyz.acevedosharp.ui_models.Empleado
import javax.persistence.*

@Entity
@Table(name = "empleado", schema = "epic")
class EmpleadoDB (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empleado_id")
    var empleadoId: Int? = null,

    @Column(name = "nombre")
    var nombre: String,

    @Column(name = "telefono")
    var telefono: String
) {
    fun toModel() = Empleado(
        empleadoId,
        nombre,
        telefono
    )

    override fun toString() = nombre
}
