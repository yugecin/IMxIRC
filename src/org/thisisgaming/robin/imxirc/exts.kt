package org.thisisgaming.robin.imxirc

import java.io.Closeable

fun String.substringToIndex(idx: Int) = if (0 < idx && idx < this.length) this.substring(0, idx) else this
fun String.substringFromIndex(idx: Int) = if (0 < idx && idx < this.length) this.substring(idx) else ""

fun Closeable.sclose() {
    try {
        this.close()
    } catch(ignored: Exception) {}
}
