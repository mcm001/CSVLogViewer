package com.mcm.logviewer

import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.CheckBox
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import kfoenix.jfxbutton
import kfoenix.jfxcheckbox
import kfoenix.jfxtextfield
import tornadofx.*
import java.io.File

typealias LogFile = ObservableList<List<Number>>

class MainView : View("Log Viewer") {

    private val xAxis = NumberAxis()
    private val yAxis = NumberAxis()

    private val mainChart = LineChart(xAxis, yAxis)
        .apply {
            title = "chart"
            style {
                backgroundColor = multi(Color.LIGHTGRAY)
            }
            lookup(".chart-plot-background").style +=
                "-fx-background-size: stretch;" +
                        "-fx-background-position: top right;" +
                        "-fx-background-repeat: no-repeat;"

            axisSortingPolicy = LineChart.SortingPolicy.NONE
            isLegendVisible = true
            animated = false
            createSymbols = false

            verticalGridLinesVisible = false
            isHorizontalGridLinesVisible = false
        }

    private val seriesBox = vbox {
        style {
            paddingAll = 20.0
//                maxWidth = 300.px
            spacing = 5.px
        }
    }

    private var currentLogFileName = SimpleStringProperty("No Log File Selected")

    override val root: Parent = vbox {
        stylesheets += resources["/AppStyle.css"]

        prefHeight = 705.0
        prefWidth = 1200.0

        style {
            paddingAll = 20.0
//                maxWidth = 300.px
            spacing = 5.px
        }

        hbox {

            style {
                paddingAll = 20.0
//                maxWidth = 300.px
                spacing = 5.px
            }

            jfxbutton("Select Local Log File") {

                action {
                    val files = chooseFile(
                        title = "Select Log File",
                        filters = arrayOf(FileChooser.ExtensionFilter("CSV Files", "*.csv"))
                    ) { initialDirectory = File("./temp") }
                    val lines = files.firstOrNull()?.readText()?.split("\n") ?: listOf()
                    if (lines.isEmpty()) return@action
                    val parsedData = CSVLogParser.parseData(lines)
                    println("data:\n${parsedData}")
                    currentLogFileName.set(files.firstOrNull()?.name ?: "")

                    updateChart(parsedData)
                }
            }

//        jfxbutton("Select Remote Log File") {
//            action {
//                object: Fragment() {
//                    override val root = vbox {
//                        hbox {
//                            jfxtextfield("Team number or IP") {  }
//                        }
//
//                        val comboBox = jfxcombobox(possibleRemoteLogFiles) {
//                            items.addAll("hello", "there")
//                        }
//                    }
//                }
//            }
//        }


            val currentLogFile = jfxtextfield(currentLogFileName)

        }

        mainChart.attachTo(this)
        seriesBox.attachTo(this)
    }

    private fun updateChart(parsedData: List<Pair<String, List<Number>>>) {
        mainChart.data.clear()
        seriesBox.clear()

        val xAxisList = parsedData.first().second // time is always the first column
        val data = parsedData.subList(1, parsedData.size)

        data.forEachIndexed { index, it ->
            val series = Series<Number, Number>()
            series.name = it.first // set name to the header of that column

            it.second.forEachIndexed { index2, it2 -> // iterate over each row
                series.data.add(
                    XYChart.Data(xAxisList[index2], it2)
                )
            }
            println("adding series:\n$series")
            mainChart.data.add(series)

            // add an hbox with the series name, and a visable tick mark
            seriesBox.add(
                jfxcheckbox(it.first) {
                    this.isSelected = true
                    selectedProperty().addListener { _, _, newValue ->
                        try {
                            if (!newValue) {
                                println("hiding series!")
                                mainChart.data[mainChart.data.indexOf(series)].node.isVisible = false
                            } else {
                                println("un-hiding series")
                                mainChart.data[mainChart.data.indexOf(series)].node.isVisible = true
                            }
                            mainChart.autosize()
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        }
                    }
                })
        }
    }
}