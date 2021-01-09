package xyz.acevedosharp

import javafx.application.Application
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class LocalSpringBootApplication

fun main() {
    Application.launch(ClientApplication::class.java)
}
