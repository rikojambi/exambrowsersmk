package id.sch.smkn6muarojambi.exambrowser

import java.net.URI

/**
 * Alamat CBT yang sudah divalidasi.
 *
 * Aturan:
 * - boleh IP lokal atau hostname lokal;
 * - http:// dan https:// diterima;
 * - jika tidak ada skema, http:// ditambahkan otomatis;
 * - skema lain ditolak;
 * - username/password pada URL ditolak;
 * - host, port, dan path divalidasi dengan URI (bukan contains()).
 */
data class CbtAddress(
    val url: String,
    val host: String,
    val port: Int,
    val scheme: String,
    val path: String
) {
    val origin: String
        get() = scheme + "://" + host + ":" + port

    companion object {
        private val SCHEMA_REGEX = Regex("^[A-Za-z][A-Za-z0-9+.-]*://")

        fun parse(input: String): CbtAddress? {
            val trimmed = input.trim()
            if (trimmed.isEmpty()) return null

            var candidate = trimmed
            if (!SCHEMA_REGEX.containsMatchIn(candidate)) {
                candidate = "http://$candidate"
            }

            val uri = try {
                URI(candidate)
            } catch (e: Exception) {
                return null
            }

            val scheme = uri.scheme?.lowercase() ?: return null
            if (scheme != "http" && scheme != "https") return null
            if (uri.userInfo != null) return null

            val host = uri.host ?: return null
            if (host.isBlank() || host.any { it.isWhitespace() }) return null

            val port = when {
                uri.port >= 0 -> uri.port
                scheme == "https" -> 443
                else -> 80
            }
            if (port !in 1..65535) return null

            val rawPath = uri.rawPath ?: ""
            if (rawPath.isNotEmpty() && !rawPath.startsWith("/")) return null
            if (rawPath.split('/').any { it == ".." }) return null

            val path = try {
                URI(rawPath).normalize().rawPath ?: rawPath
            } catch (e: Exception) {
                rawPath
            }

            val authority = when {
                scheme == "http" && port == 80 -> host
                scheme == "https" && port == 443 -> host
                else -> "$host:$port"
            }
            val query = uri.rawQuery
            val cleanUrl = scheme + "://" + authority + path +
                (if (query.isNullOrEmpty()) "" else "?$query")

            return CbtAddress(
                url = cleanUrl,
                host = host.lowercase(),
                port = port,
                scheme = scheme,
                path = path
            )
        }
    }
}
