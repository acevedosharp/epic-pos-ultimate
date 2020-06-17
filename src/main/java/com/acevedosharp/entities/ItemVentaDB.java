package com.acevedosharp.entities;

import javax.persistence.*;

@Entity
@Table(name = "item_venta", schema = "app")
public class ItemVentaDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "item_venta_id") Integer loteId;
    private @Column(name = "cantidad") Integer cantidad;
    private @Column(name = "precio_venta") Integer precioVenta;
    private @ManyToOne @JoinColumn(name = "producto") ProductoDB producto;
    private @ManyToOne @JoinColumn(name = "venta") VentaDB venta;

    public ItemVentaDB() {
    }

    public ItemVentaDB(Integer loteId, Integer cantidad, Integer precioVenta, ProductoDB producto, VentaDB venta) {
        this.loteId = loteId;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.producto = producto;
        this.venta = venta;
    }

    public Integer getLoteId() {
        return loteId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Integer precioVenta) {
        this.precioVenta = precioVenta;
    }

    public ProductoDB getProducto() {
        return producto;
    }

    public void setProducto(ProductoDB producto) {
        this.producto = producto;
    }

    public VentaDB getVenta() {
        return venta;
    }

    public void setVenta(VentaDB venta) {
        this.venta = venta;
    }
}
