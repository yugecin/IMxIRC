package org.thisisgaming.robin.imxirc.irc

import org.thisisgaming.robin.imxirc.OP
import org.thisisgaming.robin.imxirc.SERVERHOST

abstract class IRCChanHandler(val ctx: IRCContext, val topic: String) {

    fun onJoin() {
        sendInitialTopic(topic)
        onNames()
    }

    fun onNames() {
        ctx.write(":$SERVERHOST 353 ${ctx.nickname} = ##lobby :@$OP ${ctx.nickname}")
        ctx.write(":$SERVERHOST 366 ${ctx.nickname} ##lobby :End of /NAMES list.")
    }

    abstract fun onPart()
    abstract fun onMessage(msg: IRCMessage)
    abstract fun onMode(msg: IRCMessage)

    fun sendMessage(user: String, msg: String) {
        ctx.write(":$user PRIVMSG ##lobby :$msg")
    }

    protected fun sendInitialTopic(topic: String) {
        ctx.write(":$SERVERHOST 332 ${ctx.nickname} ##lobby :$topic")
        ctx.write(":$SERVERHOST 333 ${ctx.nickname} ##lobby $OP " + System.currentTimeMillis() / 1000L)
    }

}