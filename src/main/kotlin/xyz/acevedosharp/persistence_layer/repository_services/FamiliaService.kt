package xyz.acevedosharp.persistence_layer.repository_services

import xyz.acevedosharp.entities.FamiliaDB
import xyz.acevedosharp.persistence_layer.repositories.FamiliaRepo
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.BaseRepoService
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class FamiliaService(val repo: FamiliaRepo): BaseRepoService<FamiliaDB> {
    override fun all(): List<FamiliaDB> = repo.findAll()

    override fun add(item: FamiliaDB): FamiliaDB {
        when {
            repo.existsByNombre(item.nombre) -> throw Exception()
            else -> return repo.save(item)
        }
    }

    override fun edit(item: FamiliaDB): FamiliaDB {
        when {
            (repo.existsByNombre(item.nombre)) && (repo.findByNombre(item.nombre).familiaId != item.familiaId) -> throw Exception()
            else -> return repo.save(item)
        }
    }

}
