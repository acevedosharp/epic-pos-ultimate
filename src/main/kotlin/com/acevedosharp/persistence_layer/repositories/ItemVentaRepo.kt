package com.acevedosharp.persistence_layer.repositories

import com.acevedosharp.entities.ItemVentaDB
import org.springframework.data.jpa.repository.JpaRepository

interface ItemVentaRepo: JpaRepository<ItemVentaDB, Int>