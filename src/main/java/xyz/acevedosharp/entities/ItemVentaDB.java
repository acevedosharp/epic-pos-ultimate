package xyz.acevedosharp.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "item_venta", schema = "epic")
public class ItemVentaDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "item_venta_id") Integer itemVentaId;
    private @Column(name = "fecha_hora") Timestamp fechaHora;
    private @Column(name = "cantidad") Integer cantidad;
    private @Column(name = "precio_venta") Double precioVenta;

    private @ManyToOne @JoinColumn(name = "producto") ProductoDB producto;
    private @ManyToOne @JoinColumn(name = "venta") VentaDB venta;

    public ItemVentaDB() {
    }

    public ItemVentaDB(Integer itemVentaId, Timestamp fechaHora, Integer cantidad, Double precioVenta, ProductoDB producto, VentaDB venta) {
        this.itemVentaId = itemVentaId;
        this.fechaHora = fechaHora;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.producto = producto;
        this.venta = venta;
    }

    public Integer getItemVentaId() {
        return itemVentaId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
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

    public Timestamp getFechaHora() {
        return fechaHora;
    }

}
