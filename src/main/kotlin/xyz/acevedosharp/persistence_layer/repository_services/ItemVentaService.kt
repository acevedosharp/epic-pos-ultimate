package xyz.acevedosharp.persistence_layer.repository_services

import xyz.acevedosharp.entities.ItemVentaDB
import xyz.acevedosharp.persistence_layer.repositories.ItemVentaRepo
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.BatchSaving
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.NonEditableRepoService
import org.springframework.stereotype.Service

@Service
class ItemVentaService(val repo: ItemVentaRepo): NonEditableRepoService<ItemVentaDB>, BatchSaving<ItemVentaDB> {
    override fun all(): List<ItemVentaDB> = repo.findAll()

    override fun add(item: ItemVentaDB): ItemVentaDB = repo.save(item)

    override fun addAll(items: List<ItemVentaDB>): List<ItemVentaDB> = repo.saveAll(items)
}