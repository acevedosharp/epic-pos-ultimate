package xyz.acevedosharp.entities;

import javax.persistence.*;

@Entity
@Table(name = "producto", schema = "epic")
public class ProductoDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "producto_id") Integer productoId;
    private @Column(name = "codigo") String codigo;
    private @Column(name = "desc_larga") String descripcionLarga;
    private @Column(name = "desc_corta") String descripcionCorta;
    private @Column(name = "existencias") Integer existencias;
    private @Column(name = "precio_venta") Double precioVenta;
    private @ManyToOne @JoinColumn(name = "familia") FamiliaDB familia;

    public ProductoDB() {}

    public ProductoDB(Integer productoId, String codigo, String descripcionLarga, String descripcionCorta, Integer existencias, Double precioVenta, FamiliaDB familia) {
        this.productoId = productoId;
        this.codigo = codigo;
        this.descripcionLarga = descripcionLarga;
        this.descripcionCorta = descripcionCorta;
        this.existencias = existencias;
        this.precioVenta = precioVenta;
        this.familia = familia;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public void setDescripcionLarga(String descripcionLarga) {
        this.descripcionLarga = descripcionLarga;
    }

    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    public void setDescripcionCorta(String descripcionCorta) {
        this.descripcionCorta = descripcionCorta;
    }

    public Integer getExistencias() {
        return existencias;
    }

    public void setExistencias(Integer existencias) {
        this.existencias = existencias;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public FamiliaDB getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaDB familia) {
        this.familia = familia;
    }
}