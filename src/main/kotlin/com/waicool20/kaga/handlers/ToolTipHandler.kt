package com.waicool20.kaga.handlers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.getParentTabPane
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class LabelTip(val id: String, val description: String)

class ToolTipHandler(stage: Stage) : EventHandler<KeyEvent> {
    val tooltips: List<LabelTip> = run {
        val stream = Kaga::class.java.classLoader.getResourceAsStream("tooltips.json")
        val mapper = jacksonObjectMapper()
        mapper.readValue<List<LabelTip>>(stream, mapper.typeFactory.constructCollectionType(List::class.java, LabelTip::class.java))
    }
    private var showingTooltip = Tooltip()
    private var nodeUnderMouse: Node? = null
    private var mouseX = 0.0
    private var mouseY = 0.0

    private var isKeyPressed = false

    init {
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, { event ->
            mouseX = event.screenX
            mouseY = event.screenY
            val node = event.target
            if (node is Node) {
                if (nodeUnderMouse == node) return@addEventFilter
                nodeUnderMouse = node
                if (isKeyPressed) {
                    showingTooltip.hide()
                    updateTooltip()
                }
            } else {
                nodeUnderMouse = null
            }
        })
    }

    override fun handle(event: KeyEvent?) {
        if (event == null || event.code != KeyCode.SHIFT) return
        isKeyPressed = event.isShiftDown
        showingTooltip.hide()
        if (event.eventType == KeyEvent.KEY_PRESSED) {
            updateTooltip()
        }
    }

    private fun updateTooltip() {
        if (nodeUnderMouse == null) return
        with(nodeUnderMouse!!) {
            val tab = getParentTabPane()?.selectionModel?.selectedItem
            val labeltip = if (tab == null) {
                tooltips.find { it.id == nodeUnderMouse?.parent?.id }
            } else {
                tooltips.find { it.id == "${tab.text}-${nodeUnderMouse?.parent?.id}" }
            }
            if (labeltip != null) {
                showingTooltip = Tooltip(labeltip.description)
                showingTooltip.isWrapText = true
                showingTooltip.maxWidth = 500.0
                val bounds = localToScene(boundsInLocal)
                showingTooltip.show(nodeUnderMouse, bounds.maxX + scene.window.x, bounds.minY + scene.window.y)
            }
        }
    }
}
