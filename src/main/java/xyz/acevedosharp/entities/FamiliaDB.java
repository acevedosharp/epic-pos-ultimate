package xyz.acevedosharp.entities;

import javax.persistence.*;

@Entity
@Table(name = "familia", schema = "epic")
public class FamiliaDB {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id @Column(name = "familia_id") Integer familiaId;
    private @Column(name = "nombre") String nombre;

    public FamiliaDB() {
    }

    public FamiliaDB(Integer familiaId, String nombre) {
        this.familiaId = familiaId;
        this.nombre = nombre;
    }

    public Integer getFamiliaId() {
        return familiaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}