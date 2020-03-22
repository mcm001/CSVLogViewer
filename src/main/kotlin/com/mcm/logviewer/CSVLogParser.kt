package com.mcm.logviewer

object CSVLogParser {

    fun parseData(data: List<String>): List<Pair<String, List<Number>>> {
        if(data.isEmpty()) return listOf()
        val headerRow = data[0].split(",").map { it.replace("\r", "") }
        val contentRows = data.subList(1, data.size).map { it.split(",").map {it2 -> it2.toDouble() } }

        val ret = arrayListOf<Pair<String, List<Number>>>()
        headerRow.forEachIndexed { i, header ->
            ret.add(header to contentRows.map { it[i] })
        }
        return ret
    }

}