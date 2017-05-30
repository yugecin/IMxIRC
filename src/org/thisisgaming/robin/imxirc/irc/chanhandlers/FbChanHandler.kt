package org.thisisgaming.robin.imxirc.irc.chanhandlers

import org.thisisgaming.robin.imxirc.OP
import org.thisisgaming.robin.imxirc.fb.Fb
import org.thisisgaming.robin.imxirc.irc.IRCContext
import org.thisisgaming.robin.imxirc.irc.IRCMessage

class FbChanHandler(ctx: IRCContext) : IRCChanHandler(ctx, "Because opening facebook is a hassle") {

    val fb = Fb()

    override fun onJoin() {
        super.onJoin()
        sendMessage(OP, "To login: /msg q <email> <password>")
        sendMessage(OP, "You'll be sending you password in plain-text to ${ctx.connectedhost}")
        sendMessage(OP, "Use this with common sense (or don't use it at all)")
        sendMessage(OP, "Beware of logging in your IRC client!")
    }

    override fun onMessage(msg: IRCMessage) {
    }

}
