package org.thisisgaming.robin.imxirc.irc

import org.thisisgaming.robin.imxirc.substringFromIndex
import org.thisisgaming.robin.imxirc.substringToIndex

class IRCMessage constructor(val command: String, val params: List<String>, val text: String)

fun parseIRCMessage(raw: String): IRCMessage {
	val sep = raw.indexOf(':') - 1
	val text = raw.substringFromIndex(sep)
	val parts = raw.substringToIndex(sep).split(' ')
	return IRCMessage(parts[0], parts.subList(1, parts.size), if (text.isNotEmpty()) text.substring(2) else text)
}

