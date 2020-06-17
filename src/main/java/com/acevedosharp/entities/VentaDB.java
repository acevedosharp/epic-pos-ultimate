package com.acevedosharp.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "venta", schema = "app")
public class VentaDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "venta_id") Integer ventaId;
    private @Column(name = "fecha_hora") Timestamp fechaHora;
    private @ManyToOne @JoinColumn(name = "empleado") EmpleadoDB empleado;
    private @ManyToOne @JoinColumn(name = "cliente") ClienteDB clienteDB;
    private @OneToMany(mappedBy = "venta", fetch = FetchType.LAZY, cascade = CascadeType.ALL) Set<ItemVentaDB> items;

    public VentaDB() {
    }

    public VentaDB(Integer ventaId, Timestamp fechaHora, EmpleadoDB empleado, ClienteDB clienteDB, Set<ItemVentaDB> items) {
        this.ventaId = ventaId;
        this.fechaHora = fechaHora;
        this.empleado = empleado;
        this.clienteDB = clienteDB;
        this.items = items;
    }

    public Integer getVentaId() {
        return ventaId;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EmpleadoDB getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoDB empleado) {
        this.empleado = empleado;
    }

    public ClienteDB getClienteDB() {
        return clienteDB;
    }

    public void setClienteDB(ClienteDB clienteDB) {
        this.clienteDB = clienteDB;
    }

    public Set<ItemVentaDB> getItems() {
        return items;
    }

    public void setItems(Set<ItemVentaDB> items) {
        this.items = items;
    }
}
