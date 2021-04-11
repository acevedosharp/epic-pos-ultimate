package xyz.acevedosharp

import org.junit.Assert.*
import org.junit.Test
import org.junit.jupiter.api.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.context.junit4.SpringRunner
import xyz.acevedosharp.controllers.ProductoController
import xyz.acevedosharp.persistence.entities.ProductoDB
import xyz.acevedosharp.persistence.repositories.FamiliaRepo
import xyz.acevedosharp.persistence.repositories.ProductoRepo
import javax.annotation.PostConstruct

@SpringJUnitConfig
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalSpringBootApplication::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)

class ProductoTest {
    @Autowired
    private lateinit var productoRepo: ProductoRepo
    @Autowired
    private lateinit var familiaRepo: FamiliaRepo

    private lateinit var productoController: ProductoController


    @PostConstruct
    fun setController() {
        productoRepo.deleteAll()
        familiaRepo.deleteAll()
        productoController = ProductoController(productoRepo)
    }

    @Test
    @Order(1)
    fun p1_saveProducto() {
        val familia = familiaRepo.save(TestUtils.getSampleFamilia())

        val productos = TestUtils.getSampleProductos(familia, 10)
        productos.forEach {
            productoController.save(it.toModel())
        }

        assertEquals(productos.size, productoController.getProductosWithUpdate().size)
    }

    @Test
    @Order(2)
    fun p2_editProducto() {
        val familia = familiaRepo.save(TestUtils.getSampleFamilia())
        productoController.save(TestUtils.getSampleProducto(familia).toModel())

        val before = productoController.getProductosWithUpdate().first()

        val edited = ProductoDB(
            before.productoId,
            "Anything",
            "can change as long as the",
            "id stays the same",
            1,
            50,
            10,
            10.0,
            before.familia,
            10
        )

        productoController.save(edited.toModel())
        assertEquals(edited, productoController.getProductosClean().first())
    }

    @Test(expected = DataIntegrityViolationException::class)
    @Order(3)
    fun p3_duplicateProduct() {
        val familia = familiaRepo.save(TestUtils.getSampleFamilia())
        productoController.save(TestUtils.getSampleProducto(familia).toModel())

        val before = productoController.getProductosClean().first()

        productoController.save(before.apply { productoId = null }.toModel())
    }
}