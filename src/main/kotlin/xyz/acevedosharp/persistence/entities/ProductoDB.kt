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
    var precioVenta: Double,

    @Column(name = "precio_compra_efectivo")
    var precioCompraEfectivo: Double,

    @Column(name = "margen")
    var margen: Double,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "familia")
    var familia: FamiliaDB,

    @Column(name = "alerta_existencias")
    var alertaExistencias: Int,

    @Column(name = "iva")
    var iva: Int,

    @Column(name = "precio_compra")
    var precioCompra: Double
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
        alertaExistencias,
        iva,
        precioCompra
    )

    override fun toString() = descripcionLarga
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductoDB

        if (productoId != other.productoId) return false
        if (codigo != other.codigo) return false
        if (descripcionLarga != other.descripcionLarga) return false
        if (descripcionCorta != other.descripcionCorta) return false
        if (existencias != other.existencias) return false
        if (precioVenta != other.precioVenta) return false
        if (precioCompraEfectivo != other.precioCompraEfectivo) return false
        if (margen != other.margen) return false
        if (familia != other.familia) return false
        if (alertaExistencias != other.alertaExistencias) return false
        if (iva != other.iva) return false
        if (precioCompra != other.precioCompra) return false

        return true
    }

    override fun hashCode(): Int {
        var result = productoId ?: 0
        result = 31 * result + codigo.hashCode()
        result = 31 * result + descripcionLarga.hashCode()
        result = 31 * result + descripcionCorta.hashCode()
        result = 31 * result + existencias
        result = 31 * result + precioVenta.hashCode()
        result = 31 * result + precioCompraEfectivo.hashCode()
        result = 31 * result + margen.hashCode()
        result = 31 * result + familia.hashCode()
        result = 31 * result + alertaExistencias
        result = 31 * result + iva
        result = 31 * result + precioCompra.hashCode()
        return result
    }


}
