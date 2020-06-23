package com.acevedosharp.persistence_layer.repositories

import com.acevedosharp.entities.LoteDB
import com.acevedosharp.entities.ProductoDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoteRepo: JpaRepository<LoteDB, Int> {

    fun findAllByProductoEquals(productoDB: ProductoDB): List<LoteDB>
}