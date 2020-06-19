package com.acevedosharp.views

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class MainStylesheet: Stylesheet() {

    companion object {
        // Buttons
        val expandedButton   by cssclass()
        val coolBaseButton   by cssclass()
        val greenButton      by cssclass()
        val blueButton       by cssclass()
        val redButton        by cssclass()
        val navigationButton by cssclass()
        val selectedButton   by cssclass()
        val unselectedButton by cssclass()

        // Labels
        val titleLabel  by cssclass()
        val greenLabel  by cssclass()
        val blueLabel   by cssclass()
        val redLabel    by cssclass()
        val yellowLabel by cssclass()
        val searchLabel by cssclass()

        // Containers
        val topBar by cssclass()
    }

    init {
        root {
            fontSize = 18.px
            fontFamily = "JetBrains Mono"
        }

        tableView {

            cell {
                alignment = Pos.CENTER_LEFT
                fontSize = 20.px
            }

            columnHeader {
                fontSize = 22.px
                backgroundColor += c("#03a8f8")
                textFill = Color.WHITE

                filler {

                    backgroundColor += c("#03a8f8")
                    textFill = Color.WHITE

                }

                label {

                    backgroundColor += c("#03a8f8")
                    textFill = Color.WHITE

                }

            }
            columnHeaderBackground {
                filler {
                    backgroundColor += c("#03a8f8")
                    textFill = Color.WHITE
                }
            }
        }

        tableCell

        coolBaseButton {
            fontSize = 22.px
            borderRadius += box(4.px)
            backgroundRadius += box(4.px)
            padding = box(8.px, 15.px)
            backgroundInsets += box(0.px)
            textFill = Color.WHITE
            fontWeight = FontWeight.BOLD
        }
        expandedButton {
            prefWidth = 200.px
        }
        greenButton {
            borderColor += box(c("#5ca941"))
            backgroundColor += LinearGradient(
                0.0,
                0.0,
                0.0,
                1.0,
                true,
                CycleMethod.NO_CYCLE,
                Stop(0.0, c("#8add6d")),
                Stop(1.0, c("#60b044"))
            )
            and(hover) {
                backgroundColor += LinearGradient(
                    0.0,
                    0.0,
                    0.0,
                    1.0,
                    true,
                    CycleMethod.NO_CYCLE,
                    Stop(0.0, c("#79d858")),
                    Stop(1.0, c("#569e3d"))
                )
            }
            and(pressed) {
                backgroundColor += c("#569e3d")
            }
        }
        blueButton {
            borderColor += box(c("#588AA9"))
            backgroundColor += LinearGradient(
                0.0,
                0.0,
                0.0,
                1.0,
                true,
                CycleMethod.NO_CYCLE,
                Stop(0.0, c("#7DB5D5")),
                Stop(1.0, c("#6298B9"))
            )
            and(hover) {
                backgroundColor += LinearGradient(
                    0.0,
                    0.0,
                    0.0,
                    1.0,
                    true,
                    CycleMethod.NO_CYCLE,
                    Stop(0.0, c("#76aece")),
                    Stop(1.0, c("#4F7E9C"))
                )
            }
            and(pressed) {
                backgroundColor += c("#4F7E9C")
            }
        }
        redButton {
            borderColor += box(c("#A93F40"))
            backgroundColor += LinearGradient(
                0.0,
                0.0,
                0.0,
                1.0,
                true,
                CycleMethod.NO_CYCLE,
                Stop(0.0, c("#db4e52")),
                Stop(1.0, c("#c34142"))
            )
            and(hover) {
                backgroundColor += LinearGradient(
                    0.0,
                    0.0,
                    0.0,
                    1.0,
                    true,
                    CycleMethod.NO_CYCLE,
                    Stop(0.0, c("#cf4c50")),
                    Stop(1.0, c("#9b3e3f"))
                )
            }
            and(pressed) {
                backgroundColor += c("#9b3e3f")
            }
        }
        navigationButton {
            prefWidth = 200.px
            prefHeight = 80.px
            contentDisplay = ContentDisplay.LEFT
            alignment = Pos.CENTER_LEFT
            fontSize = 18.px
            textFill = Color.WHITE
        }
        selectedButton {
            textFill = c(255, 197, 47)
            backgroundColor += c(0, 0, 0, 0.15)
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
            alignment = Pos.CENTER

        }
        greenLabel { backgroundColor += c("#75C759") }
        blueLabel { backgroundColor += c("#70A7C7")}
        redLabel { backgroundColor += c("#CF484A") }
        yellowLabel { backgroundColor += c("#dec314") }
        searchLabel {
            fontSize = 18.px
            textFill = Color.WHITE
        }

        topBar {
            backgroundColor += c("#24292e")
            spacing = 10.px
            alignment = Pos.CENTER_LEFT
            padding = box(16.px, 8.px)
        }
    }
}