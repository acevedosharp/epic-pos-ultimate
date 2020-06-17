package com.acevedosharp.entities;

import javax.persistence.*;

@Entity
@Table(name = "Lote", schema = "app")
public class LoteDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "lote_id") Integer loteId;
    private @Column(name = "cantidad") Integer cantidad;
    private @Column(name = "precio_compra") Integer precioCompra;
    private @ManyToOne @JoinColumn(name = "producto") ProductoDB producto;
    private @ManyToOne @JoinColumn(name = "pedido") PedidoDB pedido;

    public LoteDB() {
    }

    public LoteDB(Integer loteId, Integer cantidad, Integer precioCompra, ProductoDB producto, PedidoDB pedido) {
        this.loteId = loteId;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
        this.producto = producto;
        this.pedido = pedido;
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

    public Integer getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Integer precioUnitarioCompra) {
        this.precioCompra = precioUnitarioCompra;
    }

    public ProductoDB getProducto() {
        return producto;
    }

    public void setProducto(ProductoDB producto) {
        this.producto = producto;
    }

    public PedidoDB getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDB pedido) {
        this.pedido = pedido;
    }
}

