package xyz.acevedosharp.persistence_layer.repository_services

import xyz.acevedosharp.entities.LoteDB
import xyz.acevedosharp.persistence_layer.repositories.LoteRepo
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.BatchSaving
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.NonEditableRepoService
import org.springframework.stereotype.Service

@Service
class LoteService(val repo: LoteRepo): NonEditableRepoService<LoteDB>, BatchSaving<LoteDB> {
    override fun all(): List<LoteDB> = repo.findAll()

    override fun add(item: LoteDB): LoteDB = repo.save(item)

    override fun addAll(items: List<LoteDB>): List<LoteDB> = repo.saveAll(items)
}