package com.acevedosharp.persistence_layer.repository_services

import com.acevedosharp.entities.LoteDB
import com.acevedosharp.persistence_layer.repositories.LoteRepo
import com.acevedosharp.persistence_layer.repository_services.interfaces.BatchSaving
import com.acevedosharp.persistence_layer.repository_services.interfaces.NonEditableRepoService
import org.springframework.stereotype.Service

@Service
class LoteService(val repo: LoteRepo): NonEditableRepoService<LoteDB>, BatchSaving<LoteDB> {
    override fun all(): List<LoteDB> = repo.findAll()

    override fun add(item: LoteDB): LoteDB = repo.save(item)

    override fun addAll(items: List<LoteDB>): List<LoteDB> = repo.saveAll(items)
}