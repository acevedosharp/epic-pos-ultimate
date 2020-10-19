package xyz.acevedosharp.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "pedido", schema = "epic")
public class PedidoDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "pedido_id") Integer pedidoId;
    private @Column(name = "fecha_hora") Timestamp fechaHora;
    private @ManyToOne @JoinColumn(name = "proveedor") ProveedorDB proveedor;
    private @ManyToOne @JoinColumn(name = "empleado") EmpleadoDB empleado;
    private @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL) Set<LoteDB> lotes;

    public PedidoDB() {
    }

    public PedidoDB(Integer pedidoId, Timestamp fechaHora, ProveedorDB proveedor, EmpleadoDB empleado, Set<LoteDB> lotes) {
        this.pedidoId = pedidoId;
        this.fechaHora = fechaHora;
        this.proveedor = proveedor;
        this.empleado = empleado;
        this.lotes = lotes;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    public ProveedorDB getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorDB proveedor) {
        this.proveedor = proveedor;
    }

    public EmpleadoDB getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoDB empleado) {
        this.empleado = empleado;
    }

    public Set<LoteDB> getLotes() {
        return lotes;
    }

    public void setLotes(Set<LoteDB> lotes) {
        this.lotes = lotes;
    }
}
