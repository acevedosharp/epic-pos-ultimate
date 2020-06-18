package com.acevedosharp.persistence_access.repositories_services

import com.acevedosharp.entities.ProductoDB
import com.acevedosharp.persistence_access.repositories.ProductoRepo
import com.acevedosharp.validation_helpers.CustomUniqueValueConstraintViolationException
import org.springframework.stereotype.Service

@Service
class ProductoService(private val productoRepo: ProductoRepo): BaseRepoService<ProductoDB> {

    override fun all(): List<ProductoDB> = productoRepo.findAll()

    @Throws(CustomUniqueValueConstraintViolationException::class)
    override fun add(item: ProductoDB): ProductoDB {
        when {
            productoRepo.existsByCodigo(item.codigo)                     -> throw CustomUniqueValueConstraintViolationException("Ya existe un producto con ese código.")
            productoRepo.existsByDescripcionLarga(item.descripcionLarga) -> throw CustomUniqueValueConstraintViolationException("Ya existe un producto con esa descripción larga.")
            productoRepo.existsByDescripcionCorta(item.descripcionCorta) -> throw CustomUniqueValueConstraintViolationException("Ya existe un producto con esa descripción corta.")
            else -> return productoRepo.save(item)
        }
    }

    @Throws(CustomUniqueValueConstraintViolationException::class)
    override fun edit(item: ProductoDB): ProductoDB {
        when {
            (productoRepo.existsByCodigo(item.codigo)                    ) && (productoRepo.findByCodigo(item.codigo)                    .productoId != item.productoId) -> throw CustomUniqueValueConstraintViolationException("Ya existe otro producto con ese código.")
            (productoRepo.existsByDescripcionLarga(item.descripcionLarga)) && (productoRepo.findByDescripcionLarga(item.descripcionLarga).productoId != item.productoId) -> throw CustomUniqueValueConstraintViolationException("Ya existe otro producto con esa descripción larga.")
            (productoRepo.existsByDescripcionCorta(item.descripcionCorta)) && (productoRepo.findByDescripcionCorta(item.descripcionCorta).productoId != item.productoId) -> throw CustomUniqueValueConstraintViolationException("Ya existe otro producto con esa descripción corta.")
            else -> return productoRepo.save(item)
        }
    }
}