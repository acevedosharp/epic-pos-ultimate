package xyz.acevedosharp.persistence.entities

import xyz.acevedosharp.ui_models.Producto
import javax.persistence.*

@Entity
@Table(name = "producto", schema = "epic")
class ProductoDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    var productoId: Int? = null,

    @Column(name = "codigo")
    var codigo: String,

    @Column(name = "desc_larga")
    var descripcionLarga: String,

    @Column(name = "desc_corta")
    var descripcionCorta: String,

    @Column(name = "existencias")
    var existencias: Int,

    @Column(name = "precio_venta")
    var precioVenta: Int,

    @Column(name = "precio_compra_efectivo")
    var precioCompraEfectivo: Int,

    @Column(name = "margen")
    var margen: Double,

    @ManyToOne @JoinColumn(name = "familia")
    var familia: FamiliaDB,

    @Column(name = "alerta_existencias")
    var alertaExistencias: Int
) {
    fun toModel() = Producto(
        productoId,
        codigo,
        descripcionLarga,
        descripcionCorta,
        precioVenta,
        precioCompraEfectivo,
        existencias,
        margen,
        familia,
        alertaExistencias
    )

    override fun toString() = descripcionCorta
}
