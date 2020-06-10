package styles

import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class MainStylesheet: Stylesheet() {

    companion object {
        val expandedButton by cssclass()
        val greenButton by cssclass()
        val blueButton by cssclass()
        val redButton by cssclass()
        val navigationButton by cssclass()
        val selectedButton by cssclass()
        val unselectedButton by cssclass()

        val titleLabel by cssclass()
        val greenLabel by cssclass()
        val blueLabel by cssclass()
        val redLabel by cssclass()

        val productosIcon by cssclass()
    }

    init {
        root {
            fontSize = 18.px
            fontFamily = "JetBrains Mono"
        }
        button {
            fontSize = 24.px
            textFill = Color.WHITE
            borderRadius += box(0.px)
            backgroundRadius += box(0.px)
        }
        expandedButton {
            prefWidth =200.px
        }
        greenButton { backgroundColor += Color.DARKOLIVEGREEN }
        blueButton { backgroundColor += Color.DODGERBLUE }
        redButton { backgroundColor += Color.INDIANRED }
        navigationButton {
            prefWidth = 250.px
            prefHeight = 80.px
            contentDisplay = ContentDisplay.LEFT
            alignment = Pos.CENTER_LEFT
        }
        selectedButton {
            backgroundColor += c(255, 255, 255, 0.15)
        }
        unselectedButton {
            backgroundColor += c(255, 255, 255, 0.0)
        }

        titleLabel {
            fontSize = 32.px
            fontWeight = FontWeight.EXTRA_BOLD
            textFill = Color.WHITE
            padding = box(vertical = 20.px, horizontal = 5.px)
            textAlignment = TextAlignment.CENTER
        }
        greenLabel { backgroundColor += Color.DARKOLIVEGREEN }
        blueLabel { backgroundColor += Color.DODGERBLUE }
        redLabel { backgroundColor += Color.INDIANRED }

        productosIcon {

        }
    }
}