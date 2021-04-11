package xyz.acevedosharp

import xyz.acevedosharp.persistence.entities.FamiliaDB
import xyz.acevedosharp.persistence.entities.ProductoDB

object TestUtils {
    private var id = 0
    
    fun getSampleProducto(familiaDB: FamiliaDB): ProductoDB {
        return ProductoDB(
            null,
            (id++).toString(),
            (id++).toString(),
            (id++).toString(),
            10,
            450000,
            350000,
            15.0,
            familiaDB,
            4
        )
    }

    fun getSampleProductos(familiaDB: FamiliaDB, n: Int): List<ProductoDB> {
        return IntRange(1,n).map {
            getSampleProducto(familiaDB)
        }
    }

    fun getSampleFamilia(): FamiliaDB {
        return FamiliaDB(
            null,
            (id++).toString()
        )
    }

    fun getSampleFamilias(n: Int): List<FamiliaDB> {
        return IntRange(1,n).map { getSampleFamilia() }
    }
}