package com.mcm.logviewer

import tornadofx.App
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