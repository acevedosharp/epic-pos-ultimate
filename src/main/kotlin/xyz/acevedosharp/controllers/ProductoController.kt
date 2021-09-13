package xyz.acevedosharp.controllers

import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.ui_models.Producto
import javafx.collections.ObservableList
import tornadofx.Controller
import org.springframework.data.repository.findByIdOrNull
import tornadofx.toObservable
import xyz.acevedosharp.persistence.entities.FamiliaDB
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.ProductoDB
import xyz.acevedosharp.persistence.repositories.ItemVentaRepo
import xyz.acevedosharp.persistence.repositories.ProductoRepo
import xyz.acevedosharp.views.screens.ProductoSaleHistoryModal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ProductoController(productoRepo: ProductoRepo? = null) : Controller(), UpdateSnapshot {

    private val productoRepo = productoRepo
        ?: find<CustomApplicationContextWrapper>().context.getBean(ProductoRepo::class.java)

    private val itemVentaRepo = find<CustomApplicationContextWrapper>().context.getBean(ItemVentaRepo::class.java)

    private val productos: MutableList<ProductoDB> = mutableListOf()

    fun getHistory(producto: ProductoDB, type: String, goBackNUnits: Int): List<ProductoSaleHistoryModal.HistoryPoint> {
        class TimeGrouping(
            val title: String,
            val startRange: Timestamp,
            val endRange: Timestamp,
            val ivs: ArrayList<ItemVentaDB>
        )

        val timeGroupings = arrayListOf<TimeGrouping>()

        val tz = TimeZone.getTimeZone("America/Bogota")
        val startRangeCalendar = Calendar.getInstance(tz)
        val endRangeCalendar = Calendar.getInstance(tz)

        val startHistory: Timestamp

        val formatter = DateTimeFormatter
            .ofPattern(if (type == "Mensual") "MMM, yyyy" else "dd/MMM/yyyy")
            .withLocale(Locale("es", "CO"))

        val unitOfTime = if (type == "Mensual") Calendar.MONTH else Calendar.DAY_OF_MONTH

        IntRange(1, goBackNUnits).forEach { _ ->
            if (type == "Mensual") {
                startRangeCalendar.set(
                    Calendar.DAY_OF_MONTH,
                    startRangeCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                )
                endRangeCalendar.set(Calendar.DAY_OF_MONTH, endRangeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            }

            startRangeCalendar.set(Calendar.HOUR_OF_DAY, startRangeCalendar.getActualMinimum(Calendar.HOUR_OF_DAY))
            startRangeCalendar.set(Calendar.MINUTE, startRangeCalendar.getActualMinimum(Calendar.MINUTE))
            startRangeCalendar.set(Calendar.SECOND, startRangeCalendar.getActualMinimum(Calendar.SECOND))

            endRangeCalendar.set(Calendar.HOUR_OF_DAY, endRangeCalendar.getActualMaximum(Calendar.HOUR_OF_DAY))
            endRangeCalendar.set(Calendar.MINUTE, endRangeCalendar.getActualMaximum(Calendar.MINUTE))
            endRangeCalendar.set(Calendar.SECOND, endRangeCalendar.getActualMaximum(Calendar.SECOND))

            timeGroupings.add(
                TimeGrouping(
                    LocalDateTime.ofInstant(startRangeCalendar.toInstant(), tz.toZoneId()).format(formatter),
                    Timestamp.from(startRangeCalendar.toInstant()),
                    Timestamp.from(endRangeCalendar.toInstant()),
                    arrayListOf()
                )
            )

            // go back one unit of time
            startRangeCalendar.add(unitOfTime, -1)
            endRangeCalendar.add(unitOfTime, -1)
        }

        // last iteration above got us to goBackNUnits units of time- back
        startHistory = Timestamp.from(startRangeCalendar.apply { add(unitOfTime, 1) }.toInstant())

        val ivs = itemVentaRepo.findAllByProductoAndFechaHoraAfter(producto, startHistory)

        for (iv in ivs) {
            for (timeGrouping in timeGroupings) {
                if (iv.fechaHora.before(timeGrouping.endRange) && iv.fechaHora.after(timeGrouping.startRange))
                    timeGrouping.ivs.add(iv)
            }
        }

        return timeGroupings.map {
            var unitCount = 0
            it.ivs.forEach { unitCount += it.cantidad }
            ProductoSaleHistoryModal.HistoryPoint(
                it.title,
                "$unitCount unidades"
            )
        }
    }

    fun getProductosWithUpdate(
        codigoQuery: String? = null,
        descripcionQuery: String? = null,
        familiaQuery: FamiliaDB? = null
    ): ObservableList<ProductoDB> {
        updateSnapshot()
        return getProductosClean(codigoQuery, descripcionQuery, familiaQuery)
    }

    fun getProductosClean(
        codigoQuery: String? = null,
        descripcionQuery: String? = null,
        familiaQuery: FamiliaDB? = null
    ): ObservableList<ProductoDB> {
        if (descripcionQuery == null && familiaQuery == null)
            return productos.take(100).toObservable()
        else
            if (codigoQuery != null)
                return listOf(productos.first { it.codigo == codigoQuery }).toObservable()
            else
                return powerSearch(productos, descripcionQuery!!, familiaQuery)
    }

    fun findById(id: Int) = productoRepo.findByIdOrNull(id)
    fun findByCodigo(barCode: String) = productoRepo.findByCodigo(barCode)

    fun save(producto: Producto) {
        productoRepo.save(
            ProductoDB(
                producto.id,
                producto.codigo,
                producto.descLarga,
                producto.descCorta,
                producto.existencias,
                producto.precioVenta,
                producto.precioCompraEfectivo,
                producto.margen,
                producto.familia,
                producto.alertaExistencias,
                producto.iva,
                producto.precioCompra
            )
        )
        updateSnapshot()
    }

    fun saveAllIgnoreRoundingForPedido(productos: List<ProductoDB>) {
        // rounding already taken care in PedidoController
        productoRepo.saveAll(productos)
        updateSnapshot()
    }

    fun isCodigoAvailable(codigo: String): Boolean {
        return productoRepo.existsByCodigo(codigo)
    }

    fun existsOtherWithCodigo(codigo: String, id: Int): Boolean {
        return productoRepo.existsByCodigo(codigo) && (productoRepo.findByCodigo(codigo).productoId != id)
    }

    fun isDescLargaAvailable(descLarga: String): Boolean {
        return productoRepo.existsByDescripcionLarga(descLarga)
    }

    fun existsOtherWithDescLarga(descLarga: String, id: Int): Boolean {
        return productoRepo.existsByDescripcionLarga(descLarga) && (productoRepo.findByDescripcionLarga(descLarga).productoId != id)
    }

    fun isDescCortaAvailable(descCorta: String): Boolean {
        return productoRepo.existsByDescripcionCorta(descCorta)
    }

    fun existsOtherWithDescCorta(descCorta: String, id: Int): Boolean {
        return productoRepo.existsByDescripcionCorta(descCorta) && (productoRepo.findByDescripcionCorta(descCorta).productoId != id)
    }

    override fun updateSnapshot() {
        productos.clear()
        productos.addAll(productoRepo.findAll())
    }

    private fun powerSearch(
        searchList: List<ProductoDB>,
        descripcionQuery: String,
        familiaQuery: FamiliaDB?
    ): ObservableList<ProductoDB> {
        return searchList.filter { product ->
            if (descripcionQuery.isNotBlank()) {
                val productString = product.descripcionLarga.lowercase(Locale.getDefault())
                val searchString = descripcionQuery.lowercase(Locale.getDefault())
                val searchWords = searchString
                    .lowercase(Locale.getDefault())
                    .split(" ")

                if (!(productString.contains(searchString) ||
                            searchWords.all { word -> productString.contains(word) })
                ) {
                    return@filter false
                }
            }

            if (familiaQuery != null) {
                if (product.familia != familiaQuery) {
                    return@filter false
                }
            }
            return@filter true
        }.toObservable()
    }
}
