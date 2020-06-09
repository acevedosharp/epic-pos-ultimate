package controllers

import javafx.collections.FXCollections
import models.Producto
import tornadofx.Controller

class ProductoController: Controller() {
    val productos = FXCollections.observableArrayList(
        Producto("US001", "Crema dental Colgate x450g", "Colgate x450g", 2500, 10),
        Producto("US002", "Paquete Croissants x12u", "Croissants x12u", 3000, 8),
        Producto("US003", "Coca-Cola clásica 1.5L", "Coca-Cola 1.5L", 3500, 25)
    )
}