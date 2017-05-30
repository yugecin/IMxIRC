package org.thisisgaming.robin.imxirc.irc.chanhandlers

import org.thisisgaming.robin.imxirc.OP
import org.thisisgaming.robin.imxirc.irc.COLOR
import org.thisisgaming.robin.imxirc.irc.IRCContext
import org.thisisgaming.robin.imxirc.irc.IRCMessage
import org.thisisgaming.robin.imxirc.irc.RESET

class LobbyChanHandler(ctx: IRCContext) : IRCChanHandler(ctx, "Welcome to ImxIRC") {

    override fun onPart() {
        // no you won't
        ctx.write(":${ctx.nickname} JOIN :##lobby")
        onJoin()
        sendMessage(OP, "${COLOR}04there's no escape$RESET")
    }

    override fun onMessage(msg: IRCMessage) {
        sendMessage(OP, "hi ${ctx.nickname}")
    }

}
