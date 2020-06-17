package com.acevedosharp.persistence_access.repositories

import com.acevedosharp.entities.PedidoDB
import org.springframework.data.jpa.repository.JpaRepository

interface PedidoRepo: JpaRepository<PedidoDB, Int>