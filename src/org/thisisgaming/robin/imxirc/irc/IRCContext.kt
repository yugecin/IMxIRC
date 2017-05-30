package org.thisisgaming.robin.imxirc.irc

import org.thisisgaming.robin.imxirc.sclose
import java.io.BufferedReader
import java.io.OutputStream
import java.net.Socket

class IRCContext(private val s: Socket, private val i: BufferedReader, private val o: OutputStream) {

    fun work() {
        while (true) {
            val line = i.readLine()
            if (line == null) {
                s.sclose()
                return
            }
            handleMessage(parseIRCMessage(line))
        }
    }

    private fun handleMessage(msg: IRCMessage) {
        println("${msg.command} ${msg.params} :${msg.text}")
    }

}
