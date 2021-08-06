package xyz.acevedosharp

import org.springframework.context.ConfigurableApplicationContext
import tornadofx.Component
import tornadofx.ScopedInstance
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CustomApplicationContextWrapper(val context: ConfigurableApplicationContext): Component(), ScopedInstance