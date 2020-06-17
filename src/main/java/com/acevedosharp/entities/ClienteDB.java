package com.acevedosharp.entities;

import javax.persistence.*;

@Entity
@Table(name = "cliente", schema = "app")
public class ClienteDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "cliente_id") Integer clienteId;
    private @Column(name = "nombre") String nombre;
    private @Column(name = "telefono") String telefono;
    private @Column(name = "direccion") String direccion;

    public ClienteDB() {
    }

    public ClienteDB(Integer clienteId, String nombre, String telefono, String direccion) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Integer getClienteId() {
        return clienteId;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
