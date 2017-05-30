package org.thisisgaming.robin.imxirc.irc

import org.thisisgaming.robin.imxirc.VERSION
import org.thisisgaming.robin.imxirc.sclose
import org.thisisgaming.robin.imxirc.trimDistance
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

class IRCContext(private val s: Socket, private val i: BufferedReader, private val o: PrintWriter) {

    var nickname = ""
    val serverhost = "imxirc"
    var clienthost = ""
    val rand = Random()
    var kickstarted = false

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
        println("<- ${msg.command} ${msg.params} :${msg.text}")
        when (msg.command) {
            "NICK" -> nickname = msg.params[0]
            "USER" -> {
                clienthost = msg.params[1].trimDistance(1)
                sendPing()
            }
            "PONG" -> kickstart()
            "MOTD" -> doMotd()
            "LUSERS" -> listUsers()
            "QUIT" -> close()
            else -> o.println(":$serverhost NOTICE IMXRC :unknown command ${msg.command}")
        }
    }

    private fun kickstart() {
        if (kickstarted) {
            return
        }
        kickstarted = true
        // thanks https://www.alien.net.au/irc/irc2numerics.html
        // also taken from connections with Unreal
        write(":$serverhost 001 $nickname :Welcome to IMxIRC")
        write(":$serverhost 002 $nickname :Your host is 127.0.0.1, running version imxirc-$VERSION")
        //o.println(":$serverhost 003 $nickname :This server was created <date>")
        write(":$serverhost 004 $nickname imxirc imxirc-$VERSION oix n")
        write(":$serverhost 005 $nickname NICKLEN=30 TOPICLEN=307 CHANNELLEN=32 PREFIX=(o)@ :are supported by this server")
        write(":$serverhost 396 $nickname cyber.space :is now your displayed host")
        listUsers()
        doMotd()
        write(":$nickname MODE $nickname :+ix")
    }

    private fun doMotd() {
        write(":$serverhost 275 $nickname :- imxirc Message of the Day -")
        write(":$serverhost 272 $nickname :yey it works")
        write(":$serverhost 276 $nickname :End of /MOTD command.")
    }

    private fun listUsers() {
        write(":$serverhost 251 $nickname :There are 1 users and 0 invisible on 1 server")
        write(":$serverhost 252 $nickname 0 :operator(s) online")
        write(":$serverhost 254 $nickname 0 :channel(s) formed") // TODO if this even matters
        write(":$serverhost 255 $nickname :I have 1 clients and 1 servers")
    }

    private fun sendPing() {
        val code = String(Array(8, {_ -> 'A' + rand.nextInt(26)}).toCharArray())
        write("PING :$code")
    }

    private fun close() {
        write("ERROR :Closing Link: $nickname[$clienthost] (Quit: $nickname)")
        s.sclose()
    }

    private fun write(msg: String) {
        println("-> $msg")
        o.println(msg)
        o.flush()
    }

}
