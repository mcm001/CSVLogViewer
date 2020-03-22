package com.mcm.logviewer.remote

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.util.*


fun main() {
//    println(RemoteLogFile.getFileList("/home/pi", "10.0.0.30", "pi", "witty.moose"))
    RemoteLogFile.getFile("/home/pi", "data.csv", "10.0.0.30", "pi", "witty.moose")
}

object RemoteLogFile {

    private val jsch = JSch()

    fun getFileList(
        directory: String,
        host: String? = null,
        username: String = "lvuser",
        password: String = ""
    ): List<String> {
        val hostName = host ?: getRoborioIP()
        val session = jsch.getSession(username, hostName, 22)
        session.setConfig("StrictHostKeyChecking", "no")
        session.setPassword(password)
        session.connect()

        val channel = session.openChannel("sftp") as ChannelSftp
        channel.connect()

        channel.cd(directory)
        val files = channel.ls(directory) as Vector<ChannelSftp.LsEntry>

        channel.disconnect()
        session.disconnect()
        return files.map { it.filename }.filter { !(it.matches(Regex(".")) || it.matches(Regex(".."))) }
    }

    fun getFile(
        directory: String,
        fileName: String,
        host: String? = null,
        username: String = "lvuser",
        password: String = ""
    ): List<String> {
        val hostName = host ?: getRoborioIP()
        val session = jsch.getSession(username, hostName, 22)
        session.setConfig("StrictHostKeyChecking", "no")
        session.setPassword(password)
        session.connect()

        val channel = session.openChannel("sftp") as ChannelSftp
        channel.connect()

        channel.cd(directory)
        val fileStream = channel.get(fileName)

        val br = BufferedReader(InputStreamReader(fileStream))
        var line: String?
        var ret = arrayListOf<String>()
        while (br.readLine().also { line = it } != null) {
            println(line)
            if(!line.isNullOrEmpty() && line != null) ret.add(line!!)
        }
        println(ret)
        br.close()

        channel.disconnect()
        session.disconnect()
        return ret
    }

    fun getRoborioIP(teamNum: Int? = null): String? {
        // RIO over usb
        "127.0.01".let { if (isHostAlive(it)) return it }

        // all other checks require a team number
        if (teamNum == null) return null

        // 10.te.am.2
        "10.${(teamNum / 100).toInt()}.${(teamNum % 100).toInt()}.2".let { if (isHostAlive(it)) return it }

        // roboRIO-<team>-FRC.local
        "roboRIO-$teamNum-FRC.local".let { if (isHostAlive(it)) return it }

        // roboRIO-<team>-FRC.lan
        "roboRIO-$teamNum-FRC.lan".let { if (isHostAlive(it)) return it }

        // roboRIO-<team>-FRC.frc-field.local
        "roboRIO-$teamNum-FRC.frc-field.local".let { if (isHostAlive(it)) return it }

        return null
    }

    fun isHostAlive(ip: String): Boolean {
        val addr = InetAddress.getByName(ip)
        return addr.isReachable(5000)
    }

}