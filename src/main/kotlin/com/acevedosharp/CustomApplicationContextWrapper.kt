package com.acevedosharp

import org.springframework.context.ConfigurableApplicationContext
import tornadofx.Component
import tornadofx.ScopedInstance

class CustomApplicationContextWrapper(val context: ConfigurableApplicationContext): Component(), ScopedInstance