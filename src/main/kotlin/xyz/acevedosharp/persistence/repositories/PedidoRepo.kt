package xyz.acevedosharp.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.acevedosharp.persistence.entities.PedidoDB

interface PedidoRepo: JpaRepository<PedidoDB, Int>
