package xyz.acevedosharp.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "venta", schema = "epic")
public class VentaDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "venta_id") Integer ventaId;
    private @Column(name = "fecha_hora") Timestamp fechaHora;
    private @Column(name = "precio_total") Integer precioTotal;
    private @Column(name = "pago_recibido") Integer pagoRecibido;
    private @ManyToOne @JoinColumn(name = "empleado") EmpleadoDB empleado;
    private @ManyToOne @JoinColumn(name = "cliente") ClienteDB cliente;
    private @OneToMany(mappedBy = "venta", fetch = FetchType.LAZY, cascade = CascadeType.ALL) Set<ItemVentaDB> items;

    public VentaDB() {
    }

    public VentaDB(Integer ventaId, Timestamp fechaHora, Integer precioTotal, Integer pagoRecibido, EmpleadoDB empleado, ClienteDB cliente, Set<ItemVentaDB> items) {
        this.ventaId = ventaId;
        this.fechaHora = fechaHora;
        this.precioTotal = precioTotal;
        this.pagoRecibido = pagoRecibido;
        this.empleado = empleado;
        this.cliente = cliente;
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

    public ClienteDB getCliente() {
        return cliente;
    }

    public void setClienteDB(ClienteDB clienteDB) {
        this.cliente = cliente;
    }

    public Set<ItemVentaDB> getItems() {
        return items;
    }

    public void setItems(Set<ItemVentaDB> items) {
        this.items = items;
    }

    public Integer getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Integer precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Integer getPagoRecibido() {
        return pagoRecibido;
    }

    public void setPagoRecibido(Integer pagoRecibido) {
        this.pagoRecibido = pagoRecibido;
    }

    @Override public String toString() {
        return "VentaDB{" +
                "ventaId=" + ventaId +
                ", fechaHora=" + fechaHora +
                ", precioTotal=" + precioTotal +
                ", pagoRecibido=" + pagoRecibido +
                ", empleado=" + empleado +
                ", cliente=" + cliente +
                ", items=" + items +
                '}';
    }
}
