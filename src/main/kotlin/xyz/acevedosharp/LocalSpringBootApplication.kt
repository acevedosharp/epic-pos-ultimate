package xyz.acevedosharp

import javafx.application.Application
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import tornadofx.*

@SpringBootApplication
class LocalSpringBootApplication

fun main() {
    Application.launch(ClientApplication::class.java)
}

object InternetConnection {
    fun isAvailable(): Boolean {
        return try {
            val url = URL("http://www.google.com")
            val conn: URLConnection = url.openConnection()
            conn.connect()
            conn.getInputStream().close()
            true
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            false
        }
    }
}

object Joe {
    var currentView = SimpleObjectProperty<UIComponent>(null)
    var rememberPrinter = SimpleBooleanProperty(false)
    var persistentPrinter = SimpleStringProperty()
}
