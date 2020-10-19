package xyz.acevedosharp.persistence_layer.repositories

import xyz.acevedosharp.entities.PedidoDB
import org.springframework.data.jpa.repository.JpaRepository

interface PedidoRepo: JpaRepository<PedidoDB, Int>