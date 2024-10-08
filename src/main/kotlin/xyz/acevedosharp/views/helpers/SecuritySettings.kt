package xyz.acevedosharp.views.helpers

import xyz.acevedosharp.views.helpers.CurrentModule.*

object SecuritySettings {
    val securedModules: HashMap<CurrentModule, Boolean> = hashMapOf(
        PUNTO_DE_VENTA to false,
        PRODUCTOS to false,
        PEDIDOS to false,
        REPORTES to false,
        FAMILIAS to false,
        PROVEEDORES to false,
        EMPLEADOS to false,
        CLIENTES to false
    )

    const val password = "password"
}