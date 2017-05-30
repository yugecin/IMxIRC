package org.thisisgaming.robin.imxirc.net

import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset
import java.util.*

fun getpage(url: String): URLConnection {
	val connection = URL(url).openConnection()
	connection.setRequestProperty("Accept-Charset", "UTF-8")
	return connection
}

fun getpagePOST(url: String, vars: String): URLConnection {
	val connection = URL(url).openConnection()
	connection.doOutput = true
	connection.setRequestProperty("Accept-Charset", "UTF-8")
	connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
	connection.getOutputStream().write(vars.toByteArray(Charset.defaultCharset()))
	return connection
}

fun getFullPage(con: URLConnection): String {
	return Scanner(con.getInputStream()).useDelimiter("\\A").next()
}