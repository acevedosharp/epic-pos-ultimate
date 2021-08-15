package xyz.acevedosharp

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GlobalSettingsProvider {
    @Value("\${doBirthdayCheck}")
    var doBirthdayCheckEnabled: Boolean = false

    @Value("\${doProductInventoryCheck}")
    var doProductInventoryCheckEnabled = false

    var daysForBirthdayCheck = 3
}
