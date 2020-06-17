package com.acevedosharp.entities;

import javax.persistence.*;

@Entity
@Table(name = "empleado", schema = "app")
public class EmpleadoDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "empleado_id") Integer empleadoId;
    private @Column(name = "nombre") String nombre;
    private @Column(name = "telefono") String telefono;
    private @Column(name = "activo") Boolean activo;

    public EmpleadoDB() {
    }

    public EmpleadoDB(Integer empleadoId, String nombre, String telefono, Boolean activo) {
        this.empleadoId = empleadoId;
        this.nombre = nombre;
        this.telefono = telefono;
        this.activo = activo;
    }

    public Integer getEmpleadoId() {
        return empleadoId;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
