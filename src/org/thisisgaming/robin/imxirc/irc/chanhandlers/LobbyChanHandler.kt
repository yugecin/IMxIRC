package org.thisisgaming.robin.imxirc.irc.chanhandlers

import org.thisisgaming.robin.imxirc.OP
import org.thisisgaming.robin.imxirc.VERSION
import org.thisisgaming.robin.imxirc.irc.COLOR
import org.thisisgaming.robin.imxirc.irc.IRCContext
import org.thisisgaming.robin.imxirc.irc.IRCMessage
import org.thisisgaming.robin.imxirc.irc.RESET

class LobbyChanHandler(ctx: IRCContext) : IRCChanHandler(ctx, "Welcome to IMxIRC") {

    override fun onJoin() {
        super.onJoin()
        sendMessage(OP, "You're connected to imxirc-$VERSION")
        sendMessage(OP, " -== Hubs ==-")
        sendMessage(OP, "##fb - ${COLOR}00,12Facebook$RESET")
    }

    override fun onMessage(msg: IRCMessage) {
        sendMessage(OP, "hi ${ctx.nickname}")
    }

}
