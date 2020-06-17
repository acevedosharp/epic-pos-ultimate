package com.acevedosharp.persistence_access.repositories

import com.acevedosharp.entities.ProductoDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductoRepo: JpaRepository<ProductoDB, Int>