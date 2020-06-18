package com.acevedosharp.entities;

import javax.persistence.*;

@Entity
@Table(name = "proveedor", schema = "app")
public class ProveedorDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "proveedor_id") Integer proveedorId;
    private @Column(name = "nombre") String nombre;
    private @Column(name = "telefono") String telefono;
    private @Column(name = "correo") String correo;
    private @Column(name = "direccion") String direccion;

    public ProveedorDB() {
    }

    public ProveedorDB(Integer proveedorId, String nombre, String telefono, String correo, String direccion) {
        this.proveedorId = proveedorId;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
