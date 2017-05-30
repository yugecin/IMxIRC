package org.thisisgaming.robin.imxirc.fb

import org.thisisgaming.robin.imxirc.net.*
import org.thisisgaming.robin.imxirc.trimDistance
import java.net.*
import java.util.*

// algorithm from https://github.com/Schmavery/facebook-chat-api/
class Fb {

	val cookiemng: CookieManager = CookieManager(null, CookiePolicy.ACCEPT_ALL)
	var loggedin = false

	init {
		CookieHandler.setDefault(cookiemng)
	}

	fun login(email: String, pass: String) {
		// load fb to get some cookies
		val page = getFullPage(getpage("https://facebook.com"))
		val inputs = loadform(page, "login_form")

		// lsd
		inputs.put("email", email)
		inputs.put("pass", pass)
		inputs.put("lgndim", String(Base64.getEncoder().encode("{\"w\":1440,\"h\":900,\"aw\":1440,\"ah\":834,\"c\":24}".toByteArray())))
		inputs.put("default_persistent", "0")
		inputs.put("locale", "en_US")
		inputs.put("timezone", "240")
		inputs.put("lgnjs", "" + System.currentTimeMillis() / 1000L)
		getmorecookies(page)

		//login
		val loginresp = getpagePOST("https://www.facebook.com/login.php?login_attempt=1&lwv=110", makepostvars(inputs))
		var loc: String? = loginresp.getHeaderField("location")
		if (loc == null) {
			loc = loginresp.getHeaderField("Location")
		}
		if (loc == null) {
			println("failed login")
			return
		}
		println("location to " + loc)

		// load again to get some more cookies
		getpage("https://facebook.com")
	}

	private fun makepostvars(vars: HashMap<String, String>): String {
		var query = ""
		vars.entries.forEach {
			query += "&${it.key}=" + URLEncoder.encode(it.value, "UTF-8")
		}
		return query.substring(1)
	}

	private fun loadform(page: String, formid: String): HashMap<String, String> {
		val inputs = HashMap<String, String>()
		val formpos = page.indexOf(formid)
		var loginform = page.substring(formpos..page.indexOf("</form>", formpos))

		while (true) {
			val nextinput = loginform.indexOf("<input")
			val end = loginform.indexOf(">", nextinput) // assuming its not in an attribute
			if (nextinput == -1 || end == -1) {
				break
			}
			val thisinput = loginform.substring(nextinput..end)
			loginform = loginform.substring(end)
			var attidx = thisinput.indexOf("name=\"")
			if (attidx == -1) {
				continue
			}
			attidx += 6
			val name = thisinput.substring(attidx..thisinput.indexOf('"', attidx))
			var nameidx = thisinput.indexOf("value=\"")
			if (nameidx == -1) {
				continue
			}
			nameidx += 7
			val value = thisinput.substring(nameidx..thisinput.indexOf('"', nameidx))
			inputs.put(name, value)
		}
		return inputs
	}

	private fun getmorecookies(page: String) {
		var p = page
		while (true) {
			val idx = p.indexOf("\"_js_")
			if (idx == -1) {
				return
			}
			val end = p.indexOf(']', idx)
			if (end == -1) {
				return
			}
			val cookiedata = p.substring(idx..end).split(',')
			val cookie = HttpCookie(cookiedata[0].trimDistance(1), cookiedata[1].trimDistance(1))
			cookie.path = cookiedata[3].trimDistance(1)
			cookie.maxAge = cookiedata[2].toLong()
			println("cookie ${cookie.name} ${cookie.value} ${cookie.path} ${cookie.maxAge}")
			cookiemng.cookieStore.add(URI("https://www.facebook.com"), cookie)
			p = p.substring(end)
		}
	}

}
