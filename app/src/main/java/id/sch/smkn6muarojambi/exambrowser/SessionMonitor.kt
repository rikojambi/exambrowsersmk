package id.sch.smkn6muarojambi.exambrowser

/**
 * Mencatat kejadian aplikasi keluar/fokus hilang HANYA di memori selama sesi.
 * Tidak ada database, tidak ada file, tidak ada pengiriman data.
 */
object SessionMonitor {

    private val events = mutableListOf<Long>()
    private var pendingReturn = false

    /** Dipanggil Activity saat aplikasi kembali ke foreground. */
    var onReturn: (() -> Unit)? = null

    fun markInterrupted() {
        val now = System.currentTimeMillis()
        // Hindari double count dari perubahan fokus + lifecycle callback.
        if (events.isNotEmpty() && now - events.last() < 1000L) return
        events.add(now)
        pendingReturn = true
    }

    fun consumeReturn(): Long? {
        if (!pendingReturn) return null
        pendingReturn = false
        return events.lastOrNull()
    }

    fun count(): Int = events.size

    fun clear() {
        events.clear()
        pendingReturn = false
    }
}
