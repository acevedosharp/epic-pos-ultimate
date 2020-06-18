package com.acevedosharp

import com.acevedosharp.styles.MainStylesheet
import com.acevedosharp.views.ProductosView
import javafx.scene.image.Image
import javafx.stage.Stage
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import tornadofx.App
import tornadofx.FX


class ClientApplication : App(ProductosView::class, MainStylesheet::class) {

    private val context: ConfigurableApplicationContext = SpringApplicationBuilder(LocalSpringBootApplication::class.java).run()

    override fun start(stage: Stage) {
        // before anything, we need to add our Spring application context to our TornadoFX's scope
        FX.getComponents()[CustomApplicationContextWrapper::class] = CustomApplicationContextWrapper(context)

        super.start(stage)
        stage.isResizable = true
        stage.isFullScreen = true
        stage.icons.add(Image("images/store_logo_icon.png"))
    }
}

