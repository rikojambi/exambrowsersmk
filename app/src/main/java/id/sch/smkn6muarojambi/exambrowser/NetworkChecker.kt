package id.sch.smkn6muarojambi.exambrowser

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.net.InetSocketAddress
import java.net.Socket

/** Pemeriksaan jaringan lokal. Tidak memerlukan internet. */
object NetworkChecker {

    fun isWifiConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = manager.activeNetwork ?: return false
            val capabilities = manager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            @Suppress("DEPRECATION")
            val info = manager.activeNetworkInfo
            @Suppress("DEPRECATION")
            info != null && info.type == ConnectivityManager.TYPE_WIFI && info.isConnected
        }
    }

    /** Cek port server CBT menggunakan socket, bukan ping. */
    fun isCbtServerReachable(host: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(
                    InetSocketAddress(host, port),
                    CONNECT_TIMEOUT_MS
                )
                socket.isConnected
            }
        } catch (e: Exception) {
            false
        }
    }

    private const val CONNECT_TIMEOUT_MS = 1500
}
