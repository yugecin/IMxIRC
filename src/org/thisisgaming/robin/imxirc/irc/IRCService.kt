package org.thisisgaming.robin.imxirc.irc

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket

fun startIRCService() {
	val socket = ServerSocket()
	socket.bind(InetSocketAddress(InetAddress.getByAddress(byteArrayOf(127, 0, 0, 1)), 6667))
	while (true) {
		val s = socket.accept()
		val i = BufferedReader(InputStreamReader(s.inputStream))
		val o = PrintWriter(s.outputStream)
		try {
			IRCContext(s, i, o).work()
		} catch (ignored: Exception) { }
	}
}
