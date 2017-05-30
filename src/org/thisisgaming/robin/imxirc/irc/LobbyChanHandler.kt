package org.thisisgaming.robin.imxirc.irc

import org.thisisgaming.robin.imxirc.OP
import org.thisisgaming.robin.imxirc.SERVERHOST

class LobbyChanHandler(ctx: IRCContext) : IRCChanHandler(ctx, "Welcome to ImxIRC") {

    override fun onPart() {
        // no you won't
        ctx.write(":${ctx.nickname} JOIN :##lobby")
        onJoin()
        sendMessage(OP, "${COLOR}04there's no escape$RESET")
    }

    override fun onMode(msg: IRCMessage) {
        ctx.write(":$SERVERHOST 324 ${ctx.nickname} ##lobby +n")
    }

    override fun onMessage(msg: IRCMessage) {
        sendMessage(OP, "hi ${ctx.nickname}")
    }

}
