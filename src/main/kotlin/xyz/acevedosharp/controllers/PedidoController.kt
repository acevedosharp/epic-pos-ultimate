package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.Lote
import xyz.acevedosharp.ui_models.Pedido
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.springframework.data.repository.findByIdOrNull
import tornadofx.Controller
import xyz.acevedosharp.persistence.entities.LoteDB
import xyz.acevedosharp.persistence.entities.PedidoDB
import xyz.acevedosharp.persistence.entities.ProductoDB
import xyz.acevedosharp.persistence.repositories.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PedidoController : Controller(), UpdateSnapshot {

    private val pedidoRepo = find<CustomApplicationContextWrapper>().context.getBean(PedidoRepo::class.java)
    private val proveedorRepo = find<CustomApplicationContextWrapper>().context.getBean(ProveedorRepo::class.java)
    private val empleadoRepo = find<CustomApplicationContextWrapper>().context.getBean(EmpleadoRepo::class.java)
    private val loteRepo = find<CustomApplicationContextWrapper>().context.getBean(LoteRepo::class.java)
    private val productoRepo = find<CustomApplicationContextWrapper>().context.getBean(ProductoRepo::class.java)

    val pedidos: ObservableList<PedidoDB> = FXCollections.observableArrayList()
    get() {
        updateSnapshot()
        return field
    }

    fun findById(id: Int) = pedidoRepo.findByIdOrNull(id)

    fun add(pedido: Pedido, lotes: List<Lote>) {
        val preRes = pedidoRepo.save(
            PedidoDB(
                null,
                Timestamp.valueOf(pedido.fechaHora),
                proveedorRepo.findByIdOrNull(pedido.proveedor.id)!!,
                empleadoRepo.findByIdOrNull(pedido.empleado.id)!!,
                setOf()
            )
        )

        updateSnapshot()

        val productosSnapshot = productoRepo.findAll()

        val lotesPersist = loteRepo.saveAll(lotes.map { lote ->
            LoteDB(
                null,
                lote.cantidad,
                lote.precioCompra,
                productosSnapshot.find { it.productoId == lote.producto.id }!!,
                preRes
            )
        })

        val productosWithNewPrice = lotesPersist.map { currentLote ->

            val producto = currentLote.producto

            if (producto.precioCompraEfectivo == null || producto.precioCompraEfectivo!! < currentLote.precioCompra) {
                producto.precioCompraEfectivo = currentLote.precioCompra
            }

            val rawSellPrice = producto.precioCompraEfectivo!! / (1 - producto.margen)
            val roundedSellPrice = rawSellPrice + (50 - (rawSellPrice % 50))
            producto.precioVenta = roundedSellPrice

            return@map producto
        }

        productoRepo.saveAll(productosWithNewPrice)
    }

    override fun updateSnapshot() {
        println("Triggered update snapshot for Pedido once at ${DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now())}")
        pedidos.setAll(pedidoRepo.findAll())
    }
}
