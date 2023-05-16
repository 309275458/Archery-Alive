package com.archeryalive.game.sdk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.annotation.ChecksSdkIntAtLeast
import com.archeryalive.game.R

/**
 * Description: 基类Webview
 *
 * @author: roy
 * Time: 2022/7/12 16:47
 * Modifier:
 * Fix Description:
 * Version:
 */
abstract class BaseWebView<T : WebJavaScriptIn>
@JvmOverloads constructor(c: Context, attrs: AttributeSet? = null, def: Int = 0) :
    WebView(c, attrs, if (def != 0) def else android.R.attr.webViewStyle) {

    companion object {
        private var cacheFileDir: String = ""

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
        private val isMultiProcessSuffix = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        private const val pn = "web-cache"
        private var isInitializedSuffix = hashMapOf<String, Boolean>()

        fun onAppAttached(c: Context, progressSuffix: String) {
            val appDir = c.cacheDir.absolutePath
            val progressName = getProgressName(c) ?: ""
            if (progressSuffix.isEmpty() || progressName.endsWith(progressSuffix)) {
                cacheFileDir = if (progressSuffix.isNotEmpty()) "$appDir/$pn$progressSuffix" else ""
                if (isMultiProcessSuffix) try {
                    if (isInitializedSuffix[progressName] == true) return
                    isInitializedSuffix[progressName] = true
                    setDataDirectorySuffix(progressSuffix)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    private var isRedirect = false
    private val webHandler = Handler(Looper.getMainLooper())
    open val javaScriptEnabled = true
    open val removeSessionAuto = false
    abstract val webDebugEnable: Boolean
    abstract val javaScriptClient: T

    private val mWebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.e("Roy","shouldOverrideUrlLoading is :$url")
            return super.shouldOverrideUrlLoading(view, url)
        }
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.e("Roy","shouldOverrideUrlLoading is :${request?.url}")
            return if (this@BaseWebView.shouldOverrideUrlLoading(
                    view,
                    request
                )
            ) true else super.shouldOverrideUrlLoading(view, request)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            this@BaseWebView.onLoadResource(view, url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            this@BaseWebView.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (isRedirect) {
                isRedirect = false
                return
            }
            val w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            val h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            measure(w, h)
            this@BaseWebView.onPageFinished(view, url)
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            Log.e("Roy","onReceivedHttpError is :${errorResponse}")
            this@BaseWebView.onReceivedHttpError(view, request, errorResponse)
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            Log.e("Roy","onReceivedSslError is :${error}")
            this@BaseWebView.onReceivedSslError(view, handler, error)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            this@BaseWebView.onReceivedError(view, request, error)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return this@BaseWebView.shouldInterceptRequest(view, request)
                ?: super.shouldInterceptRequest(view, request)
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            this@BaseWebView.onReceivedTitle(view, title)
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            this@BaseWebView.onProgressChanged(view, newProgress)
        }

        override fun getDefaultVideoPoster(): Bitmap? {
            return this@BaseWebView.getDefaultVideoPoster() ?: super.getDefaultVideoPoster()
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            consoleMessage?.message()?.let { Log.e("Roy", it) }
            return super.onConsoleMessage(consoleMessage)
        }
    }


    init {
        isEnabled = true
        isFocusable = true
        requestFocus()
        initWebSettings()
    }

    @SuppressLint("JavascriptInterface")
    @Suppress("DEPRECATION")
    private fun initWebSettings() {
        settings.let {
            setWebContentsDebuggingEnabled(webDebugEnable)
            it.javaScriptEnabled = javaScriptEnabled
            it.allowFileAccess = true
            it.allowFileAccessFromFileURLs = true


            it.builtInZoomControls = false
            it.displayZoomControls = false
            it.setSupportZoom(false)

            //setting the content automatic the app screen size
            it.useWideViewPort = true
            it.loadWithOverviewMode = true
            it.cacheMode = WebSettings.LOAD_DEFAULT
            it.domStorageEnabled = true
            it.databaseEnabled = true
            it.setAppCacheEnabled(true)
            webViewClient = mWebViewClient
            webChromeClient = mWebChromeClient

            if (cacheFileDir.isNotEmpty()) try {
                it.setAppCachePath(cacheFileDir)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //sync cookies
            CookieManager.getInstance().flush()

            it.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            if (removeSessionAuto) CookieManager.getInstance().removeSessionCookies(null)

            // inject different custom js bridge client
            webHandler.post {
                Log.e(
                    "Roy",
                    "javaScriptClient.name : ${javaScriptClient.name},javaScriptClient : ${javaScriptClient.javaClass.name}"
                )
                this.addJavascriptInterface(javaScriptClient, javaScriptClient.name)
            }
        }
    }

    override fun loadData(data: String, mimeType: String?, encoding: String?) {
        webHandler.post {
            kotlin.runCatching { super.loadData(data, mimeType, encoding) }
                .onFailure { onError(WebErrorType.THROW.throwError(it)) }
        }
    }

    override fun loadUrl(url: String) {
        webHandler.post {
            kotlin.runCatching {
                super.loadUrl(url)
            }.onFailure {
                onError(WebErrorType.THROW.throwError(it))
            }
        }
    }

    final override fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>) {
        webHandler.post {
            kotlin.runCatching { super.loadUrl(url, additionalHttpHeaders) }.onFailure {
                onError(WebErrorType.THROW.throwError(it))
            }
        }
    }


    final override fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        webHandler.post {
            kotlin.runCatching {
                super.loadDataWithBaseURL(
                    baseUrl,
                    data,
                    mimeType,
                    encoding,
                    historyUrl
                )
            }.onFailure {
                onError(WebErrorType.THROW.throwError(it))
            }
        }
    }

    final override fun reload() {
        webHandler.post { super.reload() }
    }

    open fun onPageFinished(view: WebView, url: String) {}

    open fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {}

    open fun onLoadResource(view: WebView?, url: String?) {}


    open fun shouldOverrideUrlLoading(webView: WebView?, request: WebResourceRequest?): Boolean {

        if (Build.VERSION.SDK_INT >= 24) {
            isRedirect = request?.isRedirect == true
        }

        val currentUrl = request?.url.toString()
        try {
            if (GpUtil.isGp(currentUrl)) {
                Log.e("Roy", "isGp url, goGp")
                GpUtil.redirectGp(webView?.context?.applicationContext, currentUrl)
//                finish()
            } else {
                if (SdkUtil.isAcceptedScheme(currentUrl)) {
                    webView?.loadUrl(currentUrl)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("Roy", "shouldOverrideUrlLoading error", e)
        }

        request?.let {
            return !(it.isForMainFrame || it.url.scheme?.startsWith("http") == true)
        }
        return false
    }


    open fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return null
    }

    open fun onReceivedTitle(view: WebView?, title: String?) {}

    open fun onProgressChanged(view: WebView?, newProgress: Int) {}

    open fun getDefaultVideoPoster(): Bitmap? {
        return null
    }

    open fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        onError(WebErrorType.HTTP_ERROR.onHttpError(errorResponse))
    }

    open fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        onError(WebErrorType.SSL_ERROR.onSSLError(error))
        if ((context as? Activity)?.isFinishing != false) return
        val builder = AlertDialog.Builder(context)
        builder.setTitle(android.R.string.dialog_alert_title)
        builder.setIcon(R.mipmap.icon_ssl_alert)
        builder.setMessage(R.string.ssl_error_hint)
        builder.setPositiveButton(R.string.ssl_proceed) { dialog, _ ->
            handler?.proceed()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.ssl_cancel) { dialog, _ ->
            handler?.cancel()
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    open fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        onError(WebErrorType.RESOURCE_ERROR.onResourceError(error))
    }


    open fun onError(type: WebErrorType) {
        val s = when (type) {
            WebErrorType.HTTP_ERROR -> type.httpError?.reasonPhrase
            WebErrorType.SSL_ERROR -> type.sslError?.url
            WebErrorType.RESOURCE_ERROR -> type.resourceError?.toString()
            WebErrorType.THROW -> type.throws?.message
        }
        Log.e("Roy", "${type.name} : desc = $s")
        WebLogUtils.log("${type.name} : desc = $s")
    }


    fun destroyWebView() {
        stopLoading()
        clearAnimation()
        removeJavascriptInterface(javaScriptClient.name)
        clearFormData()
        clearHistory()
        clearDisappearingChildren()
        removeAllViews()
        (parent as? ViewGroup)?.removeView(this)
        super.destroy()
    }

}
