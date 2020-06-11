import javafx.scene.image.Image
import javafx.stage.Stage
import styles.MainStylesheet
import tornadofx.*
import views.ProductosView

class ClientApplication : App(ProductosView::class, MainStylesheet::class) {
    override fun start(stage: Stage) {
        super.start(stage)
//        stage.isResizable = false
        stage.isFullScreen = true
        stage.icons.add(Image("images/store_logo_icon.png"))
    }
}
