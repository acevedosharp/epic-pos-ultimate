package xyz.acevedosharp

import javafx.scene.image.Image
import javafx.stage.Stage
import org.hibernate.exception.JDBCConnectionException
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.dao.DataAccessResourceFailureException
import tornadofx.*
import xyz.acevedosharp.views.*
import xyz.acevedosharp.views.dialogs.GenericApplicationException
import xyz.acevedosharp.views.dialogs.GenericErrorDialog
import xyz.acevedosharp.views.dialogs.NoInternetConnectionErrorDialog
import xyz.acevedosharp.views.dialogs.UnexpectedErrorDialog
import xyz.acevedosharp.views.screens.PuntoDeVentaView
import kotlin.reflect.KClass

// Treat starting view as a value. In the future could read it from a config file
private val startingView: KClass<out UIComponent> = PuntoDeVentaView::class

class ClientApplication : App(startingView, MainStylesheet::class) {

    private val context: ConfigurableApplicationContext = SpringApplicationBuilder(LocalSpringBootApplication::class.java).run()

    override fun start(stage: Stage) {
        // before anything, we need to add our Spring application context to our TornadoFX's scope
        FX.getComponents()[CustomApplicationContextWrapper::class] = CustomApplicationContextWrapper(context)

        super.start(stage)
        stage.isResizable = true
        stage.isFullScreen = true
        stage.icons.add(Image("images/store_logo_icon.png"))

        Joe.currentView.setValue(find(PuntoDeVentaView::class, this.scope))

        Thread.setDefaultUncaughtExceptionHandler { _: Thread, e: Throwable ->
            if (Joe.currentView.value != null) {
                if (e is DataAccessResourceFailureException || e is JDBCConnectionException) {
                    Joe.currentView.value.openInternalWindow(NoInternetConnectionErrorDialog())
                } else if (e is GenericApplicationException) {
                    Joe.currentView.value.openInternalWindow(GenericErrorDialog(e.message!!))
                } else if (!InternetConnection.isAvailable()) {
                    Joe.currentView.value.openInternalWindow(NoInternetConnectionErrorDialog()) // show this mf again just to make sure
                } else {
                    Joe.currentView.value.openInternalWindow(UnexpectedErrorDialog(e.message!!))
                    e.printStackTrace()
                }
            } else {
                throw RuntimeException("Did the application not load? There is no Joe.currentView.")
            }
        }
    }
}
