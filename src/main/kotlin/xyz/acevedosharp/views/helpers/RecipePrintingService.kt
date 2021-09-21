package xyz.acevedosharp.views.helpers

import org.springframework.stereotype.Service
import xyz.acevedosharp.GlobalHelper
import xyz.acevedosharp.GlobalHelper.round
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.VentaDB
import java.text.SimpleDateFormat
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.PrintRequestAttributeSet
import kotlin.math.ceil
import kotlin.math.max

@Service
class RecipePrintingService {
    fun printRecipe(venta: VentaDB, impName: String) {
        fun formatItem(item: ItemVentaDB, sb: StringBuilder) {
            sb.append(
                formatStringWithMaxCols(
                    str = item.producto.descripcionCorta,
                    cols = 26
                )
            )
            sb.append(
                formatStringWithMaxCols(
                    str = "$${formatDecimal(item.precioVentaConIva)}",
                    cols = 7
                )
            )
            sb.append('x')
            sb.append(
                formatStringWithMaxCols(
                    str = "${item.cantidad}",
                    cols = 5,
                    alignment = StringAlignment.RIGHT
                )
            )
            sb.append("=$")
            sb.append(
                formatStringWithMaxCols(
                    str = formatDecimal(item.precioVentaConIva * item.cantidad.toDouble(), noDecimals = true),
                    cols = 7,
                    alignment = StringAlignment.RIGHT
                )
            )

            sb.append('\n')
        }

        val lowerPadding = "\n\n\n\n\n\n\n"

        val sb = StringBuilder()

        sb.append("*==============================================*\n")
        sb.append("||          Autoservicio Mercamás             ||\n")
        sb.append("||    Tel: 6000607   Dir: Calle 35 #34-168    ||\n")
        sb.append("||   NIT: 5796564-6  Regimen: Simplificado    ||\n")
        sb.append("*==============================================*\n")
        sb.append("Atendido por: ${venta.empleado.nombre}\n")
        sb.append("Cliente: ${venta.cliente.nombre} \n")
        sb.append("------------------------------------------------\n")
        venta.items.forEach { item ->
            formatItem(item, sb)
        }
        if (venta.items.any { it.producto.iva != 0 }) {
            sb.append("------------------------------------------------\n")
            sb.append("                   Impuestos                    \n")
            val groupedByIva = venta.items.groupBy { it.producto.iva }
            groupedByIva.forEach { entry: Map.Entry<Int, List<ItemVentaDB>> ->
                if (entry.key != 0) {
                    val iva = "${entry.key}%"
                    sb.append(iva)
                    sb.append(" ".repeat(max(38 - iva.length, 0)))
                    var ivaValue = 0.0
                    entry.value.forEach {
                        val (_, ivaAmount, _) = GlobalHelper.calculateSellPriceBrokenDown(
                            it.producto.precioCompra,
                            it.producto.margen,
                            it.producto.iva
                        )

                        ivaValue += ivaAmount * it.cantidad
                    }
                    sb.append("$${ivaValue.round(2)}\n")
                }
            }
        }
        sb.append("------------------------------------------------\n")
        sb.append(
            formatStringWithMaxCols(
                str = "Total con iva: $${venta.totalConIva}",
                cols = 26
            )
        )
        sb.append(
            formatStringWithMaxCols(
                str = "Pago: $${venta.pagoRecibido}",
                cols = 22
            )
        )
        sb.append('\n')
        sb.append("Cambio: $${venta.pagoRecibido - venta.totalConIva}")
        sb.append('\n')
        sb.append("Gracias por su compra el ${SimpleDateFormat("dd/MM/yy HH:mm:ss").format(venta.fechaHora)}.")
        sb.append("                                                \n")
        sb.append("Res. habilitación: 18764015886194 de 03/08/2021\n")
        sb.append("Desde 1 hasta 5000. Factura #${venta.ventaId!!}\n")
        sb.append(lowerPadding)

        openCashDrawer(impName)
        printString(impName, sb.toString())
        printBytes(impName, byteArrayOf(0x1d, 'V'.toByte(), 1))
    }

    fun getPrinters(): List<String> {
        val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        val printRequestAttributes: PrintRequestAttributeSet = HashPrintRequestAttributeSet()

        val printServices: Array<PrintService> = PrintServiceLookup.lookupPrintServices(flavor, printRequestAttributes)

        return printServices.map { it.name }
            .filter { it !in listOf("Microsoft XPS Document Writer", "Microsoft Print to PDF", "Fax") }
    }

    fun openCashDrawer(printerName: String) {
        val open = byteArrayOf(27, 112, 0, 100, 250.toByte())
        val flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        val pras: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        val printServices = PrintServiceLookup.lookupPrintServices(
            flavor, pras
        )
        val service = findPrintService(printerName, printServices)
        val job = service!!.createPrintJob()

        val doc: Doc = SimpleDoc(open, flavor, null)
        val aset: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        try {
            job.print(doc, aset)
        } catch (ex: PrintException) {
            ex.printStackTrace()
        }
    }

    private fun printString(printerName: String, text: String) {

        // find the printService of name printerName
        val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        val pras: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        val printServices = PrintServiceLookup.lookupPrintServices(
            flavor, pras
        )
        val service = findPrintService(printerName, printServices)
        val job = service!!.createPrintJob()
        try {
            // use a charset that can use many characters
            val bytes: ByteArray = text.toByteArray(charset("CP437"))
            val doc: Doc = SimpleDoc(bytes, flavor, null)
            job.print(doc, HashPrintRequestAttributeSet())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun printBytes(printerName: String, bytes: ByteArray?) {
        val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        val pras: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        val printService = PrintServiceLookup.lookupPrintServices(
            flavor, pras
        )
        val service = findPrintService(printerName, printService)
        val job = service!!.createPrintJob()
        try {
            val doc: Doc = SimpleDoc(bytes, flavor, null)
            job.print(doc, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findPrintService(printerName: String, services: Array<PrintService>): PrintService? {
        for (service in services) {
            if (service.name.equals(printerName, ignoreCase = true)) {
                return service
            }
        }
        return null
    }

    enum class StringAlignment {
        LEFT, RIGHT
    }

    private fun formatStringWithMaxCols(
        str: String,
        cols: Int,
        alignment: StringAlignment = StringAlignment.LEFT
    ): String {
        val result = CharArray(cols)
        if (alignment == StringAlignment.LEFT) {
            for (i in IntRange(0, cols - 1)) {
                if (i < str.length)
                    result[i] = str[i]
                else
                    result[i] = ' '
            }
        } else if (alignment == StringAlignment.RIGHT) {
            val offset = cols - str.length
            for (i in IntRange(0, cols - 1)) {
                if (i < offset)
                    result[i] = ' '
                else
                    result[i] = str[i - offset]
            }
        }
        return String(result)
    }

    private fun formatDecimal(n: Double, noDecimals: Boolean = false): String {
        if (n % 1.0 != 0.0) {
            if (noDecimals)
                return ceil(n).toString()
            else
                return n.round(2).toString()
        } else {
            return n.toInt().toString()
        }
    }
}
