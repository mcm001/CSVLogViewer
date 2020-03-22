package com.mcm.logviewer

import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator
import javafx.stage.StageStyle
import com.mcm.logviewer.generator.fragments.InvalidTrajectoryFragment
import tornadofx.App
import tornadofx.find
import tornadofx.launch

class Main : App(MainView::class) {
    init {
        Settings
    }

    override fun stop() {
        Settings.save()
    }
}

fun main(args: Array<String>) {
    launch<Main>(args)
}