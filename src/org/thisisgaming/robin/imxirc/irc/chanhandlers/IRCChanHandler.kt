package org.thisisgaming.robin.imxirc.irc.chanhandlers

import org.thisisgaming.robin.imxirc.OP
import org.thisisgaming.robin.imxirc.SERVERHOST
import org.thisisgaming.robin.imxirc.irc.IRCContext
import org.thisisgaming.robin.imxirc.irc.IRCMessage

abstract class IRCChanHandler(val ctx: IRCContext, val topic: String) {

    open fun onJoin() {
        sendInitialTopic(topic)
        ctx.write(":$SERVERHOST 353 ${ctx.nickname} = ##lobby :@$OP ${ctx.nickname}")
        ctx.write(":$SERVERHOST 366 ${ctx.nickname} ##lobby :End of /NAMES list.")
    }

    open fun onPart() {
    }

    abstract fun onMessage(msg: IRCMessage)

    open fun onMode(msg: IRCMessage) {
        ctx.write(":$SERVERHOST 324 ${ctx.nickname} ##lobby +n")
    }

    fun sendMessage(user: String, msg: String) {
        ctx.write(":$user PRIVMSG ##lobby :$msg")
    }

    protected fun sendInitialTopic(topic: String) {
        ctx.write(":$SERVERHOST 332 ${ctx.nickname} ##lobby :$topic")
        ctx.write(":$SERVERHOST 333 ${ctx.nickname} ##lobby $OP " + System.currentTimeMillis() / 1000L)
    }

}