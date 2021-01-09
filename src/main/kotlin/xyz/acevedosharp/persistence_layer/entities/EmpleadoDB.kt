package xyz.acevedosharp.persistence_layer.entities

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
)
