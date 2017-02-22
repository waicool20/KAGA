package com.waicool20.kaga.views

import javafx.scene.layout.GridPane
import tornadofx.View


class StatsView: View() {
    override val root: GridPane by fxml("/views/stats.fxml", hasControllerAttribute = true)

}
