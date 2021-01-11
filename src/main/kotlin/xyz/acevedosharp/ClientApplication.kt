package xyz.acevedosharp

import xyz.acevedosharp.views.MainStylesheet
import javafx.scene.image.Image
import javafx.stage.Stage
import org.hibernate.exception.JDBCConnectionException
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.dao.DataAccessResourceFailureException
import tornadofx.*
import xyz.acevedosharp.views.NoInternetConnectionErrorDialog
import xyz.acevedosharp.views.UnknownErrorDialog
import xyz.acevedosharp.views.screens.PuntoDeVentaView


class ClientApplication : App(PuntoDeVentaView::class, MainStylesheet::class) {

    private val context: ConfigurableApplicationContext = SpringApplicationBuilder(LocalSpringBootApplication::class.java).run()

    override fun start(stage: Stage) {
        // before anything, we need to add our Spring application context to our TornadoFX's scope
        FX.getComponents()[CustomApplicationContextWrapper::class] = CustomApplicationContextWrapper(context)

        super.start(stage)
        stage.isResizable = true
        stage.isFullScreen = true
        stage.icons.add(Image("images/store_logo_icon.png"))

        Thread.setDefaultUncaughtExceptionHandler { t: Thread, e: Throwable ->
            if (Joe.currentView != null) {
                if (e is DataAccessResourceFailureException || e is JDBCConnectionException) {
                    Joe.currentView!!.openInternalWindow(NoInternetConnectionErrorDialog())
                } else if (!InternetConnection.isAvailable()) {
                    Joe.currentView!!.openInternalWindow(NoInternetConnectionErrorDialog()) // show this mf again just to make sure
                } else {
                    Joe.currentView!!.openInternalWindow(UnknownErrorDialog(e.message!!))
                    e.printStackTrace()
                }
            } else {
                throw RuntimeException("Did the application not load? There is no Joe.currentView.")
            }
        }
    }
}

