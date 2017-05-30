package org.thisisgaming.robin.imxirc

import java.io.Closeable

fun String.substringToIndex(idx: Int) = if (0 < idx && idx < this.length) this.substring(0, idx) else this
fun String.substringFromIndex(idx: Int) = if (0 < idx && idx < this.length) this.substring(idx) else ""
fun String.trimDistance(dist: Int) = if (dist * 2 <= this.length) this.substring(1..this.length - dist * 2) else this

fun Closeable.sclose() {
    try {
        this.close()
    } catch(ignored: Exception) {}
}
