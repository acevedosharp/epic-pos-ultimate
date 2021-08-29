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
import kotlin.math.max


@Service
class RecipePrintingService {

    fun printRecipe(venta: VentaDB, impName: String) {
        fun formatItem(item: ItemVentaDB): String {
            val (_, ivaAmount, sellPrice) = GlobalHelper.calculateSellPriceBrokenDown(
                item.producto.precioCompra,
                item.producto.margen,
                item.producto.iva
            )

            val res = StringBuilder()

            res.append(item.producto.descripcionCorta)
            res.append(" ".repeat(max(26 - item.producto.descripcionCorta.length, 0)))
            val s1 = "$${(sellPrice - ivaAmount).round(2)}"
            res.append(s1)
            res.append(" ".repeat(max(10 - s1.length, 0)))
            val s2 = " x${item.cantidad}"
            res.append(s2)

            return res.toString()
        }

        var subtotal = 0.0

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
        venta.items.forEach {
            sb.append(formatItem(it))
            sb.append("\n")

            val (_, ivaAmount, sellPrice) = GlobalHelper.calculateSellPriceBrokenDown(
                it.producto.precioCompra,
                it.producto.margen,
                it.producto.iva
            )

            subtotal += (sellPrice - ivaAmount) * it.cantidad
        }
        sb.append("Subtotal: $${subtotal.round(2)}\n")
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
        val pago = "Total (con iva): \$${venta.totalConIva}"
        sb.append(pago)
        sb.append(" ".repeat(max(33 - pago.length, 0)))
        sb.append("Pago: \$${venta.pagoRecibido}\n")
        sb.append("Cambio: $${venta.pagoRecibido - venta.totalConIva}\n")
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

    private fun openCashDrawer(printerName: String) {
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
}
