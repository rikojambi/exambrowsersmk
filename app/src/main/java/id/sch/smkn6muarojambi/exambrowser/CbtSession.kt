package id.sch.smkn6muarojambi.exambrowser

import java.net.URI

/**
 * Menyimpan alamat CBT yang sedang dipakai selama sesi.
 * Pembatasan WebView menggunakan origin (skema + host + port) dari alamat ini.
 */
object CbtSession {

    private var address: CbtAddress? = null

    fun set(address: CbtAddress) {
        this.address = address
    }

    fun current(): CbtAddress? = address

    fun clear() {
        address = null
    }

    fun isAllowed(url: String?): Boolean {
        val active = address ?: return false
        if (url.isNullOrBlank()) return false
        if (url.equals("about:blank", ignoreCase = true)) return true

        return try {
            val uri = URI(url)
            val scheme = uri.scheme?.lowercase() ?: return false
            if (scheme != "http" && scheme != "https") return false
            if (uri.userInfo != null) return false

            val host = uri.host?.lowercase() ?: return false
            val port = when {
                uri.port >= 0 -> uri.port
                scheme == "https" -> 443
                else -> 80
            }

            scheme == active.scheme && host == active.host && port == active.port
        } catch (e: Exception) {
            false
        }
    }
}
