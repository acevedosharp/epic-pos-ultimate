package xyz.acevedosharp.persistence_layer.repository_services

import xyz.acevedosharp.entities.EmpleadoDB
import xyz.acevedosharp.persistence_layer.repositories.EmpleadoRepo
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.BaseRepoService
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class EmpleadoService(val repo: EmpleadoRepo):
    BaseRepoService<EmpleadoDB> {
    override fun all(): List<EmpleadoDB> = repo.findAll()

    override fun add(item: EmpleadoDB): EmpleadoDB {
        when {
            repo.existsByNombre(item.nombre)     -> throw Exception()
            repo.existsByTelefono(item.telefono) -> throw Exception()
            else -> return repo.save(item)
        }
    }

    override fun edit(item: EmpleadoDB): EmpleadoDB {
        when {
            (repo.existsByNombre(item.nombre)    ) && (repo.findByNombre(item.nombre).empleadoId     != item.empleadoId) -> throw Exception()
            (repo.existsByTelefono(item.telefono)) && (repo.findByTelefono(item.telefono).empleadoId != item.empleadoId) -> throw Exception()
            else -> return repo.save(item)
        }
    }
}