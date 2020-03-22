package com.mcm.logviewer

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import java.io.File
import java.io.FileReader
import java.io.FileWriter


object Settings {
    val ip = SimpleStringProperty("10.59.40.2")

    private val gson = GsonBuilder().registerTypeAdapter<Settings> {
        write {
            beginArray()
            value(ip.value)
            endArray()
        }
        read {
            beginArray()
            ip.set(nextString())
            endArray()
            return@read Settings
        }
    }.create()!!

    init {
        val file = File("settings.json")
        if (file.exists()) {
            try {
                gson.fromJson<Settings>(FileReader(file))
            } catch (e: Exception) {
                file.delete()
                val writer = FileWriter(file)
                writer.write(gson.toJson(Settings))
                writer.close()
            }
        } else {
            val writer = FileWriter(file)
            writer.write(gson.toJson(Settings))
            writer.close()
        }
    }

    fun save() {
        val writer = FileWriter(File("settings.json"))
        writer.write(gson.toJson(Settings))
        writer.close()
    }
}

