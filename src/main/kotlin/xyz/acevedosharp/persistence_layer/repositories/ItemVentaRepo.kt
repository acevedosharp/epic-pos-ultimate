package xyz.acevedosharp.persistence_layer.repositories

import xyz.acevedosharp.entities.ItemVentaDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemVentaRepo: JpaRepository<ItemVentaDB, Int>