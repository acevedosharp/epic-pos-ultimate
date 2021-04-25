package xyz.acevedosharp.views.helpers

import tornadofx.*
import xyz.acevedosharp.views.screens.*

enum class CurrentModule {
    PUNTO_DE_VENTA,
    PRODUCTOS,
    PEDIDOS,
    REPORTES,
    FAMILIAS,
    PROVEEDORES,
    EMPLEADOS,
    CLIENTES
}

object CurrentModuleHelper {
    val screenMappings: HashMap<CurrentModule, View> = hashMapOf(
        CurrentModule.PUNTO_DE_VENTA to PuntoDeVentaView(),
        CurrentModule.PRODUCTOS to ProductoView(),
        CurrentModule.PEDIDOS to PedidoView(),
        CurrentModule.REPORTES to ReporteScreen(),
        CurrentModule.FAMILIAS to FamiliaView(),
        CurrentModule.PROVEEDORES to ProveedorView(),
        CurrentModule.EMPLEADOS to EmpleadoView(),
        CurrentModule.CLIENTES to ClienteView(),
    )
}
