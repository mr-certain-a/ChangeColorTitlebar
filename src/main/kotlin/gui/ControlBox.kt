package gui

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.paint.Color
import javafx.stage.Stage

// コントロールボックスの描画色
private val foregroundColor: Color = Color.web("#EEEEEE")

private data class Rect(val left: Double, val top: Double, val right: Double, val bottom: Double)

fun closeButton() = Button().apply {
    styleClass.add("closeButton")
    onAction = EventHandler {
        Platform.exit()
    }

    graphic = Canvas(18.0, 30.0).apply {
        graphicsContext2D.stroke = foregroundColor
        with(Rect(width / 2 - 5, height / 2 - 5, width / 2 + 5, height / 2 + 5)) {
            graphicsContext2D.strokeLine(left, top, right, bottom)
            graphicsContext2D.strokeLine(right, top, left, bottom)
        }
    }
}

fun maxButton(stage: Stage) = Button().apply {
    styleClass.add("systemButton")
    onAction = EventHandler {
        stage.isMaximized = true
    }
    graphic = Canvas(18.0, 30.0).apply {
        graphicsContext2D.stroke = foregroundColor
        graphicsContext2D.strokeRect(width / 2 - 5, height / 2 - 5, 10.0, 10.0)
    }
}

fun minButton(stage: Stage) = Button().apply {
    styleClass.add("systemButton")
    onAction = EventHandler {
        stage.isIconified = true
    }
    graphic = Canvas(18.0, 30.0).apply {
        graphicsContext2D.stroke = foregroundColor
        graphicsContext2D.strokeLine(width / 2 - 5, height / 2, width / 2 + 5, height / 2)
    }
}
