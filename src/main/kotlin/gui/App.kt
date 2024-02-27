package gui

import com.sun.jna.platform.win32.*
import gui.jna.MyWindowProc
import javafx.application.*
import javafx.geometry.*
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.*
import javafx.scene.layout.*
import javafx.stage.*
import java.nio.file.Paths
import java.util.*

class App: Application() {
    companion object {
        const val TITLE_STRING = "Change the color of title bar In JavaFX."

        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java, *args)
        }
    }

    private val temporaryTitle = UUID.randomUUID().toString()
    private val icon = Image(Paths.get("images/icon.png").toUri().toString())
    private val css = Paths.get("design.css").toUri().toString()

    private fun createToolBar(stage: Stage) = ToolBar().apply {
        styleClass.add("toolBar")
        val height = 30.0
        prefHeight = height
        minHeight = height
        maxHeight = height

        val pane = Pane()
        HBox.setHgrow(pane, Priority.ALWAYS)

        val labelText = TITLE_STRING
        val label = Label(labelText)

        val icon = ImageView().apply {
            image = icon
            fitWidth = 16.0
            isPreserveRatio = true
            isSmooth = true
        }

        items.addAll(icon, label, pane, minButton(stage), maxButton(stage), closeButton())
    }

    private fun createClientPane() = SplitPane().apply {
        styleClass.add("clientPane")

        // 水平方向に30%で分割
        setDividerPositions(0.30)
        orientation = Orientation.HORIZONTAL

        items.addAll(
            // 左ペイン
            VBox().apply {
                alignment = Pos.TOP_CENTER
                children.add(Button("Button placement sample"))
                children.add(Button("Try placing some"))
                children.add(Button("How about one more"))
            },
            // 右ペイン
            BorderPane().apply {
                styleClass.add("rightFrame")
            })
    }

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.UNDECORATED)

        val rootPane = BorderPane().apply {
            top = createToolBar(stage)
            center = createClientPane()
        }

        stage.isResizable = true
        stage.icons.add(icon)
        stage.title = temporaryTitle
        stage.scene = Scene(rootPane, 800.0, 600.0).apply {
            stylesheets.add(css)
        }

        stage.show()

        MyWindowProc().init(getWindowPointer())
        stage.title = TITLE_STRING
    }

    private fun getWindowPointer(): WinDef.HWND =
        User32.INSTANCE.FindWindow(null, temporaryTitle)
}
