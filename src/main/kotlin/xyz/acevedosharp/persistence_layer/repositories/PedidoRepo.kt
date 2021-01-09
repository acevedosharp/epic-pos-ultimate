package xyz.acevedosharp.persistence_layer.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.acevedosharp.persistence_layer.entities.PedidoDB

interface PedidoRepo: JpaRepository<PedidoDB, Int>
