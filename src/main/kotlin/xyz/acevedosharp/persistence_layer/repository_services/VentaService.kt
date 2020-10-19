package xyz.acevedosharp.persistence_layer.repository_services

import xyz.acevedosharp.entities.VentaDB
import xyz.acevedosharp.persistence_layer.repositories.VentaRepo
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.NonEditableRepoService
import org.springframework.stereotype.Service

@Service
class VentaService(val repo: VentaRepo): NonEditableRepoService<VentaDB> {

    override fun all(): List<VentaDB> = repo.findAll()

    override fun add(item: VentaDB): VentaDB = repo.save(item)
}