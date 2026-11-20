package id.sch.smkn6muarojambi.exambrowser

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private enum class Status { IDLE, PENDING, OK, ERROR }

    private lateinit var prefs: android.content.SharedPreferences

    private lateinit var inputContainer: View
    private lateinit var inputAddress: EditText
    private lateinit var statusDotInput: View
    private lateinit var statusTextInput: TextView
    private lateinit var inputErrorBox: View
    private lateinit var inputErrorText: TextView

    private lateinit var browseContainer: View
    private lateinit var statusDotWeb: View
    private lateinit var statusTextWeb: TextView
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var warningText: TextView
    private lateinit var webErrorBox: View
    private lateinit var webErrorText: TextView

    private val checker = Executors.newSingleThreadExecutor()
    private var checking = false
    private var hideWarning: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FLAG_SECURE aktif sejak jendela dibuat sampai aplikasi ditutup.
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        if (!BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(false)
        }

        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        inputContainer = findViewById(R.id.input_container)
        inputAddress = findViewById(R.id.input_address)
        statusDotInput = findViewById(R.id.status_dot_input)
        statusTextInput = findViewById(R.id.status_text_input)
        inputErrorBox = findViewById(R.id.input_error_box)
        inputErrorText = findViewById(R.id.input_error_text)

        browseContainer = findViewById(R.id.browse_container)
        statusDotWeb = findViewById(R.id.status_dot_web)
        statusTextWeb = findViewById(R.id.status_text_web)
        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progress)
        warningText = findViewById(R.id.warning_text)
        webErrorBox = findViewById(R.id.web_error_box)
        webErrorText = findViewById(R.id.web_error_text)

        SessionMonitor.clear()
        SessionMonitor.onReturn = { runOnUiThread { showBackgroundWarning() } }

        inputAddress.setText(prefs.getString(KEY_ADDRESS, "") ?: "")
        inputAddress.setSelection(inputAddress.text.length)

        configureWebView()

        findViewById<Button>(R.id.button_connect).setOnClickListener { connectFromInput() }
        findViewById<Button>(R.id.button_input_retry).setOnClickListener { connectFromInput() }
        findViewById<Button>(R.id.button_input_change).setOnClickListener {
            hideInputError()
            inputAddress.requestFocus()
            inputAddress.selectAll()
        }

        findViewById<View>(R.id.button_reload).setOnClickListener {
            val active = CbtSession.current()
            if (active != null) {
                hideWebError()
                progressBar.visibility = View.VISIBLE
                webView.reload()
            }
        }
        findViewById<Button>(R.id.button_ganti_server).setOnClickListener { confirmChangeServer() }
        findViewById<Button>(R.id.button_web_change).setOnClickListener { confirmChangeServer() }
        findViewById<Button>(R.id.button_web_retry).setOnClickListener {
            CbtSession.current()?.let { connect(it, fromInput = false) }
        }

        hideSystemBars()
        setStatus(statusDotInput, statusTextInput, Status.IDLE)
    }

    @Suppress("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView.settings.apply {
            // CBT PHP memerlukan JavaScript dan penyimpanan lokal.
            javaScriptEnabled = true
            domStorageEnabled = true

            // Pengamanan WebView.
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            cacheMode = WebSettings.LOAD_NO_CACHE
            setGeolocationEnabled(false)
            mediaPlaybackRequiresUserGesture = true
            @Suppress("DEPRECATION")
            savePassword = false
        }

        @Suppress("DEPRECATION")
        applyLegacyRestrictions(webView.settings)

        // Tidak ada addJavascriptInterface -> tidak ada jembatan JavaScript.
        webView.setDownloadListener { _, _, _, _, _ -> }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return !CbtSession.isAllowed(request.url?.toString())
            }

            @Deprecated("Digunakan hanya untuk API 21-23")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return !CbtSession.isAllowed(url)
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                hideWebError()
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String?) {
                progressBar.visibility = View.GONE
                setStatus(statusDotWeb, statusTextWeb, Status.OK)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                if (request?.isForMainFrame == true) showWebError()
            }

            @Deprecated("Digunakan hanya untuk API 21-23")
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                showWebError()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress >= 100) progressBar.visibility = View.GONE
            }

            // Blokir popup / jendela baru yang bisa membuka situs lain.
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean = false
        }
    }

    /** Setelan lama yang masih diperlukan untuk perangkat Android lawas. */
    @Suppress("DEPRECATION")
    private fun applyLegacyRestrictions(settings: WebSettings) {
        settings.databaseEnabled = false
        settings.allowFileAccessFromFileURLs = false
        settings.allowUniversalAccessFromFileURLs = false
    }

    private fun connectFromInput() {
        val raw = inputAddress.text?.toString().orEmpty()
        if (raw.isBlank()) {
            showInputError(getString(R.string.error_empty_address))
            return
        }

        val address = CbtAddress.parse(raw)
        if (address == null) {
            showInputError(getString(R.string.error_invalid_address))
            return
        }

        connect(address, fromInput = true)
    }

    private fun connect(address: CbtAddress, fromInput: Boolean) {
        if (checking) return
        checking = true

        hideInputError()
        hideWebError()
        setStatus(statusDotInput, statusTextInput, Status.PENDING)
        setStatus(statusDotWeb, statusTextWeb, Status.PENDING)
        progressBar.visibility = View.VISIBLE

        checker.execute {
            val wifi = NetworkChecker.isWifiConnected(this@MainActivity)
            val reachable = wifi && NetworkChecker.isCbtServerReachable(address.host, address.port)

            runOnUiThread {
                checking = false
                if (reachable) {
                    CbtSession.set(address)
                    prefs.edit().putString(KEY_ADDRESS, address.url).apply()
                    progressBar.visibility = View.GONE
                    showBrowse()
                    setStatus(statusDotWeb, statusTextWeb, Status.OK)
                    webView.loadUrl(address.url)
                } else {
                    progressBar.visibility = View.GONE
                    setStatus(statusDotInput, statusTextInput, Status.ERROR)
                    setStatus(statusDotWeb, statusTextWeb, Status.ERROR)
                    if (fromInput) showInputError() else showWebError()
                }
            }
        }
    }

    private fun showBrowse() {
        inputContainer.visibility = View.GONE
        browseContainer.visibility = View.VISIBLE
        webView.visibility = View.VISIBLE
        hideWebError()
        hideSystemBars()
    }

    private fun confirmChangeServer() {
        AlertDialog.Builder(this)
            .setTitle(R.string.change_server_title)
            .setMessage(R.string.change_server_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.change) { _, _ -> returnToInput() }
            .show()
    }

    private fun returnToInput() {
        CbtSession.clear()
        webView.stopLoading()
        webView.loadUrl("about:blank")
        webView.clearHistory()

        browseContainer.visibility = View.GONE
        inputContainer.visibility = View.VISIBLE

        hideWebError()
        hideInputError()
        inputAddress.setText(prefs.getString(KEY_ADDRESS, "") ?: "")
        inputAddress.setSelection(inputAddress.text.length)
        setStatus(statusDotInput, statusTextInput, Status.IDLE)
        hideSystemBars()
    }

    private fun setStatus(dot: View, text: TextView, status: Status) {
        when (status) {
            Status.IDLE -> {
                dot.setBackgroundResource(R.drawable.dot_idle)
                text.setText(R.string.status_idle)
            }
            Status.PENDING -> {
                dot.setBackgroundResource(R.drawable.dot_pending)
                text.setText(R.string.status_checking)
            }
            Status.OK -> {
                dot.setBackgroundResource(R.drawable.dot_ok)
                text.setText(R.string.status_connected)
            }
            Status.ERROR -> {
                dot.setBackgroundResource(R.drawable.dot_error)
                text.setText(R.string.status_disconnected)
            }
        }
    }

    private fun showInputError(message: String? = null) {
        inputErrorText.text = message ?: getString(R.string.error_unreachable)
        inputErrorBox.visibility = View.VISIBLE
    }

    private fun hideInputError() {
        inputErrorBox.visibility = View.GONE
    }

    private fun showWebError() {
        webErrorText.setText(R.string.error_unreachable)
        webErrorBox.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun hideWebError() {
        webErrorBox.visibility = View.GONE
    }

    private fun showBackgroundWarning() {
        warningText.text = getString(R.string.warning_background, SessionMonitor.count())
        warningText.visibility = View.VISIBLE
        hideWarning?.let { warningText.removeCallbacks(it) }
        hideWarning = Runnable { warningText.visibility = View.GONE }.also {
            warningText.postDelayed(it, 4000L)
        }
    }

    private fun hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(
                    WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
                )
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
            if (SessionMonitor.consumeReturn() != null) showBackgroundWarning()
        } else {
            SessionMonitor.markInterrupted()
        }
    }

    @Deprecated("API lama; diperlukan agar tombol Back mengikuti riwayat CBT")
    override fun onBackPressed() {
        if (browseContainer.visibility == View.VISIBLE) {
            if (webErrorBox.visibility != View.VISIBLE && webView.canGoBack()) {
                webView.goBack()
                return
            }

            AlertDialog.Builder(this)
                .setTitle(R.string.exit_title)
                .setMessage(R.string.exit_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.exit) { _, _ -> finish() }
                .show()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        SessionMonitor.onReturn = null
        hideWarning?.let { warningText.removeCallbacks(it) }
        checker.shutdownNow()
        super.onDestroy()
    }

    private companion object {
        const val PREF_NAME = "exambrowser"
        const val KEY_ADDRESS = "cbt_address"
    }
}
