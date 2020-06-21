package com.acevedosharp.persistence_layer.repository_services

import com.acevedosharp.entities.ClienteDB
import com.acevedosharp.persistence_layer.repositories.ClienteRepo
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class ClienteService(val repo: ClienteRepo): BaseRepoService<ClienteDB> {
    override fun all(): List<ClienteDB> = repo.findAll()

    override fun add(item: ClienteDB): ClienteDB {
        when {
            repo.existsByNombre(item.nombre)     -> throw Exception()
            repo.existsByTelefono(item.telefono) -> throw Exception()
            else -> return repo.save(item.apply {
                if (item.direccion.isEmpty()) direccion = null
            })
        }
    }

    override fun edit(item: ClienteDB): ClienteDB {
        when {
            (repo.existsByNombre(item.nombre)    ) && (repo.findByNombre(item.nombre).clienteId     != item.clienteId) -> throw Exception()
            (repo.existsByTelefono(item.telefono)) && (repo.findByTelefono(item.telefono).clienteId != item.clienteId) -> throw Exception()
            else -> return repo.save(item.apply {
                if (item.direccion.isEmpty()) direccion = null
            })
        }
    }
}