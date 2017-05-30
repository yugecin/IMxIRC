package org.thisisgaming.robin.imxirc.irc

import org.thisisgaming.robin.imxirc.*
import org.thisisgaming.robin.imxirc.irc.chanhandlers.IRCChanHandler
import org.thisisgaming.robin.imxirc.irc.chanhandlers.LobbyChanHandler
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

class IRCContext(private val s: Socket, private val i: BufferedReader, private val o: PrintWriter) {

    var nickname = ""
    var clienthost = ""
    val rand = Random()
    var kickstarted = false
    val chanhandlers = HashMap<String, IRCChanHandler>()
    var running = true
    val pingthread = Thread({
        while (true) {
            // hmmhhmmm not thread safe but it'll stop when it fails right? .. right?
            while (running && !s.isClosed) {
                if (!s.isClosed) {
                    sendPing()
                }
                Thread.sleep(3 * 60 * 1000)
            }
        }
    })

    init {
        chanhandlers.put("##lobby", LobbyChanHandler(this))
    }

    fun work() {
        try {
            while (true) {
                val line = i.readLine()
                if (line == null) {
                    s.sclose()
                    return
                }
                handleMessage(parseIRCMessage(line))
            }
        } finally {
            running = false
        }
    }

    private fun handleMessage(msg: IRCMessage) {
        println("<- ${msg.command} ${msg.params} :${msg.text}")
        when (msg.command) {
            "NICK" -> nickname = msg.params[0]
            "USER" -> {
                clienthost = msg.params[1].trimDistance(1)
                pingthread.start()
            }
            "PONG" -> kickstart()
            "MOTD" -> doMotd()
            "LUSERS" -> listUsers()
            "QUIT" -> close()
            "PRIVMSG" -> privmsg(msg)
            "NAMES" -> {}
            "JOIN" -> chanhandlers[msg.params[0]]?.onJoin()
            "PART" -> chanhandlers[msg.params[0]]?.onPart()
            "MODE" -> if (msg.params.size == 1) chanhandlers[msg.params[0]]?.onMode(msg)
            else -> write(":$SERVERHOST NOTICE IMXIRC :unknown command ${msg.command}")
        }
    }

    private fun privmsg(msg: IRCMessage) {
        if (msg.params[0][0] == '#') {
            chanhandlers[msg.params[0]]?.onMessage(msg)
            return
        }
        write(":$SERVERHOST NOTICE IMXRC :unknown privmsg target ${msg.params[0]}")
    }

    private fun kickstart() {
        if (kickstarted) {
            return
        }
        kickstarted = true
        // thanks https://www.alien.net.au/irc/irc2numerics.html
        // also taken from connections with Unreal
        write(":$SERVERHOST 001 $nickname :Welcome to IMxIRC")
        write(":$SERVERHOST 002 $nickname :Your host is 127.0.0.1, running version imxirc-$VERSION")
        //o.println(":$serverhost 003 $nickname :This server was created <date>")
        write(":$SERVERHOST 004 $nickname imxirc imxirc-$VERSION oix n")
        write(":$SERVERHOST 005 $nickname NICKLEN=30 TOPICLEN=307 CHANNELLEN=32 PREFIX=(o)@ :are supported by this server")
        write(":$SERVERHOST 396 $nickname cyber.space :is now your displayed host")
        listUsers()
        doMotd()
        write(":$nickname MODE $nickname :+ix")
        write(":$nickname JOIN :##lobby")
        chanhandlers["##lobby"]!!.onJoin()
    }

    private fun doMotd() {
        write(":$SERVERHOST 275 $nickname :- imxirc Message of the Day -")
        write(":$SERVERHOST 272 $nickname :yey it works")
        write(":$SERVERHOST 276 $nickname :End of /MOTD command.")
    }

    private fun listUsers() {
        write(":$SERVERHOST 251 $nickname :There are 1 users and 0 invisible on 1 server")
        write(":$SERVERHOST 252 $nickname 0 :operator(s) online")
        write(":$SERVERHOST 254 $nickname 0 :channel(s) formed") // TODO if this even matters
        write(":$SERVERHOST 255 $nickname :I have 1 clients and 1 servers")
    }

    private fun sendPing() {
        val code = String(Array(8, {_ -> 'A' + rand.nextInt(26)}).toCharArray())
        write("PING :$code")
    }

    private fun close() {
        write("ERROR :Closing Link: $nickname[$clienthost] (Quit: $nickname)")
        s.sclose()
    }

    fun write(msg: String) {
        println("-> $msg")
        o.println(msg)
        o.flush()
    }

}
