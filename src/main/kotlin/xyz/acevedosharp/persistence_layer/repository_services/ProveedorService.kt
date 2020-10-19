package xyz.acevedosharp.persistence_layer.repository_services

import xyz.acevedosharp.entities.ProveedorDB
import xyz.acevedosharp.persistence_layer.repositories.ProveedorRepo
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.BaseRepoService
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class ProveedorService(val repo: ProveedorRepo):
    BaseRepoService<ProveedorDB> {
    override fun all(): List<ProveedorDB> = repo.findAll()

    override fun add(item: ProveedorDB): ProveedorDB {
        when {
            repo.existsByNombre(item.nombre)                                 -> throw Exception()
            repo.existsByTelefono(item.telefono)                             -> throw Exception()
            !item.correo.isNullOrBlank() && repo.existsByCorreo(item.correo) -> throw Exception()
            else -> return repo.save(item.apply {
                if (item.correo.isNullOrBlank()) correo = null
                if (item.direccion.isNullOrBlank()) direccion = null
            })
        }
    }

    override fun edit(item: ProveedorDB): ProveedorDB {
        when {
            (repo.existsByNombre(item.nombre)    ) && (repo.findByNombre(item.nombre).proveedorId     != item.proveedorId)                                   -> throw Exception()
            (repo.existsByTelefono(item.telefono)) && (repo.findByTelefono(item.telefono).proveedorId != item.proveedorId)                                   -> throw Exception()
            !item.correo.isNullOrBlank() && ((repo.existsByCorreo(item.correo))     && (repo.findByCorreo(item.correo).proveedorId     != item.proveedorId)) -> throw Exception()
            else -> return repo.save(item.apply {
                if (item.correo.isNullOrBlank()) correo = null
                if (item.direccion.isNullOrBlank()) direccion = null
            })
        }
    }
}