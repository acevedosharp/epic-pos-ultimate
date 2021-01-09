package xyz.acevedosharp.persistence_layer.entities

import javax.persistence.*

@Entity
@Table(name = "familia", schema = "epic")
class FamiliaDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "familia_id")
    var familiaId: Int? = null,

    @Column(name = "nombre")
    var nombre: String
)
