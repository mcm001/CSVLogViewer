package com.mcm.logviewer

object CSVLogParser {

    fun parseData(data: List<String>): List<Pair<String, List<Number>>> {
        if(data.isEmpty()) return listOf()
        val headerRow = data[0].split(",").map { it.replace("\r", "") }
        val contentRows = data.subList(1, data.size).mapNotNull {
            it.split(",")
                .let { it3 -> if (it3.size != headerRow.size) null else it3 }
                ?.map { it2 -> if (it2.isEmpty()) 0.0 else it2.toDouble() }
        }

        val ret = arrayListOf<Pair<String, List<Number>>>()
        headerRow.forEachIndexed { i, header ->
            ret.add(header to contentRows.map { it[i] })
        }
        return ret
    }

}